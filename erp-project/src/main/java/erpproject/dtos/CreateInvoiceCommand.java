package erpproject.dtos;

import erpproject.model.MethodOfPayment;
import erpproject.model.Vat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateInvoiceCommand {

    @NotBlank(message = "Required field")
    @Schema(description="Number of the invoice", example = "2023/01234")
    private String invoiceNumber;
    @NotNull(message = "Required field")
    @Schema(description="Date of the issue", example ="2023-01-01")
    private LocalDate invoiceDate;
    @NotNull(message = "Required field")
    @Schema(description="Date of the fulfilment", example ="2023-01-01")
    private LocalDate fulfilmentDate;
    @NotNull(message = "Required field")
    @Schema(description="Due date", example ="2024-01-01")
    @FutureOrPresent(message = "Due date cannot be in the past.")
    private LocalDate dueDate;
    @Schema(description="Value added tax on the item", example ="VAT_27")
    private Vat vat;
    @Schema(description="Method of the payment", example ="BANK_TRANSFER")
    private MethodOfPayment methodOfPayment;


}
