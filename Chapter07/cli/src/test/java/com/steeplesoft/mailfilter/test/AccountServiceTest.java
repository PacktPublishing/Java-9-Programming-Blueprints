package com.steeplesoft.mailfilter.test;

import org.testng.annotations.Test;
import com.steeplesoft.mailfilter.AccountService;
import com.steeplesoft.mailfilter.model.Account;
import com.steeplesoft.mailfilter.model.Rule;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.Assert;

/**
 *
 * @author jason
 */
public class AccountServiceTest {

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void withBadFileName() {
        new AccountService("");
    }

    @Test(expectedExceptions = {IllegalArgumentException.class}, enabled = false)
    public void withNoName() {
        new AccountService(null);
    }

    @Test
    public void goodFile() {
        AccountService accountService = new AccountService("src/test/resources/rules.json");
        List<Account> accounts = accountService.getAccounts();
        Assert.assertTrue(accounts.size() > 0);
    }

    @Test
    public void writeAccounts() {
        try {
            final Set fields = new HashSet();
            fields.add("to");
            fields.add("from");
            fields.add("cc");
            
            Account account = new Account();
            account.setServerName("test");
            account.setServerPort(49152);
            account.setUserName("test@example.com");
            account.setPassword("password");

            List<Account> accounts = new ArrayList<>();
            accounts.add(account);

            List<Rule> rules = new ArrayList<>();
            rules.add(Rule.create().sourceFolder("Some box")
                    .type("DELETE")
                    .olderThan(45)
                    .fields(fields));

            account.setRules(rules);

            final File tempFile = File.createTempFile("rules", "json");
            AccountService service = new AccountService(tempFile.getCanonicalPath());
            service.saveAccounts(accounts);

            List<Account> read = service.getAccounts();
            Assert.assertEquals(accounts, read);
        } catch (IOException ex) {
            Logger.getLogger(AccountServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
