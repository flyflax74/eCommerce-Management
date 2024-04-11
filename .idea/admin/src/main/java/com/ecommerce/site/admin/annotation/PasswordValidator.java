package com.ecommerce.site.admin.annotation;

import com.ecommerce.site.admin.validation.PasswordStrengthValidator;
import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidator {

    String message() default "Please choose a strong password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
