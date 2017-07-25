/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.steeplesoft.datecalc.parser.token;

/**
 *
 * @author jason
 * @param <T>
 */
public abstract class Token<T> {
    protected T value;
    
    public interface Info {
        String getRegex();
        Token getToken(String text);
    }

    public T getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getValue();
    }
}
