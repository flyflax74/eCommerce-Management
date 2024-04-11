package com.ecommerce.site.shop.annotation;

import com.ecommerce.site.shop.validation.PasswordStrengthValidator;
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
