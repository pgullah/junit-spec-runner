package io.github.pgullah.jsr.annotation;

import io.github.pgullah.jsr.conversion.MethodArgsConverter;
import io.github.pgullah.jsr.conversion.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Spec {
    String path();

    Class<? extends MethodArgsConverter> argsConverter() default MethodArgsConverter.None.class;

    Class<? extends TypeConverter<String, ?>> resultConverter() default TypeConverter.None.class;

    boolean includeHeader() default false;
}
