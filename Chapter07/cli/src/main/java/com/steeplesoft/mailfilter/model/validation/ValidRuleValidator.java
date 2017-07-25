package com.steeplesoft.mailfilter.model.validation;

import com.steeplesoft.mailfilter.model.Rule;
import com.steeplesoft.mailfilter.model.RuleType;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 * @author jason
 */
public class ValidRuleValidator implements ConstraintValidator<ValidRule, Object> {
    @Override
    public void initialize(ValidRule constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext ctx) {
        if (value == null) {
            addViolation(ctx, "Null values are not considered valid Rules");
            return false;
        }
        boolean valid = true;
        if (value instanceof Collection) {
            for (Object o : ((Collection) value)) {
                valid &= validateRule(o, ctx);
            }
        } else {
            valid = validateRule(value, ctx);
        }
        return valid;
    }

    private boolean validateRule(Object value, ConstraintValidatorContext ctx) {
        if (!(value instanceof Rule)) {
            addViolation(ctx, "Constraint valid only on instances of Rule.");
            return false;
        }
        boolean valid = true;
        Rule rule = (Rule) value;

        if (rule.getType() == RuleType.MOVE) {
            valid &= validateNotBlank(ctx, rule.getDestFolder(), "A destination folder must be specified.");
        }
        if (!isBlank(rule.getMatchingText())) {
            valid &= validateFields(ctx, rule);
        } else if (rule.getOlderThan() != null) {
            if (rule.getOlderThan() <= 0) {
                addViolation(ctx, "The age must be greater than 0.");
                valid = false;
            }
        } else if (rule.getOlderThan() == null) {
            addViolation(ctx, "Either matchingText or olderThan must be specified.");
            valid = false;
        }

        return valid;
    }

    private void addViolation(ConstraintValidatorContext ctx, String message) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }

    private boolean validateFields(ConstraintValidatorContext ctx, Rule rule) {
        if (rule.getFields() == null || rule.getFields().isEmpty()) {
            addViolation(ctx, "Rules which specify a matching text must specify the field(s) to match on.");
            return false;
        }

        return true;
    }

    private boolean validateNotBlank(ConstraintValidatorContext ctx, String value, String message) {
        if (isBlank(value)) {
            addViolation(ctx, message);
            return false;
        }
        return true;
    }

    private boolean isBlank(String value) {
        return (value == null || (value.trim().isEmpty()));
    }
}
