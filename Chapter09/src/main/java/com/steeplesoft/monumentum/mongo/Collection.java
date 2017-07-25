package com.steeplesoft.monumentum.mongo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 *
 * @author jason
 */
@Qualifier 
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.METHOD, ElementType.FIELD, 
    ElementType.PARAMETER, ElementType.TYPE})  
public @interface Collection {
    @Nonbinding String value() default "unknown";  
}
