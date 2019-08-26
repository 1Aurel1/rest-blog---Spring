package com.restblogv2.restblog.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Constraint(validatedBy =  Constrain)
@Target( {ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    public String value() default "06";

    public String message() default "Phone number must start with '06'";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default{};

}
