package com.steeplesoft.mailfilter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steeplesoft.mailfilter.exceptions.AccountValidationException;
import com.steeplesoft.mailfilter.exceptions.RuleValidationException;
import com.steeplesoft.mailfilter.model.Account;
import com.steeplesoft.mailfilter.model.Rule;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 *
 * @author jason
 */
public class AccountService {

    private final File rulesFile;

    public AccountService() {
        this(null);
    }

    public AccountService(String fileName) {
        this.rulesFile = getRulesFile(fileName);
    }

    public List<Account> getAccounts() {
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        List<Account> accounts = null;
        try {
            accounts = mapper.readValue(rulesFile, 
                    new TypeReference<List<Account>>() {});
            if (accounts != null) {
                accounts.forEach((account) -> {
                    final Set<ConstraintViolation<Account>> accountViolations = validator.validate(account);
                    if (accountViolations.size() > 0) {
                        throw new AccountValidationException(accountViolations);
                    }
                    account.getRules().sort((o1, o2) -> o1.getType().compareTo(o2.getType()));
                });
            }
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return accounts;
    }

    public void saveAccounts(List<Account> accounts) {
        try {
            final ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            System.out.println(mapper.writeValueAsString(accounts));
            mapper.writeValue(rulesFile, accounts);
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File getRulesFile(final String fileName) {
        final File file = new File(fileName != null ? fileName
                : System.getProperty("user.home") + File.separatorChar
                + ".mailfilter" + File.separatorChar + "rules.json");
        if (!file.exists()) {
            throw new IllegalArgumentException("The rules file does not exist: " + rulesFile);
        }
        return file;
    }
}
