package erpproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateOrderCommand {

    @NotBlank(message = "Required field")
    @Schema(description="Order number", example ="2023/999999")
    private String orderNumber;
    @Schema(description="Order date", example ="2023-01-01")
    private LocalDate orderDate;
    @NotBlank(message = "Required field")
    @Schema(description="item description", example ="piros ceruza")
    private String itemDescription;
    @Schema(description="Net unit price", example ="300")
    private int netUnitPrice;
    @Positive(message = "Must be positive.")
    @Schema(description="Quantity", example ="3")
    private int quantity;
}
