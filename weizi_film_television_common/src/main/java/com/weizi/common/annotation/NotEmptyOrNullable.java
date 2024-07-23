package com.weizi.common.annotation;

import com.weizi.common.annotation.validation.NotEmptyOrNullableValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEmptyOrNullableValidator.class)
public @interface NotEmptyOrNullable {
    String message() default "字段必须为非空字符串或null";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
