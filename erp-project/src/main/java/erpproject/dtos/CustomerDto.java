package erpproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerDto {

    private Long id;
    private String name;
    private String taxNumber;
    private String zipCode;
    private String city;
    private String address;
}
