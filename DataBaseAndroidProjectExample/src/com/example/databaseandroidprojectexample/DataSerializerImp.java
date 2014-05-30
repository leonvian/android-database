package com.example.databaseandroidprojectexample;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvc.database.util.DataSerializer;

public class DataSerializerImp implements DataSerializer {
	
	public static final String DEFAULT_FORMAT_DATE = "yyyy-MM-dd HH:mm a z";
	
	private ObjectMapper objectMapper = null;
	private static DataSerializerImp  instance;
	
	
	public  static DataSerializerImp getInstance() {
		if(instance == null)
			instance = new DataSerializerImp();
		return instance;
	}


	public DataSerializerImp() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(Include.NON_NULL);  
		
		DateFormat df = new SimpleDateFormat(DEFAULT_FORMAT_DATE);
		objectMapper.setDateFormat(df);
	} 
  
 

	public String toJson(Object content)   {
		try {
			return objectMapper.writeValueAsString(content);	
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
 
	public Object toObject(String json, Class targetClass)  {
		try {
			return  objectMapper.readValue(json, targetClass);	
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	 

}
