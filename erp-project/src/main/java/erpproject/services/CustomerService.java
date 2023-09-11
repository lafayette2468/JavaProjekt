package erpproject.services;

import erpproject.dtos.CreateCustomerCommand;
import erpproject.dtos.CustomerDto;
import erpproject.dtos.CustomerWithOrderDto;
import erpproject.dtos.UpdateCustomerCommand;
import erpproject.exceptions.CustomerNotFoundException;
import erpproject.mappers.CustomerMapper;
import erpproject.model.Customer;
import erpproject.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public List<CustomerDto> getAllCustomers(Optional<String> customerName) {
        List<Customer> customers;

        if (customerName.isPresent()) {
            customers = repository.findCustomerByNameContains(customerName.get());
        } else {
            customers = repository.findAll();
        }
        return mapper.toDto(customers);

    }

    public CustomerDto createCustomer(CreateCustomerCommand command) {
        Customer customer = Customer.builder()
                .name(command.getName())
                .taxNumber(command.getTaxNumber())
                .zipCode(command.getZipCode())
                .city(command.getCity())
                .address(command.getAddress())
                .build();
        repository.save(customer);
        return mapper.toDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(long id, UpdateCustomerCommand command) {
        Customer found = repository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
        found.setName(command.getName());
        found.setTaxNumber(command.getTaxNumber());
        found.setZipCode(command.getZipCode());
        found.setCity(command.getCity());
        found.setAddress(command.getAddress());
        return mapper.toDto(found);

    }

    public CustomerWithOrderDto getCustomerWithOrdersAndInvoices(long id) {
        Customer found = repository.findCustomerWithOrders(id).orElseThrow(() -> new CustomerNotFoundException(id));
        return mapper.toDtoWithOrder(found);
    }
}
