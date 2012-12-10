package com.robusta.commons.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({CONSTRUCTOR, METHOD, TYPE})
@Retention(RUNTIME)
public @interface OnlyForJpa {
}
