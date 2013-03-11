package com.lvc.database;

import java.lang.reflect.Field;
import java.util.Date;

public class TypeFinder {

	
	public static FieldType getFieldType(Field field) throws ReflectionException {
		return getFieldType(field.getType());
	}
	
     @SuppressWarnings({ "rawtypes", "unchecked" })
	public static FieldType getFieldType(Class targetClass) throws ReflectionException {
		
		if(targetClass.isAssignableFrom(String.class) )
			return FieldType.STRING;


		else if(targetClass.isAssignableFrom(Integer.class) )
			return FieldType.INTEGER;
		
		else if(targetClass.isAssignableFrom(int.class) )
			return FieldType.INTEGER_PRIMITIVE;

		else if(targetClass.isAssignableFrom(Double.class) )
			return FieldType.DOUBLE;
		
		else if(targetClass.isAssignableFrom(double.class) )
			return FieldType.DOUBLE_PRIMITIVE;

		else if(targetClass.isAssignableFrom(Float.class) )
			return FieldType.FLOAT;
				
		else if(targetClass.isAssignableFrom(float.class) )
			return FieldType.FLOAT_PRIMITIVE;

		else if(targetClass.isAssignableFrom(Date.class) ) 
			return FieldType.DATE;
		
		else if(targetClass.isAssignableFrom(Boolean.class) ) 
			return FieldType.BOOLEAN;
		
		else if(targetClass.isAssignableFrom(boolean.class) ) 
			return FieldType.BOOlEAN_PRIMITIVE;
		
		else if(targetClass.isAssignableFrom(Long.class) ) 
			return FieldType.LONG;
		
		else if(targetClass.isAssignableFrom(long.class) ) 
			return FieldType.LONG_PRIMITIVE;
		
		
		else {
			throw new ReflectionException("O tipo : " +targetClass.getName() + " n‹o foi encontrado!");
		}
		
	}
}
