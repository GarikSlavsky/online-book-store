package mate.academy.onlinebookstore.dto.book;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    private static final String ISBN_PATTERN = "\\d{3}-\\d{10}";
    private static final String TITLE_PATTERN = "^[A-Z][a-z]*([\\s\\w]*)$";
    private static final String AUTHOR_PATTERN = "\\b([A-Z][a-z]*)\\b(\\s\\b([A-Z][a-z]*)\\b)*";

    @NotBlank
    @Pattern(regexp = TITLE_PATTERN,
            message = "The text must start with an uppercase letter, "
                    + "and the subsequent words can be in any case.")
    private String title;

    @NotBlank
    @Pattern(regexp = AUTHOR_PATTERN,
            message = "Each word must start with an uppercase letter.")
    private String author;

    @NotBlank
    @Pattern(regexp = ISBN_PATTERN,
            message = "Invalid ISBN format. The correct format should be three digits, "
                    + "followed by a hyphen, and then ten digits (e.g., 123-4567890123).")
    private String isbn;

    @NotNull
    @Positive
    @Digits(integer = 6, fraction = 2,
            message = "Invalid price property. "
                    + "The value should be a number with up to 6 digits before the decimal point "
                    + "and up to 2 digits after the decimal point (e.g., 999999.00).")
    private BigDecimal price;

    @NotBlank(message = "Please enter a meaningful description.")
    @Size(min = 10, max = 250, message = "The description should be no longer than 250 characters.")
    private String description;

    private String coverImage;
}
