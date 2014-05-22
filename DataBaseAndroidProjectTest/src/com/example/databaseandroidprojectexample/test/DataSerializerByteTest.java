package com.example.databaseandroidprojectexample.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.databaseandroidprojectexample.DataSerializerImp;
import com.lvc.database.util.DataSerializerByte;

public class DataSerializerByteTest extends AndroidTestCase {


	public void testHashMapSerializeByte() throws Exception {
		HashMap<String, String> hashMapUm = getHashMap();
		
		byte[] data = DataSerializerByte.toByte(hashMapUm);
		
		HashMap<String, String> hashMapUmRetorned = (HashMap<String, String>) DataSerializerByte.toObject(data);
		
		Set<String> keys = hashMapUmRetorned.keySet();
		for(String key : keys) {
			assertTrue(hashMapUm.containsKey(key));	
		}
		
		Collection<String> values = hashMapUmRetorned.values();
		for(String value : values) {
			Log.i("VALUE", "" + value);
			assertTrue(hashMapUm.containsValue(value));	
		} 
	}
	
	
	public void testHashMapSerializeJSON() throws Exception {
		HashMap<String, String> hashMapUm = getHashMap();
		
		DataSerializerImp dataSerializerImp = new DataSerializerImp();
		String json = dataSerializerImp.toJson(hashMapUm);
		
		HashMap<String, String> hashMapUmRetorned = (HashMap<String, String>) dataSerializerImp.toObject(json, HashMap.class);
		
		Set<String> keys = hashMapUmRetorned.keySet();
		for(String key : keys) {
			assertTrue(hashMapUm.containsKey(key));	
		}
		
		Collection<String> values = hashMapUmRetorned.values();
		for(String value : values) {
			Log.i("VALUE", "" + value);
			assertTrue(hashMapUm.containsValue(value));	
		} 
	}
	
	private HashMap<String, String> getHashMap() {
		HashMap<String, String> hashMapUm = new HashMap<String, String>();
		
		for(int i =0; i < TestConfiguration.NUMBER_REPETITION; i++) {
			hashMapUm.put("Carlos" + i, "Gordinho"+ i);
			hashMapUm.put("André"+ i, "Castro"+ i);
			hashMapUm.put("Rafael"+ i, "Silveira"+ i);	
		}
		 
		
		return hashMapUm;
	}
}