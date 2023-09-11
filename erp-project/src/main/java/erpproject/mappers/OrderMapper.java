package erpproject.mappers;

import erpproject.dtos.CreateOrderCommand;
import erpproject.dtos.OrderDto;
import erpproject.model.Order;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    List<OrderDto> toDto(List<Order> orders);
    OrderDto toDto(Order order);
    CreateOrderCommand toCreateCommand(Order order);
}
