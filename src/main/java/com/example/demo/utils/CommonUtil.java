package com.example.demo.utils;

import com.example.demo.exception.ParameterValidationException;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;

public class CommonUtil {

    public static LocalDate getExpiredDate(){
        return LocalDate.now().plusYears(1);
    }

    public static void bindingResult(BindingResult result){
        if(result.hasErrors()){
            throw new ParameterValidationException(result.getAllErrors().stream().findFirst().get().getDefaultMessage());
        }
    }
}
