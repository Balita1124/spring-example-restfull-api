package com.balita.springexamplecrud.playload.error;

import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Data
public class CustomFieldError {
    private String fieldname;
    private String errorMessage;

    public CustomFieldError(String fieldname, String errorMessage) {
        this.fieldname = fieldname;
        this.errorMessage = errorMessage;
    }
}
