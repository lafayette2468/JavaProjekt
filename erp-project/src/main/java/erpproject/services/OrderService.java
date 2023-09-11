package erpproject.services;

import erpproject.dtos.CreateOrderCommand;
import erpproject.dtos.OrderDto;
import erpproject.dtos.UpdateOrderCommand;
import erpproject.exceptions.*;
import erpproject.mappers.OrderMapper;
import erpproject.model.Customer;
import erpproject.model.Order;
import erpproject.repository.CustomerRepository;
import erpproject.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerRepository customerRepository;
    private final OrderMapper mapper;

    public OrderDto createOrder(long customerId, CreateOrderCommand command) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(customerId));
        Order order = Order.builder()
                .orderNumber(command.getOrderNumber())
                .orderDate(command.getOrderDate())
                .itemDescription(command.getItemDescription())
                .netUnitPrice(command.getNetUnitPrice())
                .quantity(command.getQuantity())
                .customer(customer)
                .build();
        repository.save(order);
        return mapper.toDto(order);
    }

    @Transactional
    public OrderDto createOrderForCreditNote(long invoiceId) {
        Order original = repository.findOrderByInvoiceId(invoiceId).orElseThrow(() -> new InvoiceNotFoundException(invoiceId));
        String creditedOrderNumber = "C/" + original.getOrderNumber();
        if (repository.findOrderByOrderNumber(creditedOrderNumber).isPresent()) {
            throw new CannotCreateInvoiceException();
        }
        CreateOrderCommand commandForCreditedOrder = mapper.toCreateCommand(original);
        commandForCreditedOrder.setOrderNumber(creditedOrderNumber);
        commandForCreditedOrder.setNetUnitPrice(original.getNetUnitPrice() * -1);
        return createOrder(original.getCustomer().getId(), commandForCreditedOrder);
    }

    @Transactional
    public void deleteOrder(long id) {
        Order order = findOrder(id);
        if (hasOrderInvoice(id)) {
            throw new CannotModifyOrderException(id);
        }
        repository.delete(order);
    }

    @Transactional
    public OrderDto updateOrder(long id, UpdateOrderCommand command) {
        Order order = findOrder(id);
        if (hasOrderInvoice(id)) {
            throw new CannotModifyOrderException(id);
        }
        order.setOrderNumber(command.getOrderNumber());
        order.setOrderDate(command.getOrderDate());
        order.setItemDescription(command.getItemDescription());
        order.setNetUnitPrice(command.getNetUnitPrice());
        order.setQuantity(command.getQuantity());
        return mapper.toDto(order);
    }

    public List<OrderDto> getUninvoicedOrders() {
        return mapper.toDto(repository.getUninvoicedOrders());
    }

    private Order findOrder(long id) {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    private boolean hasOrderInvoice(long id) {
        return findOrder(id).getInvoice() != null;
    }


}

