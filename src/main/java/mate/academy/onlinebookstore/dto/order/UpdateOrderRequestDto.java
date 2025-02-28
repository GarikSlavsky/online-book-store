package mate.academy.onlinebookstore.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import mate.academy.onlinebookstore.model.Order;

@Data
@Schema(description = "Request DTO for updating an order.")
public class UpdateOrderRequestDto {

    @NotNull(message = "Status cannot be blank.")
    @Schema(description = "Actual status of the order.",
            example = "IN_PROGRESS")
    private Order.Status status;
}
