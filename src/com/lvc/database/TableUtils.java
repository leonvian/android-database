package com.lvc.database;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import android.util.Log;

import com.lvc.database.annotation.Column;
import com.lvc.database.annotation.IgnoreColumn;
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

			if(!isToIgnore(field)) {

				String name = getColumnNameByField(field);
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

				if(field.isAnnotationPresent(PrimaryKey.class)) {
					
					stringBuilder.append(" PRIMARY KEY");
					PrimaryKey primaryKey  = field.getAnnotation(PrimaryKey.class);
					boolean isAutoIncrement = primaryKey.autoIncrement();
					if(isAutoIncrement)
						stringBuilder.append(" AUTOINCREMENT");

				}

				stringBuilder.append(",");

			}
		}

		String createTableValue = stringBuilder.toString();
		int index = createTableValue.lastIndexOf(',');
		createTableValue =createTableValue.substring(0,index);

		String createTableScript = createTableValue.concat(")");
		Log.i("TABLE CREATED", "Script criação tabela : " + createTableScript);
		
		return createTableScript;
	}

	private static String getColumnNameByField(Field field) {
		
		if(field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			return column.name();

		} else {
			return field.getName();
		}
	}

	private static boolean isToIgnore(Field field) {

		if(field.isAnnotationPresent(IgnoreColumn.class)) 
			return true;

		if(Modifier.isFinal(field.getModifiers())) {
			return true;
		}

		if(field.isEnumConstant()) {
			return true;
		}

		return false;
	}
}