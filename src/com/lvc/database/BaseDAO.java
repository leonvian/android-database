package com.lvc.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.databaseandroidproject.R;
import com.lvc.database.annotation.TableName;

public abstract class BaseDAO<T extends EntitiePersistable> extends BaseDAOReflection<T> {

	private static final String SELECT_ALL_FROM = "SELECT  * FROM ";
	private static final String SELECT = "SELECT";
	private static final String FROM = "FROM"; 
	private static final String RETURN_ALL_SYMBOL = "*";
	private static final String COMMA = ",";


	protected SQLiteDatabase dataBase;
	private Context context;


	public BaseDAO(Context context) {
		this.context = context;
		openConnection(); 
	}

	public void openConnection() {
		SQLiteOpenHelper helper = getDataBaseHelper();
		dataBase = helper.getWritableDatabase();
	}

	public void reopenConnectionIfClose() {
		if(dataBase == null || !dataBase.isOpen()) {
			Log.e("SQLITE", "Reabrindo conex‹o reabrindo!");
			openConnection();
		}
	}

	public Context getContext() {
		return context;
	}

	public abstract SQLiteOpenHelper getDataBaseHelper();

	public String getIdColumnName() throws AndroidDataBaseException {
		Field fieldPrimary = getPrimaryKeyField();
		return getColumnNameByField(fieldPrimary); 
	}



	public long getPrimaryKeyValue(T entitie) throws AndroidDataBaseException, ReflectionException {
		String fieldPrimary = getPrimaryKeyField().getName();
		Method method = getGetMethodByName(fieldPrimary);
		Object objReturn = invokeMethod(entitie, method);

		if(objReturn instanceof Integer) {
			Integer primaryKeyValue = (Integer)objReturn;
			return primaryKeyValue.longValue();
		} else {
			return (Long)objReturn;	
		}


	}

	public String getTableName() {

		if(getEntitieClass().isAnnotationPresent(TableName.class)) {
			TableName tableName = getEntitieClass().getAnnotation(TableName.class);
			return tableName.value();
		} else {
			return getEntitieClass().getSimpleName();
		}

	}

	public long save(T entitie) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();

			ContentValues values = generateContentValues(entitie, true);
			return dataBase.insert(getTableName(), null, values);

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_salvar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}

	public void saveOrUpdade(T entitie) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();

			Long id = getPrimaryKeyValue(entitie);

			if(id == 0) 
				save(entitie);
			else 
				saveOrUpdateIfExist(entitie, id);


		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new AndroidDataBaseException(e, "Falha ao salvar ou Alterar".concat("" + e.getMessage()));
		}
	}

	private void saveOrUpdateIfExist(T entitie, long id) throws AndroidDataBaseException {
		T entitieToEdit = getEntitieByID(id);
		if(entitieToEdit != null) { // tem registro com esse ID
			update(entitie);
		} else {
			save(entitie);
		}
	}


	public long saveAll(Collection<T> elements) throws AndroidDataBaseException {
		long result = 0;

		reopenConnectionIfClose();

		for(T entitie : elements) {
			result = save(entitie);
			if(result == -1) 
				return result;
		}

		return result;
	}



	public long saveOrUpdadeAll(Collection<T> elements) throws AndroidDataBaseException {
		long result = 0;

		reopenConnectionIfClose();

		for(T entitie : elements) {
			saveOrUpdade(entitie);
		}

		return result;
	}

	public T getEntitieByID(long id) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();

			String selectQuery = "SELECT  * FROM " + getTableName() + " WHERE " + getIdColumnName() + "= " + id;
			Cursor cursor = dataBase.rawQuery(selectQuery, null);

			try {

				if (cursor != null && cursor.moveToFirst()) {
					T t = cursorToEntitie(cursor);
					return t;
				} else {
					return null;
				}

			} finally {
				closeCursor(cursor);
			}

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_recuperar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}

	private void closeCursor(Cursor cursor) {
		if(cursor != null && !cursor.isClosed())
			cursor.close();	
	}

	public List<T> getAllElements() throws AndroidDataBaseException  {
		String selectQuery = SELECT_ALL_FROM.concat(getTableName());
		List<T> elementsList = getAllElements(selectQuery);

		return elementsList;
	}

	public <Z>List<Z> getAllElements(String selectQuery, Class<Z> especificReturn) throws AndroidDataBaseException  {
		throwExceptionIfIsAIncorretQuery(selectQuery);
		List<Z> elementsList = new ArrayList<Z>();
		try {
			reopenConnectionIfClose();
			Cursor cursor = dataBase.rawQuery(selectQuery, null);

			try {

				if (cursor.moveToFirst()) {
					do {
						Z entitie = cursorToEntitie(cursor, especificReturn);
						elementsList.add(entitie);
					} while (cursor.moveToNext());
				}

			} finally {
				if(!cursor.isClosed())
					cursor.close();	
			}

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_recuperar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		} 

		return elementsList;
	}

	public List<T> getAllElements(String selectQuery) throws AndroidDataBaseException  {
		throwExceptionIfIsAIncorretQuery(selectQuery);
		boolean returnAllColumns = hasToReturnAllColumns(selectQuery);

		List<T> elementsList = new ArrayList<T>(); 
		try {
			reopenConnectionIfClose();

			Cursor cursor = dataBase.rawQuery(selectQuery, null);
			try {

				if (cursor.moveToFirst()) {
					do {
						T entitie = null;
						
						if(returnAllColumns) {
							entitie = cursorToEntitie(cursor);
						} else {
							String[] selectColumnsArray = getSelectColumns(selectQuery);
							List<Field> selectedFields = getFieldsByColumnName(selectColumnsArray);
							entitie = cursorToEntitie(cursor, selectedFields);
						}
						
						elementsList.add(entitie);
					} while (cursor.moveToNext());
				}

			} finally {
				if(!cursor.isClosed())
					cursor.close();	
			}

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_recuperar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		} 

		return elementsList;
	}

	private boolean hasToReturnAllColumns(String query) {

		String selectColumns = getSelectColumnsString(query);

		if(selectColumns.length() == 0) 
			throw new AndroidDataBaseException("No columns return was especified  = SELECT *** NOTHING HERE *** FROM");

		if(RETURN_ALL_SYMBOL.equals(selectColumns)) {
			return  true;
		} else if(selectColumns.contains(COMMA)) {
			String[] selectColumnsArray = selectColumns.split(COMMA);
			throwExeceptionIfColumnDoesnMatch(selectColumnsArray);
			return false;
		} else {
			throwExeceptionIfColumnDoesnMatch(selectColumns);
			return false;
		}
	}

	private String[] getSelectColumns(String query) {
		String selectColumns = getSelectColumnsString(query);

		if(selectColumns.contains(COMMA)) {
			String[] selectColumnsArray = selectColumns.split(COMMA); 
			throwExeceptionIfColumnDoesnMatch(selectColumnsArray);
			return selectColumnsArray;
		} else {
			throwExeceptionIfColumnDoesnMatch(selectColumns);
			String[] selectColumnsArray = {
					selectColumns.trim()
			};
			return selectColumnsArray;
		}
	}

	private List<Field> getFieldsByColumnName(String... selectColumnsArray) {
		List<Field> selectedFields = new ArrayList<Field>();

		for(String columnTarget : selectColumnsArray) {
			Field targetField = getFieldByName(columnTarget.trim());
			selectedFields.add( targetField );	
		}

		return selectedFields;
	}

	private void throwExeceptionIfColumnDoesnMatch(String[] selectColumnsArray) {
		for(String selectColumn : selectColumnsArray) {
			throwExeceptionIfColumnDoesnMatch(selectColumn);
		}
	}

	private void throwExeceptionIfColumnDoesnMatch(String selectColumn) {
		selectColumn = selectColumn.trim();
		if(!existThisColumn(selectColumn))
			throw new AndroidDataBaseException("This column " + selectColumn+ " doesn't was found in " + getTableName());
	}

	private String getSelectColumnsString(String query) {
		query = query.toUpperCase();
		int indexSelect = query.indexOf(SELECT) + SELECT.length();
		int indexFrom = query.indexOf(FROM); 

		String selectColumns = query.substring(indexSelect, indexFrom).trim();
		return selectColumns;
	}

	private void throwExceptionIfIsAIncorretQuery(String query) {
		if(query == null)
			throw new AndroidDataBaseException("Query passed is NULL");

		query = query.toUpperCase();
		if(!query.contains(SELECT)) {
			throw new AndroidDataBaseException("Your string query has to have SELECT " + query);
		} else if(!query.contains(FROM)) {
			throw new AndroidDataBaseException("Your string query has to have FROM " + query);
		}

	}


	public int update(T entitie) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();
			ContentValues values = generateContentValues(entitie,false);
			Long id = getPrimaryKeyValue(entitie);
			return dataBase.update(getTableName(), values, getIdColumnName() + " = ?", new String[] { String.valueOf(id) });

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_alterar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}


	public void deleteAll() {

		reopenConnectionIfClose();

		String deleteQuery = "DELETE FROM " + getTableName();
		dataBase.execSQL(deleteQuery);
	}

	public int count() {
		String countQuery = "SELECT  * FROM " + getTableName();
		return count(countQuery);
	}

	public int count(String countQuery) {
		reopenConnectionIfClose();

		int count = 0;

		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(countQuery, null);
			count = cursor.getCount();	
		} finally {
			closeCursor(cursor);
		}

		return count;
	}

	public void delete(T entitie) throws AndroidDataBaseException {

		reopenConnectionIfClose();

		try {
			Long id = getPrimaryKeyValue(entitie);
			dataBase.delete(getTableName(), getIdColumnName() + " = ?",	new String[] { String.valueOf(id) });
		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_deletar).concat(e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}

	public void close() {
		if(dataBase != null && dataBase.isOpen())
			dataBase.close();

		dataBase = null;
	}


}