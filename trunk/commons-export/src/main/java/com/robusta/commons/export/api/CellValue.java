package com.robusta.commons.export.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method level annotation that indicates that
 * the annotated method supplies string value
 * for a spreadsheet cell.
 *
 * <p>Annotated methods must be a accessor
 * method that takes no arguments and returns
 * a string</p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CellValue {
    /**
     * Column index into which this value would be populated into.
     * @return - int index
     */
    int position();

    /**
     * Column sorted header text rendering.
     * @return String - header text
     */
    String headerText();

    /**
     *
     * @return boolean - visibility
     */
    boolean visibility() default true;
}
