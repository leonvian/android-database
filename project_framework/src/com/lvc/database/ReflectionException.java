package com.lvc.database;

public class ReflectionException extends Exception {


	
	private static final long serialVersionUID = 6129513457765155991L;

	public ReflectionException(Throwable t, String message) {
		super(message,t);
	}
	
	
	public ReflectionException(String message) {
		super(message);
	}
}
