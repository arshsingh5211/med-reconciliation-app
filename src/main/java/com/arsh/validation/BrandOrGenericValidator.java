package com.arsh.validation;

import com.arsh.model.Medication;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BrandOrGenericValidator implements ConstraintValidator<BrandOrGeneric, Medication> {

    @Override
    public void initialize(BrandOrGeneric constraintAnnotation) {
    }

    @Override
    public boolean isValid(Medication medication, ConstraintValidatorContext context) {
        return medication.getBrandName() != null || medication.getGenericName() != null;
    }
}
