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
class CustomerControllerIT {
    @Autowired
    WebTestClient testClient;

    CreateCustomerCommand newCustomer;
    CustomerDto customer;
    ProblemDetail problem;


    @BeforeEach
    void testInit() {
        newCustomer = new CreateCustomerCommand("ABC Kft.", "123", "1111", "Budapest", "Fő u 1.");

        customer = testClient.post()
                .uri("api/customers")
                .bodyValue(newCustomer)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDto.class).returnResult().getResponseBody();
    }


    @Test
    void testCreateCustomer() {

        assertEquals("ABC Kft.", customer.getName());
        assertEquals("123", customer.getTaxNumber());
        assertEquals("Budapest", customer.getCity());
        assertNotNull(customer.getId());

    }

    @Test
    void testCreateCustomerWithoutTaxNumber(){
        problem = testClient.post()
                .uri("api/customers")
                .bodyValue(new CreateCustomerCommand("ABC Kft.", "", "1111", "Budapest", "Fő u 1."))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), problem.getType());
        assertTrue(problem.getDetail().startsWith("Required field"));
    }

    @Test
    void testUpdateCustomer() {
        UpdateCustomerCommand updated = new UpdateCustomerCommand("BBB Kft.", "123", "1234", "Budapest", "Mellék u 2.");

        CustomerDto updatedSaved = testClient.put()
                .uri("/api/customers/{id}", customer.getId())
                .bodyValue(updated)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDto.class).returnResult().getResponseBody();

        assertEquals(customer.getId(), updatedSaved.getId());
        assertEquals(customer.getTaxNumber(), updatedSaved.getTaxNumber());
        assertEquals("BBB Kft.", updatedSaved.getName());
    }

    @Test
    void testUpdateCustomerWithoutName(){
        problem = testClient.put()
                .uri("api/customers/{id}", customer.getId())
                .bodyValue(new UpdateCustomerCommand("", "123", "1234", "Budapest", "Mellék u 2."))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult().getResponseBody();

        assertEquals(URI.create("validation/not-valid"), problem.getType());
        assertTrue(problem.getDetail().startsWith("Required field"));
    }

    @Test
    void testInvalidUpdate(){
        UpdateCustomerCommand updated = new UpdateCustomerCommand("BBB Kft.", "123", "1234", "Budapest", "Mellék u 2.");

        ProblemDetail detail = testClient.put()
                .uri("/api/customers/{id}",customer.getId()+1)
                .bodyValue(updated)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("customers/customer-not-found"),detail.getType());

    }

    @Test
    void testGetAllCustomers() {

        testClient.post()
                .uri("api/customers")
                .bodyValue(newCustomer)
                .exchange();
        CreateCustomerCommand secondNewCustomer = new CreateCustomerCommand("CCC Kft.", "123", "1111", "Budapest", "Fő u 1.");

        testClient.post()
                .uri("api/customers")
                .bodyValue(secondNewCustomer)
                .exchange();

        List<CustomerDto> result = testClient.get()
                .uri("/api/customers")
                .exchange()
                .expectBodyList(CustomerDto.class).returnResult().getResponseBody();

        assertEquals(3, result.size());

        result = testClient.get()
                .uri("/api/customers?customerName=a")
                .exchange()
                .expectBodyList(CustomerDto.class).returnResult().getResponseBody();

        assertEquals(2, result.size());
    }

    @Test
    void testGetCustomerWithOrdersAndInvoices() {

        CreateOrderCommand newOrder = new CreateOrderCommand("2023/1111", LocalDate.parse("2023-01-01"), "kék toll", 300, 2);
        CreateInvoiceCommand newInvoice = new CreateInvoiceCommand("2023/0001234", LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-01"), LocalDate.parse("20246-01-01"), Vat.VAT_27, MethodOfPayment.CASH);

        OrderDto order = testClient.post()
                .uri("/api/orders/{id}", customer.getId())
                .bodyValue(newOrder)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(OrderDto.class).returnResult().getResponseBody();

        InvoiceDto invoice = testClient.post()
                .uri("/api/invoices/{id}", order.getId())
                .bodyValue(newInvoice)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(InvoiceDto.class).returnResult().getResponseBody();

        CustomerWithOrderDto result = testClient.get()
                .uri("/api/customers/{id}", customer.getId())
                .exchange()
                .expectBody(CustomerWithOrderDto.class).returnResult().getResponseBody();

        assertEquals("1111", result.getZipCode());
        assertEquals("2023/0001234", result.getOrders().get(0).getInvoice().getInvoiceNumber());
    }

    @Test
    void testGetNotExistingCustomer(){
        ProblemDetail detail = testClient.get()
                .uri("/api/customers/{id}",customer.getId()+1)
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectBody(ProblemDetail.class).returnResult().getResponseBody();

        assertEquals(URI.create("customers/customer-not-found"),detail.getType());

    }
}