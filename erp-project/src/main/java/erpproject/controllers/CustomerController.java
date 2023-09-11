package erpproject.controllers;

import erpproject.dtos.CreateCustomerCommand;
import erpproject.dtos.CustomerDto;
import erpproject.dtos.CustomerWithOrderDto;
import erpproject.dtos.UpdateCustomerCommand;
import erpproject.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/customers")
@AllArgsConstructor
@Tag(name = "Operations on customers")
public class CustomerController {
    private CustomerService service;


    //1. Vevő POST Új vevő rögzítése
    @PostMapping
    @Operation(summary = "Create a new customer")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createCustomer(@Valid @RequestBody CreateCustomerCommand command) {
        return service.createCustomer(command);
    }

    //2. Vevő PUT Vevő adatainak módosítása
    @PutMapping("/{id}")
    @Operation(summary = "Update a customer's data")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto updateCustomer(@PathVariable("id") long id, @Valid @RequestBody UpdateCustomerCommand command) {
        return service.updateCustomer(id, command);
    }


    //3.  Vevő GET Összes vevő lekérdezése
    @GetMapping
    @Operation(summary = "Get all the customers")
    public List<CustomerDto> getAllCustomers(@RequestParam Optional<String> customerName) {
        return service.getAllCustomers(customerName);
    }

    //4. Vevő GET Vevő lekérdezése id alapján - mellé megrendelés és számla lista
    @GetMapping("/{id}")
    @Operation(summary = "Get a customer by id with all the orders and invoices")
    public CustomerWithOrderDto getCustomerWithOrdersAndInvoices(@PathVariable("id") long id) {
        return service.getCustomerWithOrdersAndInvoices(id);
    }
}
