package ru.practicum.explore.utilits;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class AdminEventDateValidator implements ConstraintValidator<AdminEventDate, LocalDateTime> {
    @Override
    public void initialize(AdminEventDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        try {
            System.out.println(localDateTime);
            return localDateTime == null || localDateTime.isAfter(LocalDateTime.now().plusHours(1));
        } catch (Exception e) {
            return false;
        }
    }
}