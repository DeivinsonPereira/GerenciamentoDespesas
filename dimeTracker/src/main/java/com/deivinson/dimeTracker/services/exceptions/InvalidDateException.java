package com.deivinson.dimeTracker.services.exceptions;

public class InvalidDateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public InvalidDateException (String msg) {
		super(msg);
	}
}
