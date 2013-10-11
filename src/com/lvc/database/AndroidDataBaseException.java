package com.lvc.database;

public class AndroidDataBaseException extends RuntimeException {

	private static final long serialVersionUID = 1851527176956408438L;
	
	
	public AndroidDataBaseException(Throwable t, String message) {
		super(message, t);
	}
	
	
	public AndroidDataBaseException(String message) {
		super(message);
	}

}
