package com.github.aklakina.edmma.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Singleton annotation is used to mark a class as a singleton.
 * A singleton class is a class that can have only one object (an instance of the class) at a time.
 * This annotation can be applied to any class type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {

}