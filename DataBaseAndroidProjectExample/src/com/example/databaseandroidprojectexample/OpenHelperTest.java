package com.example.databaseandroidprojectexample;

import android.content.Context;

import com.lvc.database.DatabaseHelper;
import com.lvc.database.ReflectionException;
import com.lvc.database.TableUtils;

public class OpenHelperTest extends DatabaseHelper {

	private static final String DATABASE_NAME = "teste_database";
	private static final int DATABASE_VERSION = 1;
	
	
	public OpenHelperTest(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION);
	}
	
	@Override
	public String[] getScriptsCreateDataBase() throws ReflectionException {
		String[] scriptCreateDataBase = {
				TableUtils.createTableScript(Pessoa.class),
				TableUtils.createTableScript(OutraPessoa.class),
		};
		 

		return scriptCreateDataBase;
	}

	@Override
	public String[] getScriptsUpdateDataBase() throws ReflectionException {
		String[] scriptUpdateDataBase = {
				TableUtils.createDropTableScript(Pessoa.class),
				TableUtils.createDropTableScript(OutraPessoa.class),
		};
			 
		return scriptUpdateDataBase;
	}
}