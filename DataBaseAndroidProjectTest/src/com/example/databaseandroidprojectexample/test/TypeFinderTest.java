package com.example.databaseandroidprojectexample.test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import android.test.AndroidTestCase;

import com.example.databaseandroidprojectexample.Pessoa;
import com.lvc.database.FieldType;
import com.lvc.database.ReflectionException;
import com.lvc.database.TypeFinder;

public class TypeFinderTest extends AndroidTestCase { 

	public void testTypeFinder() throws ReflectionException {
		List<FieldType> listFieldTypes =  getTypeFieldsPessoa();
		
		Field[] fields = Pessoa.class.getDeclaredFields();
		for(Field field : fields) { 
			FieldType fieldType = TypeFinder.getFieldType(field);
			assertTrue("Não achou: " + fieldType,listFieldTypes.contains(fieldType));
		} 
	}
	
	private List<FieldType> getTypeFieldsPessoa() {
		FieldType[] fieldTypes = {
				FieldType.LONG_PRIMITIVE, // Seria ID Version
				FieldType.INTEGER_PRIMITIVE,
				FieldType.STRING,
				FieldType.BYTE_ARRAY,
				FieldType.INTEGER,
				FieldType.BYTE,
				FieldType.DATE,
				FieldType.BOOlEAN_PRIMITIVE,
		};
		
		List<FieldType> listFieldTypes = Arrays.asList(fieldTypes);
		return listFieldTypes; 
	}

}
