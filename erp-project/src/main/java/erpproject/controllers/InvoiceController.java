package erpproject.controllers;

import erpproject.dtos.CreateInvoiceCommand;
import erpproject.dtos.InvoiceDto;
import erpproject.services.InvoiceService;
import erpproject.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/invoices")
@AllArgsConstructor
@Tag(name="Operations on invoices")
public class InvoiceController {
    private InvoiceService invoiceService;
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "List all the invoices")
    public List<InvoiceDto> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @PostMapping("/{orderId}")
    @Operation(summary = "Create an invoice to an order")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createInvoice(@PathVariable long orderId, @Valid @RequestBody CreateInvoiceCommand command) {
        return invoiceService.createInvoice(orderId,command);
    }

    @PostMapping("/credit-note/{invoiceId}")
    @Operation(summary = "Create a credit note")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createCreditNote(@PathVariable long invoiceId) {
        long orderId= orderService.createOrderForCreditNote(invoiceId).getId();
        return invoiceService.createCreditNote(invoiceId, orderId);
    }



}
