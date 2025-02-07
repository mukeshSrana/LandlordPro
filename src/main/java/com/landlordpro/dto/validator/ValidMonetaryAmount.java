package com.landlordpro.dto.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// This annotation will be used to validate the MonetaryAmount
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MonetaryAmountValidator.class)
public @interface ValidMonetaryAmount {
    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
