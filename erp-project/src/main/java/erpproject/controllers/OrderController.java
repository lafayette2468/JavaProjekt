package erpproject.controllers;

import erpproject.dtos.*;
import erpproject.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@AllArgsConstructor
@Tag(name="Operations on orders")
public class OrderController {
    private OrderService service;

    @PostMapping("/{customerId}")
    @Operation(summary = "Create a new order of a customer")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@PathVariable long customerId, @Valid @RequestBody CreateOrderCommand command) {
        return service.createOrder(customerId, command);
    }
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Delete an uninvoiced order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable long orderId){
        service.deleteOrder(orderId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an uninvoiced order")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto updateOrder(@PathVariable long id, @Valid @RequestBody UpdateOrderCommand command) {
        return service.updateOrder(id, command);
    }

    @GetMapping
    @Operation(summary = "List all the uninvoiced orders")
    public List<OrderDto> getUninvoicedOrders() {
        return service.getUninvoicedOrders();
    }

}
