package com.lvc.database.util;


public interface DataSerializer {
	
	public  String toJson(Object content);
	
	public <T>  T toObject(String json, Class<T> targetClass);

}