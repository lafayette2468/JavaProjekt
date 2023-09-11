package erpproject.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderWithInvoiceDto {

    private Long id;
    private String orderNumber;
    private LocalDate orderDate;
    private String itemDescription;
    private int netUnitPrice;
    private int quantity;
    private InvoiceDto invoice;
}
