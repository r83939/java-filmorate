package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        if (login.contains(" ")) {
            return false;
        }
        return true;
    }
}
