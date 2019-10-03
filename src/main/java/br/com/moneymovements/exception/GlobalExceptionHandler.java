package br.com.moneymovements.exception;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import br.com.moneymovements.domain.ApiError;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ InsufficientBalanceException.class, OpenAccountException.class, CloseAccountException.class,
			UnableToDepositException.class, AccountNotFoundException.class, SameAccountException.class, UnableToCreateUserException.class })
	public final ResponseEntity<ApiError> handleExeption(Exception ex, WebRequest request) {

		HttpHeaders headers = new HttpHeaders();

		if (ex instanceof InsufficientBalanceException) {
			HttpStatus status = HttpStatus.CONFLICT;
			InsufficientBalanceException unfe = (InsufficientBalanceException) ex;
			return handleInsufficientBalanceException(unfe, headers, status, request);
		}

		else if (ex instanceof OpenAccountException) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			OpenAccountException unfe = (OpenAccountException) ex;
			return handleOpenAccountException(unfe, headers, status, request);
		}

		else if (ex instanceof CloseAccountException) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			CloseAccountException unfe = (CloseAccountException) ex;
			return handleCloseAccountException(unfe, headers, status, request);
		}

		else if (ex instanceof UnableToDepositException) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			UnableToDepositException unfe = (UnableToDepositException) ex;
			return handleUnableToDepositException(unfe, headers, status, request);
		}

		else if (ex instanceof AccountNotFoundException) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			AccountNotFoundException unfe = (AccountNotFoundException) ex;
			return handleAccountNotFoundException(unfe, headers, status, request);
		}

		else if (ex instanceof SameAccountException) {
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			SameAccountException unfe = (SameAccountException) ex;
			return handleSameAccountException(unfe, headers, status, request);
		}
		
		else if (ex instanceof InvalidJWTAuthenticationException) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			InvalidJWTAuthenticationException unfe = (InvalidJWTAuthenticationException) ex;
			return handleInvalidJWTAuthentication(unfe, headers, status, request);
		}
		
		else if (ex instanceof UnableToCreateUserException) {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			UnableToCreateUserException unfe = (UnableToCreateUserException) ex;
			return handleUnableToCreateUserException(unfe, headers, status, request);
		}

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return handleExceptionInternal(ex, null, headers, status, request);
	}

	private ResponseEntity<ApiError> handleOpenAccountException(Exception ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleCloseAccountException(Exception ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleInsufficientBalanceException(Exception ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleUnableToDepositException(Exception ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleAccountNotFoundException(Exception ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleSameAccountException(Exception ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

	private ResponseEntity<ApiError> handleExceptionInternal(Exception ex, @Nullable ApiError body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(body, headers, status);
	}
	
	private ResponseEntity<ApiError> handleInvalidJWTAuthentication(Exception ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}
	
	private ResponseEntity<ApiError> handleUnableToCreateUserException(Exception ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		List<String> error = Collections.singletonList(ex.getMessage());
		return handleExceptionInternal(ex, new ApiError(error), headers, status, request);
	}

}
