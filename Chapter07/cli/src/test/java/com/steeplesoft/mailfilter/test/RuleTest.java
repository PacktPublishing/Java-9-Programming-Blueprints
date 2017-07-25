/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.mailfilter.test;

import com.steeplesoft.mailfilter.model.Rule;
import java.util.HashSet;
import java.util.Set;
import javax.mail.Message;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class RuleTest {

    @Test
    public void multipleFieldsShouldReturnOrTerm() {
        final Set fields = new HashSet();
        fields.add("to");
        fields.add("from");
        fields.add("cc");

        Rule rule = Rule.create()
                .sourceFolder("INBOX")
                .matchingText("testText")
                .fields(fields);
        SearchTerm term = rule.getSearchTerm();
        Assert.assertTrue(term instanceof OrTerm);
        SearchTerm[] terms = ((OrTerm) term).getTerms();
        for (SearchTerm t : terms) {
            Assert.assertTrue(t instanceof FromStringTerm
                    || (t instanceof RecipientStringTerm && ((RecipientStringTerm) t).getRecipientType() == Message.RecipientType.CC)
                    || (t instanceof RecipientStringTerm && ((RecipientStringTerm) t).getRecipientType() == Message.RecipientType.TO));
        }
    }
}
