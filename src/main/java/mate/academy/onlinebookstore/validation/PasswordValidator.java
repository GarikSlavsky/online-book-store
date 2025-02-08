package mate.academy.onlinebookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.onlinebookstore.dto.user.UserRegistrationRequestDto;

public class PasswordValidator
        implements ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    @Override
    public boolean isValid(UserRegistrationRequestDto dto, ConstraintValidatorContext context) {
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
