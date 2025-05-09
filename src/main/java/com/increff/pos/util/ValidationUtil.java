package com.increff.pos.util;

import com.increff.pos.exception.ApiException;
import com.increff.pos.exception.FieldErrorData;
import org.springframework.stereotype.Service;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ValidationUtil {
    public static <T> void validate(T obj) throws ApiException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (violations.isEmpty()) {
            return;
        }
        List<FieldErrorData> errorList = new ArrayList<>(violations.size());
        for (ConstraintViolation<T> violation : violations) {
            FieldErrorData error = new FieldErrorData();
            error.setCode("");
            error.setField(violation.getPropertyPath().toString());
            error.setMessage(violation.getMessage());
            errorList.add(error);
        }
        throw new ApiException( "Validation failed", errorList);
    }
}

