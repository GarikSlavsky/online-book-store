package mate.academy.onlinebookstore.dto.book;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Schema(description = "Request DTO for creating a new book")
public class CreateBookRequestDto {
    private static final String ISBN_PATTERN = "\\d{3}-\\d{10}";
    private static final String TITLE_PATTERN = "^[A-Z][a-z]*([\\s\\w]*)$";
    private static final String AUTHOR_PATTERN = "\\b([A-Z][a-z]*)\\b(\\s\\b([A-Z][a-z]*)\\b)*";

    @NotBlank
    @Pattern(regexp = TITLE_PATTERN,
            message = "The text must start with an uppercase letter, "
                    + "and the subsequent words can be in any case.")
    @Schema(description = "The title of the book", example = "Effective Java")
    private String title;

    @NotBlank
    @Pattern(regexp = AUTHOR_PATTERN,
            message = "Each word must start with an uppercase letter.")
    @Schema(description = "The author of the book", example = "Joshua Bloch")
    private String author;

    @NotBlank
    @Pattern(regexp = ISBN_PATTERN,
            message = "Invalid ISBN format. The correct format should be three digits, "
                    + "followed by a hyphen, and then ten digits (e.g., 123-4567890123).")
    @Schema(description = "The ISBN number of the book", example = "978-0134685991")
    private String isbn;

    @NotNull
    @Positive
    @Digits(integer = 6, fraction = 2,
            message = "Invalid price property. "
                    + "The value should be a number with up to 6 digits before the decimal point "
                    + "and up to 2 digits after the decimal point (e.g., 999999.00).")
    @Schema(description = "The price of the book", example = "49.99")
    private BigDecimal price;

    @NotBlank(message = "Please enter a meaningful description.")
    @Size(min = 10, max = 250, message = "The description should be no longer than 250 characters.")
    @Schema(description = "A brief description of the book's content",
            example = "A comprehensive guide to best practices in Java programming.")
    private String description;

    @Schema(description = "URL to the cover image of the book",
            example = "http://example.com/images/book-cover.jpg")
    private String coverImage;
}
