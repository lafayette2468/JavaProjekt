package erpproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateCustomerCommand {

    @NotBlank(message = "Required field")
    @Schema(description = "Name of the customer", example = "ABC Kft.")
    private String name;
    @NotBlank(message = "Required field")
    @Schema(description = "Tax number", example = "12345678-2-41")
    private String taxNumber;
    @NotBlank(message = "Required field")
    @Schema(description = "Zip code", example = "1111")
    private String zipCode;
    @NotBlank(message = "Required field")
    @Schema(description = "City", example = "Budapest")
    private String city;
    @NotBlank(message = "Required field")
    @Schema(description = "Customer's street and house number", example = "Fő u. 1.")
    private String address;


}
