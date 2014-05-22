package com.lvc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class DatabaseHelper extends SQLiteOpenHelper {

	protected Context context;
	
	public DatabaseHelper(Context context, String dataBaseName, int dataBaseVersion) {
		super(context, dataBaseName, null, dataBaseVersion);
		this.context = context;
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {		
		try {
			
			String[] scriptsCreate = getScriptsCreateDataBase();
			for(String query : scriptsCreate) {
				Log.i("ON CREATE", " " + query);
				db.execSQL(query);	
			}

		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try {

			String[] scriptsUpdate = getScriptsUpdateDataBase();

			for(String query : scriptsUpdate) {
				db.execSQL(query);	
			}
			
			onCreate(db);

		} catch (ReflectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	public abstract String[] getScriptsUpdateDataBase() throws ReflectionException;
	public abstract String[] getScriptsCreateDataBase() throws ReflectionException;

}