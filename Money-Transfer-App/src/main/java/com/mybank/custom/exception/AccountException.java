package com.mybank.custom.exception;

/**
 * 
 * @author munish
 *
 */
public class AccountException extends Exception {

	private static final long serialVersionUID = 5951267492931028722L;

	public AccountException(String msg) {
		super(msg);
	}

	public AccountException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
