/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.mailfilter.model;

import com.steeplesoft.mailfilter.model.validation.ValidRule;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author jdlee
 */
public class Account {

    @NotBlank(message = "A value must be specified for serverName")
    private String serverName;
    @NotNull(message = "A value must be specified for serverPort")
    @Min(value = 0L, message = "The value must be positive")
    private Integer serverPort = 993;
    private boolean useSsl = true;
    @NotBlank(message = "A value must be specified for userName")
    private String userName;
    @NotBlank(message = "A value must be specified for password")
    private String password;
    @ValidRule
    private List<Rule> rules = new ArrayList<>();

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.serverName);
        hash = 59 * hash + Objects.hashCode(this.serverPort);
        hash = 59 * hash + (this.useSsl ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.userName);
        hash = 59 * hash + Objects.hashCode(this.password);
        hash = 59 * hash + Objects.hashCode(this.rules);
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
        final Account other = (Account) obj;
        if (this.useSsl != other.useSsl) {
            return false;
        }
        if (!Objects.equals(this.serverName, other.serverName)) {
            return false;
        }
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.serverPort, other.serverPort)) {
            return false;
        }
        if (!Objects.equals(this.rules, other.rules)) {
            return false;
        }
        return true;
    }

}
