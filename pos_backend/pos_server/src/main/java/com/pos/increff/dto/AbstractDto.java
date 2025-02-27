package com.pos.increff.dto;

import com.pos.increff.api.ApiException;
import com.pos.increff.model.data.FieldErrorData;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractDto {
    
    protected static Validator validator;
    
    static {
        ValidatorFactory factory = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
        validator = factory.getValidator();
    }

    protected static <T> void checkValid(T obj) throws ApiException {
        Set<ConstraintViolation<T>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            List<FieldErrorData> errorList = new ArrayList<>(violations.size());
            for (ConstraintViolation<T> violation : violations) {
                FieldErrorData error = new FieldErrorData();
                error.setField(violation.getPropertyPath().toString());
                error.setMessage(violation.getMessage());
                System.out.println(error);
                System.out.println(error.getField());
                System.out.println(error.getMessage());
                errorList.add(error);
            }
            throw new ApiException("Input validation failed", errorList);
        }
    }
}
