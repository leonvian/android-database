package com.lvc.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.example.databaseandroidproject.R;
import com.lvc.database.annotation.Column;
import com.lvc.database.annotation.IgnoreColumn;
import com.lvc.database.annotation.PrimaryKey;

public abstract class BaseDAOReflection<T extends EntitiePersistable> {


	private static final String PREFIX_SET_METHODS = "set";
	private static final String PREFIX_GET_METHODS = "get";
	private static final String PREFIX_IS_METHODS = "is";

	private String[] atributesToIgnore = {
			"serialVersionUID"
	};
	
	private static final String PRIMARY_KEY_NOT_FOUND = "Não foi possível informar Primary Key, certifique-se de que utilizou a anotação PRIMARY KEY nas suas entidades";


	//protected List<Method> listMethods;
	//protected List<Field> listFields;

	public BaseDAOReflection() {

	}

	private <Z>List<Field> getFields(Class<Z> entitieClass) {
		Field[] fieldsArray = entitieClass.getDeclaredFields();
		List<Field> listFields = arrayToListWithoutIgnorableFields(fieldsArray);

		return listFields;
	}

	private List<Field> getFields() {
		return getFields(getEntitieClass());
	}

	private <Z>List<Method> getMethods(Class<Z> entitieClass) {
		Method[] methods = entitieClass.getDeclaredMethods();
		List<Method> listMethods = Arrays.asList(methods);

		return listMethods;
	}

	private List<Method> getMethods() {
		return getMethods(getEntitieClass());
	}

	private List<Field> arrayToListWithoutIgnorableFields(Field[] fieldsArray) {
		List<Field> fields = new ArrayList<Field>();

		for(Field field : fieldsArray) {

			String atributeName = field.getName();
			if(!isIgnorable(atributeName) && !isConstant(field) && !field.isAnnotationPresent(IgnoreColumn.class))  
				fields.add(field);	
		
		}

		return fields; 

	}

	
	private boolean isConstant(Field field) {
		
		if(Modifier.isFinal(field.getModifiers())) {
			return true;
		}
		
		if(field.isEnumConstant()) {
			return true;
		}
		
		return false;
		
	}

	public abstract Class<T> getEntitieClass();

	public <Z>Z cursorToEntitie(Cursor cursor, Class<Z> classOther) throws InstantiationException, IllegalAccessException, ReflectionException {

		if(cursor == null || cursor.getCount() == 0)
			return null;

		ContentValues contentValues = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, contentValues);

		List<Field> fieldsAsList = arrayToListWithoutIgnorableFields(classOther.getDeclaredFields());
		Z entitie = contentValuesToObject(contentValues, fieldsAsList, classOther);

		return entitie;
	}

	public T cursorToEntitie(Cursor cursor) throws InstantiationException, IllegalAccessException, ReflectionException {

		if(cursor == null || cursor.getCount() == 0)
			return null;

		ContentValues contentValues = new ContentValues();
		DatabaseUtils.cursorRowToContentValues(cursor, contentValues);
		List<Field> listFields = getFields();
		T entitie = contentValuesToObject(contentValues, listFields);

		return entitie;
	}

	public Field getPrimaryKeyField() throws AndroidDataBaseException {
		
		List<Field> listFields = getFields();
		for(Field field : listFields) {
			if(field.isAnnotationPresent(PrimaryKey.class)) {
				return field;
			}
		}


		throw new AndroidDataBaseException(PRIMARY_KEY_NOT_FOUND);

	}
	

	public T contentValuesToObject(ContentValues contentValues, List<Field> listFields) throws InstantiationException, IllegalAccessException, ReflectionException {

		return contentValuesToObject(contentValues, listFields, getEntitieClass());
	}

	/**
	 *  Método usado para converter um ContentValues em um objeto de negócio após o mesmo ter sido recuperado da base.
	 */
	public <Z>Z contentValuesToObject(ContentValues contentValues, List<Field> listFields, Class<Z> entitieTarget) throws InstantiationException, IllegalAccessException, ReflectionException {
		Z entitie = entitieTarget.newInstance();
		for(Field field : listFields) {

			String fieldName = field.getName();
			String columnName = getColumnNameByField(field);
			FieldType type = TypeFinder.getFieldType(field);
			Object parameter = null;

			switch (type) {
			case STRING:
				parameter = contentValues.getAsString(columnName);
				break;

			case INTEGER:
				parameter = contentValues.getAsInteger(columnName);
				break;

			case INTEGER_PRIMITIVE:
				parameter = contentValues.getAsInteger(columnName);
				break;
				
			case LONG:
				parameter = contentValues.getAsLong(columnName);
				break;

			case LONG_PRIMITIVE:
				parameter = contentValues.getAsLong(columnName);
				break;

			case DOUBLE:
				parameter = contentValues.getAsDouble(columnName);
				break;

			case DOUBLE_PRIMITIVE:
				parameter = contentValues.getAsDouble(columnName);
				break;

			case FLOAT:
				parameter = contentValues.getAsFloat(columnName);
				break;

			case FLOAT_PRIMITIVE:
				parameter = contentValues.getAsFloat(columnName);
				break;

			case DATE:
				String data = contentValues.getAsString(columnName);
				parameter = toDateTime(data);
				break;

			case BOOLEAN:
				Integer result = contentValues.getAsInteger(columnName);
				if(result ==  null || result == 0) {
					parameter = false;
				} else {
					parameter = true;
				}
				break;

			case BOOlEAN_PRIMITIVE:
				Integer result2 = contentValues.getAsInteger(columnName);
				if(result2 ==  null || result2 == 0) {
					parameter = false;
				} else {
					parameter = true;
				}
				break;

			default:
				throw new ReflectionException("Nenhum tipo foi encontrado para: " + field.getClass().getName());
			}

			Method setMethod = getSetMethodByName(fieldName, entitieTarget);
			invokeMethodWithParameter(entitie, setMethod,parameter);

		}

		return entitie;
	}


	public String getColumnNameByField(Field field) {
		if(field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			return column.name();
		} else {
			return field.getName();
		}
	}

	public  ContentValues generateContentValues(T entitie) throws ReflectionException {
		ContentValues contentValues = new ContentValues();
		List<Field> listFields = getFields();
		for(Field field : listFields) {
			String key = getColumnNameByField(field);

			String fieldName = field.getName();
			Method method = getGetMethodByName(fieldName);
			Object value = invokeMethod(entitie, method);

			carregarContentValue(contentValues, key, value);
		}

		return contentValues;
	}



	private void carregarContentValue(ContentValues contentValues, String key, Object value) throws ReflectionException {
		if(value == null)
			return;


		FieldType type = TypeFinder.getFieldType(value.getClass());


		switch (type) {
		case STRING:
			contentValues.put(key, (String)value);
			break;

		case INTEGER:
			contentValues.put(key, (Integer)value);
			break;

		case INTEGER_PRIMITIVE:
			if(value == null)
				contentValues.put(key, 0);
			else
				contentValues.put(key, (Integer)value);
			break;

		case DOUBLE:
			contentValues.put(key, (Double)value);
			break;

		case DOUBLE_PRIMITIVE:
			if(value == null)
				contentValues.put(key, 0);
			else
				contentValues.put(key, (Double)value);
			break;

		case FLOAT:
			contentValues.put(key, (Float)value);
			break;

		case FLOAT_PRIMITIVE:
			if(value == null)
				contentValues.put(key, 0);
			else
				contentValues.put(key, (Float)value);
			break;

		case DATE:
			String dateTime = toDateTime((Date)value);
			contentValues.put(key, dateTime);
			break;

		case BOOLEAN:
			contentValues.put(key, (Boolean)value);
			break;

		case BOOlEAN_PRIMITIVE:
			if(value == null)
				contentValues.put(key, (Boolean)false);
			else
				contentValues.put(key, (Boolean)value);
			break;

		case LONG:
			contentValues.put(key, (Long)value);
			break;

		case LONG_PRIMITIVE:
			if(value == null)
				contentValues.put(key, 0);
			else
				contentValues.put(key, (Long)value);
			break;


		default:
			break;
		}


	}

	protected String toDateTime(Date data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String dateTime = dateFormat.format(data);

		return dateTime;
	}

	protected Date toDateTime(String data) {
		if(data == null)
			return null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			Date dateTime = dateFormat.parse(data);

			return dateTime;	
		} catch(ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	public Object invokeMethod(T entitie, Method method) throws ReflectionException {
		try {
			return method.invoke(entitie, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReflectionException(e, "Falha ao invocar  método: " + method.getName());
		} 
	}

	public Object invokeMethodWithParameter(Object entitie, Method method, Object... param) throws ReflectionException {
		try {
			Object setParam = param[0];
			return method.invoke(entitie, setParam);	
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReflectionException(e, "Falha ao invocar  método: " + method.getName());
		} 
	}

	protected <Z>Method getSetMethodByName(String attributeName)  {
		return getMethodByName(PREFIX_SET_METHODS,attributeName, null);		
	}

	protected <Z>Method getSetMethodByName(String attributeName, Class<Z> target)  {
		return getMethodByName(PREFIX_SET_METHODS,attributeName, target);		
	}

	protected <Z>Method getGetMethodByName(String attributeName) {
		return getGetMethodByName(attributeName, null);
	}

	protected <Z>Method getGetMethodByName(String attributeName, Class<Z> target) {
		Method methodResult = null;
		try {
			methodResult = getMethodByName(PREFIX_GET_METHODS,attributeName, target);	
		} catch(RuntimeException runtimeException) {
			methodResult = getIsMethodByName(attributeName, target);	
		}
		return  methodResult;
	}

	protected <Z>Method getIsMethodByName(String attributeName, Class<Z> target) {
		return getMethodByName(PREFIX_IS_METHODS,attributeName, target); 
	}

	private <Z>Method getMethodByName(String prefix, String attributeName, Class<Z> target) {
		String nameMethodSet = getMethodNameByPrefix(prefix,attributeName);

		List<Method> listMethods = null;

		if(target != null)
			listMethods = getMethods(target);
		else
			listMethods = getMethods();


		for(Method method : listMethods) {
			if(method.getName().equalsIgnoreCase(nameMethodSet)) 
				return method;
		}		

		throw new RuntimeException("Método " + prefix + " não encontrado para o valor: " + attributeName + " Tentou achar: " + nameMethodSet + " Classe alvo: " + getEntitieClass().getName());
	}

	private boolean isIgnorable(String atributeName) {
		for(String ignorableAtributes : atributesToIgnore) {
			if(atributeName.equals(ignorableAtributes))
				return true;
		}

		return false;
	}


	private String getMethodNameByPrefix(String prefix, String attributeName) {
		char oldChar = attributeName.charAt(0);
		char newChar = attributeName.toUpperCase().charAt(0); 
		String nameMethodSet = prefix + attributeName.replace(oldChar, newChar);
		return nameMethodSet;
	}

}
