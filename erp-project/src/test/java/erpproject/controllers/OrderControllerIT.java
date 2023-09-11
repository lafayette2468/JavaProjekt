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
class OrderControllerIT {
    @Autowired
    WebTestClient testClient;

    CreateCustomerCommand newCustomer;
    CreateOrderCommand newOrder;
    CustomerDto customer;
    OrderDto order;
    ProblemDetail problem;


    @BeforeEach
    void testInit() {
        newCustomer = new CreateCustomerCommand("ABC Kft.", "123", "1111", "Budapest", "Fő u 1.");
        newOrder = new CreateOrderCommand("2023/1111", LocalDate.parse("2023-01-01"), "kék toll", 300, 2);
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
    }

    @Test
    void testCreateOrder() {

        assertEquals("2023/1111", order.getOrderNumber());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertNotNull(order.getId());
    }

    @Test
    void testCreateOrderWithNonExistingCustomer() {
        ProblemDetail detail = testClient.post()
                .uri("/api/orders/{id}", customer.getId() + 1)
                .bodyValue(newOrder)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("customers/customer-not-found"), detail.getType());
    }

    @Test
    void testCreateOrderWithNullItemDescription() {
        problem = testClient.post()
                .uri("api/orders/{id}", customer.getId())
                .bodyValue(new CreateOrderCommand("2023/1234", LocalDate.parse("2023-01-01"), null, 300, 2))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), problem.getType());
        assertTrue(problem.getDetail().startsWith("Required field"));
    }


    @Test
    void testDeleteOrder() {
        CustomerWithOrderDto customerWithOrder = testClient.get()
                .uri("/api/customers/{id}", customer.getId())
                .exchange()
                .expectBody(CustomerWithOrderDto.class).returnResult().getResponseBody();

        assertEquals(1, customerWithOrder.getOrders().size());

        testClient.delete()
                .uri("/api/orders/{id}", customer.getId())
                .exchange()
                .expectStatus().isNoContent();

        customerWithOrder = testClient.get()
                .uri("/api/customers/{id}", customer.getId())
                .exchange()
                .expectBody(CustomerWithOrderDto.class).returnResult().getResponseBody();

        assertTrue(customerWithOrder.getOrders().isEmpty());

    }

    @Test
    void testDeleteOrderWhenAlreadyInvoiced() {

        CreateInvoiceCommand newInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("2024-01-01"), Vat.VAT_27, MethodOfPayment.CASH);
        testClient.post()
                .uri("/api/invoices/{id}", order.getId())
                .bodyValue(newInvoice)
                .exchange()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();


        ProblemDetail detail = testClient.delete()
                .uri("/api/orders/{id}", customer.getId())
                .exchange()
                .expectStatus().isEqualTo(405)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("orders/order-cannot-modify"), detail.getType());
    }

    @Test
    void testUpdateOrder() {
        CreateOrderCommand updatedOrder = new CreateOrderCommand("2023/1111", LocalDate.parse("2023-01-01"), "piros toll", 400, 2);

        OrderDto updatedSaved = testClient.put()
                .uri("api/orders/{id}", order.getId())
                .bodyValue(updatedOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();

        assertEquals(order.getId(), updatedSaved.getId());
        assertEquals(order.getOrderNumber(), updatedSaved.getOrderNumber());
        assertEquals("piros toll", updatedSaved.getItemDescription());
    }

    @Test
    void testUpdateOrderWithNegativeQuantity() {
        problem = testClient.put()
                .uri("api/orders/{id}", order.getId())
                .bodyValue(new UpdateOrderCommand("2023/1234", LocalDate.parse("2023-01-01"), "kék toll", 300, -2))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), problem.getType());
        assertTrue(problem.getDetail().startsWith("Must be positive."));
    }

    @Test
    void testGetUninvoicedOrders() {

        List<OrderDto> result = testClient.get()
                .uri("api/orders")
                .exchange()
                .expectBodyList(OrderDto.class).returnResult().getResponseBody();

        assertEquals(1, result.size());

        CreateInvoiceCommand newInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("2024-01-01"), Vat.VAT_27, MethodOfPayment.CASH);
        testClient.post()
                .uri("/api/invoices/{id}", order.getId())
                .bodyValue(newInvoice)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();

        result = testClient.get()
                .uri("api/orders")
                .exchange()
                .expectBodyList(OrderDto.class).returnResult().getResponseBody();
        assertEquals(0, result.size());

    }


}