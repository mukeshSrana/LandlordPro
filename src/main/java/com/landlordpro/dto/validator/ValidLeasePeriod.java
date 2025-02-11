package com.landlordpro.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = LeasePeriodValidator.class)
@Target(ElementType.TYPE)  // Apply this annotation at the class level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLeasePeriod {
    String message() default "Lease end date must be after lease start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

