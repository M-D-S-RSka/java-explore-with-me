package ru.practicum.explore.utilits;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserEventDateValidator.class)
public @interface UserEventDate {

    String message() default "Validation exception: The date and time on which the event is scheduled cannot be earlier than two hours from the current moment";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}