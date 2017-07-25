package com.steeplesoft.mailfilter.model;

/**
 *
 * @author jason
 */
public enum RuleType {
    DELETE, MOVE;
    
    public static RuleType getRuleType(String type) {
        switch(type.toLowerCase()) {
            case "delete" : return DELETE;
            case "move" : return MOVE;
            default : return null;
//                throw new IllegalArgumentException("Invalid rule type specified: " + type);
        }
    }
}
