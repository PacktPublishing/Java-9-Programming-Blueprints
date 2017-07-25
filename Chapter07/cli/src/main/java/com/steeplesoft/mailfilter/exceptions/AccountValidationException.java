package com.steeplesoft.mailfilter.exceptions;

import com.steeplesoft.mailfilter.model.Account;
import java.util.Set;
import javax.validation.ConstraintViolation;

/**
 *
 * @author jason
 */
public class AccountValidationException extends RuntimeException {
    private final Set<ConstraintViolation<Account>> violations;

    public AccountValidationException(Set<ConstraintViolation<Account>> violations) {
        super();
        this.violations = violations;
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder("Account validation error(s) occurred:");
        for (ConstraintViolation violation : violations) {
            message.append("\n\t").append(violation.getMessage());
        }
        
        return message.toString();
    }
}
