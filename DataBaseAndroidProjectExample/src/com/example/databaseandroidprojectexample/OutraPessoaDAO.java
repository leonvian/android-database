package com.example.databaseandroidprojectexample;

import android.content.Context;

public class OutraPessoaDAO extends BaseDAOTest<OutraPessoa> {
	
	private static OutraPessoaDAO instance;
	
	
	public static OutraPessoaDAO getInstance(Context context) {
		if(instance == null)
			instance = new OutraPessoaDAO(context);
		return instance;
	}

	public OutraPessoaDAO(Context context) {
		super(context); 
	} 

	@Override
	public Class<OutraPessoa> getEntitieClass() { 
		return OutraPessoa.class;
	}
 
}
