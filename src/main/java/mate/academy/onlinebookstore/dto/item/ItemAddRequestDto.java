package mate.academy.onlinebookstore.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Request DTO for a new item to add into a shopping cart.")
public class ItemAddRequestDto {
    @NotNull(message = "Book ID cannot be null.")
    @Positive(message = "Book ID must be a positive number.")
    @Digits(integer = 6, fraction = 0,
            message = "Book ID must be a numeric value with up to 6 digits.")
    @Schema(description = "The ID of a book that is to be added to the shopping cart.",
            example = "123")
    private Long bookId;

    @Positive(message = "Quantity must be a positive number.")
    @Digits(integer = 2, fraction = 0,
            message = "Quantity must be a numeric value with up to 2 digits.")
    @Schema(description = "The number of books to be added to the shopping cart.",
            example = "2")
    private int quantity;
}
