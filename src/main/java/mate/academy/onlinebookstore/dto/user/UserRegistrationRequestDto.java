package mate.academy.onlinebookstore.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Data;
import mate.academy.onlinebookstore.model.Role;
import mate.academy.onlinebookstore.validation.FieldMatch;

@Data
@FieldMatch
@Schema(description = "Request DTO for a new user signing up.")
public class UserRegistrationRequestDto {
    private static final String ADDRESS =
            "^(\\d+\\s)?[A-Za-z]+\\s[A-Za-z]+,\\s[A-Za-z]+,\\s[A-Za-z]+$";

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email address format.")
    @Schema(description = "The user's email address. "
            + "This must be a valid email format (e.g., example@email.com).",
            example = "emily@email.com")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters.")
    @Schema(description = "The user's password. It must be between 8 and 20 characters.",
            example = "PaSsWorD789")
    private String password;

    @NotBlank(message = "Password confirmation cannot be blank.")
    @Size(min = 8, max = 20, message = "Password confirmation must be between 8 and 20 characters.")
    @Schema(description = "The user must re-enter the password to confirm it. "
            + "It should match the password field.",
            example = "PaSsWorD789")
    private String confirmPassword;

    @NotBlank(message = "First name cannot be blank.")
    @Schema(description = "The first name of the person who is going to register.",
            example = "Emily")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    @Schema(description = "The last name of the person who is going to register.",
            example = "Smith")
    private String lastName;

    @Pattern(regexp = ADDRESS, message = "The address should contain a street, city, and country.")
    @Schema(description = "The real address of the user, including the street, city, and country.",
            example = "123 Main St, City, Country")
    private String shippingAddress;

    @NotNull
    @Schema(description = "The role of the user. It must be either USER or ADMIN.",
            example = "USER")
    private Set<Role.RoleName> roles;
}
