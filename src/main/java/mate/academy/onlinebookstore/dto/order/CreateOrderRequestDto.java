package mate.academy.onlinebookstore.dto.order;

import static mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto.ADDRESS;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "Request DTO for creating a new order.")
public class CreateOrderRequestDto {
    @Pattern(regexp = ADDRESS, message = "The address should contain a street, city, and country.")
    @Schema(description = "The real address of the user, including the street, city, and country.",
            example = "123 Main St, City, Country")
    private String shippingAddress;
}
