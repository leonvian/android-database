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

import com.example.databaseandroidproject.R;
import com.lvc.database.annotation.TableName;
import com.lvc.database.util.DataSerializer;

public abstract class BaseDAO<T extends EntitiePersistable> extends BaseDAOReflection<T> {

	private static final String SELECT_ALL_FROM = "SELECT  * FROM ";
	private static final String DELETE_FROM = "DELETE FROM ";
	private static final String WHERE = " WHERE ";
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
			openConnection();
		}
	}

	public Context getContext() {
		return context;
	}

	/**
	 * By default is null.
	 * But this method should be override in case of save a data as String. SaveAsString
	 */
	@Override
	public DataSerializer getDataSerializer() {
		return null;
	}

	public abstract SQLiteOpenHelper getDataBaseHelper();

	public String getIdColumnName() throws AndroidDataBaseException {
		Field fieldPrimary = getPrimaryKeyField();
		return getColumnNameByField(fieldPrimary); 
	}

	private Long getPrimaryKeyValueOrReturnNull(T entitie) throws AndroidDataBaseException, ReflectionException {
		String fieldPrimary = getPrimaryKeyField().getName();
		Method method = getGetMethodByName(fieldPrimary);
		Object objReturn = invokeMethod(entitie, method);

		if(objReturn == null)
			return null;

		if(objReturn instanceof Integer) {
			Integer primaryKeyValue = (Integer)objReturn;
			return primaryKeyValue.longValue();
		} else {
			return (Long)objReturn;	
		} 
	}

	private void setPrimaryKeyValue(T entitie, Long value) throws AndroidDataBaseException, ReflectionException { 
		Field primaryKeyField = getPrimaryKeyField();
		String fieldPrimary = primaryKeyField.getName();
		Method method = getSetMethodByName(fieldPrimary);

		FieldType type = TypeFinder.getFieldTypeWithoutAnnotationVerification(primaryKeyField);

		if(type == FieldType.INTEGER || type == FieldType.INTEGER_PRIMITIVE) {
			int valueInt = value.intValue();
			invokeMethodWithParameter(entitie, method, valueInt);
		} else if(type == FieldType.LONG || type == FieldType.LONG_PRIMITIVE) {
			invokeMethodWithParameter(entitie, method, value);	
		} else {
			return;
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

	/**
	 * This method just will call saveOrUpdate method.
	 * If you want a method to insert any data without Primary Key validation use insert method.
	 * 
	 * @param entitie 
	 * @return
	 * @throws AndroidDataBaseException
	 */
	public long save(T entitie) throws AndroidDataBaseException {
		return saveOrUpdade(entitie);
	}
	
	
	/**
	 * This method will insert a item at Database.
	 * 
	 * @param entitie 
	 * @return
	 * @throws AndroidDataBaseException
	 */
	public long insert(T entitie) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();

			ContentValues values = generateContentValues(entitie, true);
			long insertedId = dataBase.insert(getTableName(), null, values); 
			setPrimaryKeyValue(entitie, insertedId);
			return insertedId;

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_salvar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}

	/**
	 * This method will try to save a Entitie or Update if it already exist at database.
	 * @param entitie 
	 * @return
	 * @throws AndroidDataBaseException
	 */
	public long saveOrUpdade(T entitie) throws AndroidDataBaseException {
		long result = 0;
		try {

			reopenConnectionIfClose();

			Long id = getPrimaryKeyValueOrReturnNull(entitie);

			if(id == null || id == 0) 
				result = insert(entitie);
			else 
				result = saveOrUpdateIfExist(entitie, id);


		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new AndroidDataBaseException(e, "Fail to save or update ".concat(e.getMessage()));
		}
		
		return result;
	}

	private long saveOrUpdateIfExist(T entitie, long id) throws AndroidDataBaseException {
		long result = 0;
		T entitieToEdit = getEntitieByID(id);
		if(entitieToEdit != null) { // tem registro com esse ID
			result = update(entitie);
		} else {
			result = insert(entitie);
		}
		
		return result;
	}

	public long saveAll(Collection<T> elements) throws AndroidDataBaseException {
		long result = 0;

		reopenConnectionIfClose();

		dataBase.beginTransaction();

		try {
			for(T entitie : elements) {
				result = insert(entitie);
				if(result == -1) 
					return result;
			}

			dataBase.setTransactionSuccessful();
		} finally {
			dataBase.endTransaction();
		}

		return result;
	}
	
	public long saveOrUpdadeAll(Collection<T> elements) throws AndroidDataBaseException {
		long result = 0;

		reopenConnectionIfClose();

		dataBase.beginTransaction();

		try {

			for(T entitie : elements) {
				saveOrUpdade(entitie);
			}

			dataBase.setTransactionSuccessful();
		} finally {
			dataBase.endTransaction();
		}

		return result;
	}

	public T getEntitieByID(long id) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();

			String selectQuery = SELECT_ALL_FROM + getTableName() + " WHERE " + getIdColumnName() + "= " + id;
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

	public List<T> getElements(String where, String[] whereArgs, boolean distinct, String[] columns, String groupBy, String having, String orderBy, String limit) throws AndroidDataBaseException  {
		
		boolean returnAllColumns = hasToReturnAllColumns(columns);
		List<T> elementsList = new ArrayList<T>(); 
		
		try {
			reopenConnectionIfClose();
			Cursor cursor = dataBase.query(distinct, getTableName(), columns, where, whereArgs, groupBy, having, orderBy, limit);
			try {

				if (cursor.moveToFirst()) {
					do {
						T entitie = null;

						if(returnAllColumns) {
							entitie = cursorToEntitie(cursor);
						} else {
							List<Field> selectedFields = getFieldsByColumnName(columns);
							entitie = cursorToEntitie(cursor, selectedFields);
						}

						elementsList.add(entitie);
					} while (cursor.moveToNext());
				}

			} finally {
				if(!cursor.isClosed())
					cursor.close();	
			}
			
			return elementsList;

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_recuperar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		} 
	}
	
	public List<T> getElements(String where, String[] whereArgs, String orderBy, String limit) throws AndroidDataBaseException  {
		List<T> elements = getElements(where, whereArgs, false, null, null, null, orderBy, limit);
		return elements;
	}
	
	public List<T> getElements(String where, String[] whereArgs) throws AndroidDataBaseException  {
		List<T> elements = getElements(where, whereArgs, false, null, null, null, null, null);
		return elements;
	}
	
	public List<T> getElements(String where, String[] whereArgs, String orderBy) throws AndroidDataBaseException  {
		List<T> elements = getElements(where, whereArgs, false, null, null, null, orderBy, null);
		return elements;
	}
	
	public List<T> getElementsRawQuery(String selectQuery, String[] selectionArgs) throws AndroidDataBaseException  {
		throwExceptionIfIsAIncorretQuery(selectQuery);
		boolean returnAllColumns = hasToReturnAllColumns(selectQuery);

		List<T> elementsList = new ArrayList<T>(); 
		try {
			reopenConnectionIfClose();

			Cursor cursor = dataBase.rawQuery(selectQuery, selectionArgs);
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

	protected List<T> cursorToElements(Cursor cursor) throws InstantiationException, IllegalAccessException, ReflectionException {
		List<T> elementsList = new ArrayList<T>(); 
		
		try {
			if (cursor.moveToFirst()) {
				do {
					T entitie = cursorToEntitie(cursor);
					elementsList.add(entitie);
				} while (cursor.moveToNext());
			}
		} finally {
			if(!cursor.isClosed())
				cursor.close();	
		}
		return elementsList;
	}

	public List<T> getElementsByWhereClause(String where, String[] selectionArgs) throws AndroidDataBaseException  {
		String query = SELECT_ALL_FROM + getTableName() + WHERE + where;
		List<T> elementsList = getElementsRawQuery(query, selectionArgs);
		return elementsList;
	}

	public List<T> getElementsByWhereClause(String where) throws AndroidDataBaseException  {
		List<T> elementsList = getElementsByWhereClause(where, null);
		return elementsList;
	}

	public List<T> getAllElements(String selectQuery) throws AndroidDataBaseException  {
		List<T> elementsList = getElementsRawQuery(selectQuery, null);  
		return elementsList;
	}
	
	private boolean hasToReturnAllColumns(String[] columns) {
		boolean result = false;
		
		if (columns == null || columns.length == 0) {
			result = true;
		}
		
		return result; 
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
	/**
	 * 
	 * This method is prepared to a unique return for a query
	 * This time you should just pass a Where part of a Query 
	 * 
	 * @param where - example: nome = 'Leonardo Viana
	 * @return
	 * @throws NoElementFoundException
	 */
	public T retrieveOneElementOrThrowException(String where) throws NoElementFoundException {
		String myQuery = SELECT_ALL_FROM + " " + getTableName() + " WHERE " + where + " LIMIT 1";
		List<T> elements = getAllElements(myQuery);
		if(elements.isEmpty()) {
			throw new NoElementFoundException("No element was found for query: " + myQuery);
		} else {
			return elements.get(0);
		} 
	}


	public int update(T entitie) throws AndroidDataBaseException {
		try {

			reopenConnectionIfClose();
			ContentValues values = generateContentValues(entitie,false);
			Long id = getPrimaryKeyValueOrReturnNull(entitie);
			if(id == null)
				throw new IllegalArgumentException("There is NO ID - Primary Key at " + getEntitieClass() +  " Please verify if this object was persisted before you update!");
			
			return dataBase.update(getTableName(), values, getIdColumnName() + " = ?", new String[] { String.valueOf(id) });

		} catch(Exception e) {
			e.printStackTrace();
			String message = getContext().getString(R.string.falha_alterar).concat(""+e.getMessage());
			throw new AndroidDataBaseException(e, message);
		}
	}


	public void deleteAll() {

		reopenConnectionIfClose();

		String deleteQuery = DELETE_FROM + getTableName();
		dataBase.execSQL(deleteQuery);
	}

	public void delete(String whereClause) {
		delete(whereClause, null);
	}

	public void delete(String whereClause, String[] whereArgs) {
		reopenConnectionIfClose();
		dataBase.delete(getTableName(), whereClause, whereArgs);
	}

	public int count() {
		return count(null, null);
	}

	public int count(String where, String[] selectionArgs) {
		reopenConnectionIfClose();
		
		int count = 0;
		String countWhere = SELECT_ALL_FROM + getTableName();
		
		if (where != null) {
			countWhere = countWhere.concat(" WHERE ".concat(where));
		}
	
		Cursor cursor = null;
		try { 
			cursor = dataBase.rawQuery(countWhere, selectionArgs);
			count = cursor.getCount();

		} finally {
			closeCursor(cursor);
		}

		return count;
	}

	public void delete(T entitie) throws AndroidDataBaseException {

		reopenConnectionIfClose();

		try {
			Long id = getPrimaryKeyValueOrReturnNull(entitie);
			if(id == null)
				throw new IllegalArgumentException("There is NO data at ID - Primary key at " + getEntitieClass() +  " Please verify if this object was persisted before you try to delete!");
			
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