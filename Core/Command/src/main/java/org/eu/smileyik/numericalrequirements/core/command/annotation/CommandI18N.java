package org.eu.smileyik.numericalrequirements.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandI18N {
    /**
     * 指令的 I18N 根路径.
     * @return
     */
    String value() default "";
}
