package com.increff.pos.exception;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Setter
@Getter
public class ApiException extends Exception {

	private ApiStatus status;
	private List<FieldErrorData> fieldErrors;

	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, List<FieldErrorData> fieldErrors) {
		super(message);
		this.status = status;
		this.fieldErrors = fieldErrors;
	}

	public ApiStatus getStatus() {
		return status;
	}

	public List<FieldErrorData> getFieldErrors() {
		return fieldErrors;
	}
}



