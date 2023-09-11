package erpproject.mappers;

import erpproject.dtos.CustomerDto;
import erpproject.dtos.CustomerWithOrderDto;
import erpproject.model.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    List<CustomerDto> toDto(List<Customer> customers);
    CustomerDto toDto(Customer customer);
    CustomerWithOrderDto toDtoWithOrder(Customer customer);


}
