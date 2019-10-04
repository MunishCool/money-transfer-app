package com.mybank.custom.exception;

/**
 * 
 * @author munish
 *
 */
public class UserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 404416333861565486L;

	public UserException(String msg) {
		super(msg);
	}

	public UserException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
