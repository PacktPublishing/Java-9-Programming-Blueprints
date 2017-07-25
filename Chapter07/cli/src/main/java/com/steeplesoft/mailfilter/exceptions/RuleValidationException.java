package com.steeplesoft.mailfilter.exceptions;

import com.steeplesoft.mailfilter.model.Rule;
import java.util.Set;
import javax.validation.ConstraintViolation;

/**
 *
 * @author jason
 */
public class RuleValidationException extends MailFilterException {
    private final Set<ConstraintViolation<Rule>> violations;

    public RuleValidationException(Set<ConstraintViolation<Rule>> violations) {
        super();
        this.violations = violations;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder("Rule validation error(s) occurred:");
        for (ConstraintViolation violation : violations) {
            message.append("\n\t").append(violation.getMessage());
        }
        
        return message.toString();
    }
}
