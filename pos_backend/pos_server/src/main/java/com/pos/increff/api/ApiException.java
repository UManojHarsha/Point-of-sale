package com.pos.increff.api;

import com.pos.increff.model.data.FieldErrorData;
import java.util.List;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final List<FieldErrorData> fieldErrors;
	
	public ApiException(String message) {
		super(message);
		this.fieldErrors = null;
	}

	public ApiException(String message, List<FieldErrorData> fieldErrors) {
		super(message);
		this.fieldErrors = fieldErrors;
	}

	public List<FieldErrorData> getFieldErrors() {
		return fieldErrors;
	}

}
