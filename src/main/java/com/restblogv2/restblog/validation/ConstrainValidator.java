package com.restblogv2.restblog.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ConstrainValidator implements ConstraintValidator<PhoneNumber, String> {

    private String phonePrefix;

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        phonePrefix = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        boolean result;

        if (value != null){
            if (value.length() == 10){
                result = value.startsWith(phonePrefix);
            }else {
                return false;
            }

        }else
            return true;



        return result;
    }

}
