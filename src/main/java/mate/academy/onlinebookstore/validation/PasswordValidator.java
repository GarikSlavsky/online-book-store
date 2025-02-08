package mate.academy.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto;

public class PasswordValidator
        implements ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        return Objects.equals(dto.getPassword(), dto.getConfirmPassword());
    }
}
