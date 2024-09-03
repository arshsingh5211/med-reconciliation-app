package com.arsh.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BrandOrGenericValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BrandOrGeneric {
    String message() default "Either brandName or genericName must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
