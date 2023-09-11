package erpproject.dtos;

import erpproject.model.MethodOfPayment;
import erpproject.model.Vat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate fulfilmentDate;
    private LocalDate dueDate;
    private Vat vat;
    private MethodOfPayment methodOfPayment;
    private OrderDto order;
}
