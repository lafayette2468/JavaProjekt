package erpproject.controllers;

import erpproject.dtos.*;
import erpproject.model.MethodOfPayment;
import erpproject.model.Vat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"delete from invoices", "delete from orders", "delete from customers"})
class InvoiceControllerIT {

    @Autowired
    WebTestClient testClient;

    CreateCustomerCommand newCustomer;
    CreateOrderCommand newOrder;
    CreateInvoiceCommand newInvoice;
    CustomerDto customer;
    OrderDto order;
    InvoiceDto invoice;

    @BeforeEach
    void testInit() {
        newCustomer = new CreateCustomerCommand("ABC Kft.", "123", "1111", "Budapest", "Fő u 1.");
        newOrder = new CreateOrderCommand("2023/1111", LocalDate.parse("2023-01-01"), "kék toll", 300, 2);
        newInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("2024-01-01"), Vat.VAT_27, MethodOfPayment.CASH);
        customer = testClient.post()
                .uri("api/customers")
                .bodyValue(newCustomer)
                .exchange()
                .expectBody(CustomerDto.class).returnResult().getResponseBody();
        order = testClient.post()
                .uri("/api/orders/{id}", customer.getId())
                .bodyValue(newOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();
        invoice = testClient.post()
                .uri("/api/invoices/{id}", order.getId())
                .bodyValue(newInvoice)
                .exchange()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();

    }

    @Test
    void testCreateInvoice() {

        assertEquals("2023/0001234", invoice.getInvoiceNumber());
        assertEquals(order.getId(), invoice.getOrder().getId());
        assertNotNull(invoice.getId());

    }

    @Test
    void testCreateInvoiceWithNonExistingOrder() {
        ProblemDetail detail = testClient.post()
                .uri("/api/invoices/{id}", order.getId() + 1)
                .bodyValue(newInvoice)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("orders/order-not-found"), detail.getType());
        assertTrue(detail.getDetail().startsWith("Order not found with"));
    }

    @Test
    void testCreateInvoiceForAlreadyInvoicedOrder() {

        CreateInvoiceCommand secondNewInvoice = new CreateInvoiceCommand("2023/0001235", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("2024-01-01"), Vat.VAT_27, MethodOfPayment.CASH);

        ProblemDetail detail = testClient.post()
                .uri("/api/invoices/{id}", order.getId())
                .bodyValue(secondNewInvoice)
                .exchange()
                .expectStatus().isEqualTo(405)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("invoices/invoice-cannot-create"), detail.getType());
        assertTrue(detail.getDetail().startsWith("Invoice already created or credited."));
    }

    @Test
    void testCreateInvoiceWithNullFulfilmentDate() {

        CreateOrderCommand anotherNewOrder = new CreateOrderCommand("2023/9999", LocalDate.parse("2023-01-01"), "kék toll", 300, 2);
        CreateInvoiceCommand anotherNewInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), null, LocalDate.parse("2024-01-01"), Vat.VAT_27, MethodOfPayment.CASH);
        OrderDto anotherOrder = testClient.post()
                .uri("/api/orders/{id}", customer.getId())
                .bodyValue(anotherNewOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();
        ProblemDetail detail = testClient.post()
                .uri("/api/invoices/{id}", anotherOrder.getId())
                .bodyValue(anotherNewInvoice)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), detail.getType());
        assertTrue(detail.getDetail().startsWith("Required field"));
    }

    @Test
    void testCreateInvoiceWithPastDueDate() {

        CreateOrderCommand anotherNewOrder = new CreateOrderCommand("2023/9999", LocalDate.parse("2023-01-01"), "kék toll", 300, 2);
        CreateInvoiceCommand anotherNewInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), Vat.VAT_27, MethodOfPayment.CASH);
        OrderDto anotherOrder = testClient.post()
                .uri("/api/orders/{id}", customer.getId())
                .bodyValue(anotherNewOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();
        ProblemDetail detail = testClient.post()
                .uri("/api/invoices/{id}", anotherOrder.getId())
                .bodyValue(anotherNewInvoice)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), detail.getType());
        assertTrue(detail.getDetail().startsWith("Due date cannot be in the past."));
    }


    @Test
    void testGetAllInvoices() {
        CreateOrderCommand secondNewOrder = newOrder;
        secondNewOrder.setOrderNumber("2023/1112");
        OrderDto secondOrder = testClient.post()
                .uri("/api/orders/{id}", customer.getId())
                .bodyValue(secondNewOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();
        CreateInvoiceCommand secondNewInvoice = newInvoice;
        secondNewInvoice.setInvoiceNumber("2023/0001235");

        testClient.post()
                .uri("/api/invoices/{id}", secondOrder.getId())
                .bodyValue(newInvoice)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();


        List<InvoiceDto> result = testClient.get()
                .uri("api/invoices")
                .exchange()
                .expectBodyList(InvoiceDto.class).returnResult().getResponseBody();
        assertEquals(2, result.size());

    }

    @Test
    void testCreateCreditNote() {

        InvoiceDto creditNote = testClient.post()
                .uri("/api/invoices/credit-note/{id}", invoice.getId())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();

        assertEquals("C/" + invoice.getInvoiceNumber(), creditNote.getInvoiceNumber());
        assertEquals(invoice.getDueDate(), creditNote.getDueDate());
        assertEquals(order.getNetUnitPrice() * -1, creditNote.getOrder().getNetUnitPrice());

    }

    @Test
    void testCreateCreditNoteWithNonExistingInvoice() {
        ProblemDetail detail = testClient.post()
                .uri("/api/invoices/credit-note/{id}", invoice.getId() + 1)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("invoices/invoice-not-found"), detail.getType());
    }


}