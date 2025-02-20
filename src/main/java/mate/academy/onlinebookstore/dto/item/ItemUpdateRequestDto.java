package mate.academy.onlinebookstore.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Request DTO for updating an existing item within the shopping cart.")
public class ItemUpdateRequestDto {

    @Positive(message = "Quantity must be a positive number.")
    @Digits(integer = 2, fraction = 0,
            message = "Quantity must be a numeric value with up to 2 digits.")
    @Schema(description = "The number of books to be added to the shopping cart.",
            example = "2")
    private int quantity;
}
