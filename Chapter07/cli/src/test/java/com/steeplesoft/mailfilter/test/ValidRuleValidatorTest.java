/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.mailfilter.test;

import com.steeplesoft.mailfilter.model.Rule;
import com.steeplesoft.mailfilter.model.RuleType;
import com.steeplesoft.mailfilter.model.validation.ValidRule;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author jason
 */
public class ValidRuleValidatorTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void emptyRule() {
        Rule rule = new Rule();
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void matchingRuleMustSpecifyDestination() {
        Rule rule = new Rule();
        rule.setMatchingText("dummy");
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void matchingRuleMustSpecifyFields() {
        Rule rule = new Rule();
        rule.setMatchingText("dummy");
        rule.setDestFolder("TRASH");
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertFalse(violations.isEmpty());
    }

    @Test
    public void moveMessageByMatchingText() {
        Rule rule = new Rule();
        rule.setMatchingText("dummy");
        rule.setDestFolder("TRASH");
        rule.getFields().add("from");
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void moveMessageByAge() {
        Rule rule = new Rule();
        rule.setDestFolder("TRASH");
        rule.setOlderThan(30);
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void deleteMessageByMatchingText() {
        Rule rule = new Rule();
        rule.setType("delete");
        rule.setMatchingText("dummy");
        rule.getFields().add("from");
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void deleteMessageByAge() {
        Rule rule = new Rule();
        rule.setType("delete");
        rule.setOlderThan(30);
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertTrue(violations.isEmpty());
    }

    @Test
    public void deleteMessageWithInvalidAge() {
        Rule rule = new Rule();
        rule.setType("delete");
        rule.setOlderThan(-1);
        final Set<ConstraintViolation<Rule>> violations = validator.validate(rule);
        Assert.assertFalse(violations.isEmpty());
    }
    
    @Test
    public void validRuleShouldOnlyWorkOnRules() {
        final Set<ConstraintViolation<NotARule>> violations = validator.validate(new NotARule());
        Assert.assertFalse(violations.isEmpty());
    }
    
    @ValidRule
    private class NotARule {
        
    }
}
