package com.steeplesoft.mailfilter.model;

import com.steeplesoft.mailfilter.model.validation.ValidRule;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.mail.Message;
import javax.mail.search.BodyTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author jdlee
 */
@ValidRule
public class Rule {

    @NotNull
    private RuleType type = RuleType.MOVE;
    @NotBlank(message = "Rules must specify a source folder.")
    private String sourceFolder = "INBOX";
    private String destFolder;
    private Set<String> fields = new HashSet<>();
    private String matchingText;
    @Min(value = 1L, message = "The age must be greater than 0.")
    private Integer olderThan;
    private SearchTerm term;

    public Rule() {
    }

    public static Rule create() {
        return new Rule();
    }

    public Rule type(String type) {
        setType(type);
        return this;
    }

    public Rule sourceFolder(String name) {
        setSourceFolder(name);
        return this;
    }

    public Rule destFolder(String name) {
        setDestFolder(name);
        return this;
    }

    public Rule fields(Set<String> fields) {
        setFields(fields);
        return this;
    }

    public Rule matchingText(String text) {
        setMatchingText(text);
        return this;
    }

    public Rule olderThan(int days) {
        setOlderThan(days);
        return this;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }
    
    public void setType(String type) {
        this.type = RuleType.getRuleType(type);
    }

    public String getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }

    public String getDestFolder() {
        return destFolder;
    }

    public void setDestFolder(String destFolder) {
        this.destFolder = destFolder;
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setFields(Set<String> fields) {
        this.fields = fields;
    }

    public String getMatchingText() {
        return matchingText;
    }

    public void setMatchingText(String matchingText) {
        this.matchingText = matchingText;
    }

    public Integer getOlderThan() {
        return olderThan;
    }

    public void setOlderThan(Integer olderThan) {
        this.olderThan = olderThan;
    }

    @JsonIgnore
    public SearchTerm getSearchTerm() {
        if (term == null) {
            if (matchingText != null) {
                List<SearchTerm> terms = fields.stream().map(f -> createFieldSearchTerm(f))
                        .collect(Collectors.toList());
                term = new OrTerm(terms.toArray(new SearchTerm[0]));
            } else if (olderThan != null) {
                LocalDateTime day = LocalDateTime.now().minusDays(olderThan);
                term = new SentDateTerm(ComparisonTerm.LE,
                        Date.from(day.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
            }
        }

        return term;
    }

    private SearchTerm createFieldSearchTerm(String f) {
        switch (f.toLowerCase()) {
            case "from":
                return new FromStringTerm(matchingText);
            case "cc":
                return new RecipientStringTerm(Message.RecipientType.CC, matchingText);
            case "to":
                return new RecipientStringTerm(Message.RecipientType.TO, matchingText);
            case "body":
                return new BodyTerm(matchingText);
            case "subject":
                return new SubjectTerm(matchingText);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Rule{" + "type=" + type + ", sourceFolder=" + sourceFolder + ", destFolder=" + destFolder + ", fields=" + fields
                + ", matchingText=" + matchingText + ", olderThan=" + olderThan + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + Objects.hashCode(this.sourceFolder);
        hash = 89 * hash + Objects.hashCode(this.destFolder);
        hash = 89 * hash + Objects.hashCode(this.fields);
        hash = 89 * hash + Objects.hashCode(this.matchingText);
        hash = 89 * hash + Objects.hashCode(this.olderThan);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (!Objects.equals(this.sourceFolder, other.sourceFolder)) {
            return false;
        }
        if (!Objects.equals(this.destFolder, other.destFolder)) {
            return false;
        }
        if (!Objects.equals(this.matchingText, other.matchingText)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.fields, other.fields)) {
            return false;
        }
        if (!Objects.equals(this.olderThan, other.olderThan)) {
            return false;
        }
        return true;
    }

}
