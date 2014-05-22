package com.example.databaseandroidprojectexample;

import com.lvc.database.util.DataSerializer;

public class DataSerializerImp implements DataSerializer {

	@Override
	public String toJson(Object content) {
		return null;//br.com.lvc.utility.connection.DataSerializer.getInstance().toJson(content);
	}

	@Override
	public <T> T toObject(String json, Class<T> targetClass) {
		return null;//br.com.lvc.utility.connection.DataSerializer.getInstance().toObject(json, targetClass);
	}

	 

}
