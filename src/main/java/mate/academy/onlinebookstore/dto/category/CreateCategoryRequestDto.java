package mate.academy.onlinebookstore.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request DTO for creating a new category.")
public class CreateCategoryRequestDto {
    private static final String NAME_PATTERN = "^[A-Z][a-z]*([\\s\\w]*)$";

    @NotBlank
    @Pattern(regexp = NAME_PATTERN,
            message = "The text must start with an uppercase letter, "
                    + "and the subsequent words can be in any case.")
    @Schema(description = "The name of the category.", example = "Detective")
    private String name;

    @NotBlank(message = "Please enter a meaningful description.")
    @Size(min = 10, max = 250, message = "The description should be no longer than 250 characters.")
    @Schema(description = "A brief description of the categories' content",
            example = "A comprehensive guide to best practices in Java programming.")
    private String description;
}
