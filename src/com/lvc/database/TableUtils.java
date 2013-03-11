package com.lvc.database;

import java.lang.reflect.Field;
import com.lvc.database.annotation.PrimaryKey;
import com.lvc.database.annotation.TableName;

public class TableUtils {


	
	public static String createDropTableScript(Class entieClass) throws ReflectionException {
		String tableName = entieClass.getSimpleName();		
		
		if(entieClass.isAnnotationPresent(TableName.class)) {
			TableName tableNameAn = (TableName) entieClass.getAnnotation(TableName.class);
			tableName = tableNameAn.value();
		}		
		
		return "DROP TABLE IF EXISTS " + tableName;
		
	}


	public static String createTableScript(Class entieClass) throws ReflectionException {
		String tableName = entieClass.getSimpleName();
		
		if(entieClass.isAnnotationPresent(TableName.class)) {
			TableName tableNameAn = (TableName) entieClass.getAnnotation(TableName.class);
			tableName = tableNameAn.value();
		}
		
		Field[] fieldsArray = entieClass.getDeclaredFields();

		StringBuilder stringBuilder  = new StringBuilder();
		stringBuilder.append("CREATE TABLE ");
		stringBuilder.append(tableName);
		stringBuilder.append("(");

		for(Field field : fieldsArray) {	
			String name = field.getName();
			stringBuilder.append(name);

			FieldType type = TypeFinder.getFieldType(field);

			switch (type) {
			case STRING:
				stringBuilder.append(" TEXT");
				break;

			case INTEGER:
				stringBuilder.append(" INTEGER");
				break;
				
			case INTEGER_PRIMITIVE:
				stringBuilder.append(" INTEGER");
				break;

			case DOUBLE:
				stringBuilder.append(" DOUBLE");
				break;

			case DOUBLE_PRIMITIVE:
				stringBuilder.append(" DOUBLE");
				break;

			case FLOAT:
				stringBuilder.append(" FLOAT");
				break;
				
			case FLOAT_PRIMITIVE:
				stringBuilder.append(" FLOAT");
				break;


			case DATE:
				stringBuilder.append(" TEXT");
				break;

			case BOOLEAN:
				stringBuilder.append(" INTEGER");
				break;
				
			case BOOlEAN_PRIMITIVE:
				stringBuilder.append(" INTEGER");
				break;
				

			case LONG:
				stringBuilder.append(" LONG");
				break;
				
			case LONG_PRIMITIVE:
				stringBuilder.append(" LONG");
				break;

			default:
				throw new ReflectionException("Nenhum tipo foi encontrado para: " + field.getClass().getName());
			}

			if(field.isAnnotationPresent(PrimaryKey.class)) 
				stringBuilder.append(" PRIMARY KEY");
			
			stringBuilder.append(",");
		}


		String createTableValue = stringBuilder.toString();
		int index = createTableValue.lastIndexOf(',');
		createTableValue =createTableValue.substring(0,index);


		String createTableScript = createTableValue.concat(")");



		return createTableScript;

	}

}
