package com.steeplesoft.mailfilter.model.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 *
 * @author jason
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRuleValidator.class)
@Documented
public @interface ValidRule {
    String message() default "Validation errors";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}