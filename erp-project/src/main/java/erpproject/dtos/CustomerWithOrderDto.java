package erpproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerWithOrderDto {

    private Long id;
    private String name;
    private String taxNumber;
    private String zipCode;
    private String city;
    private String address;
    private List<OrderWithInvoiceDto> orders;
}
