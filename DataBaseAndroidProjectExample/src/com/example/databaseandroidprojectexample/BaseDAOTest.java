package com.example.databaseandroidprojectexample;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

import com.lvc.database.BaseDAO;
import com.lvc.database.EntitiePersistable;
import com.lvc.database.util.DataSerializer;

public abstract class BaseDAOTest<T extends EntitiePersistable> extends BaseDAO<T>  {

	public BaseDAOTest(Context context) {
		super(context); 
	}

	@Override
	public SQLiteOpenHelper getDataBaseHelper() {
		return new OpenHelperTest(getContext());
	}
	
	@Override
	public DataSerializer getDataSerializer() {
		return DataSerializerImp. getInstance();
	}
	
}