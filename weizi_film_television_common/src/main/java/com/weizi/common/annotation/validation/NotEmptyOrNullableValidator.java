package com.weizi.common.annotation.validation;

import com.weizi.common.annotation.NotEmptyOrNullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyOrNullableValidator implements ConstraintValidator<NotEmptyOrNullable, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || !value.trim().isEmpty();
    }
}
