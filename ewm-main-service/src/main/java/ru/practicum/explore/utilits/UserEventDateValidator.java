package ru.practicum.explore.utilits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class UserEventDateValidator implements ConstraintValidator<UserEventDate, LocalDateTime> {
    private static final Logger logger = LoggerFactory.getLogger(AdminEventDateValidator.class);

    @Override
    public void initialize(UserEventDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        try {
            logger.info("Validating event date: {}", localDateTime);
            return localDateTime == null || localDateTime.isAfter(LocalDateTime.now().plusHours(2));
        } catch (Exception e) {
            logger.error("An error occurred while validating the event date", e);
            return false;
        }
    }
}

