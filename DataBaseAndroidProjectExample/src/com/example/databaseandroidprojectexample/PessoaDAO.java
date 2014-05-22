package com.example.databaseandroidprojectexample;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lvc.database.ReflectionException;

public class PessoaDAO extends BaseDAOTest<Pessoa> {

	private static PessoaDAO instance; 

	public PessoaDAO(Context context) {
		super(context);
	}

	public static PessoaDAO getInstance(Context context) {
		if(instance == null)
			instance = new PessoaDAO(context);
		return instance;
	}

	@Override
	public Class<Pessoa> getEntitieClass() {
		return Pessoa.class;
	}

	@Override
	public ContentValues generateContentValues(Pessoa entitie, boolean ignorePrimaryKey) throws ReflectionException {
		
		ContentValues contentValues = new ContentValues();
		if(!ignorePrimaryKey)
			contentValues.put("id", entitie.getId()); 
		
		contentValues.put("nome", entitie.getNome());
		contentValues.put("hashMapUm", getDataSerializer().toJson(entitie.getHashMapUm()));
		contentValues.put("photo", entitie.getPhoto());
		contentValues.put("idade", entitie.getIdade());
		contentValues.put("heterosexual", entitie.isHeterosexual());

		return contentValues;
	}

	@Override
	public Pessoa cursorToEntitie(Cursor cursor) throws InstantiationException, IllegalAccessException, ReflectionException {
		int indexID = cursor.getColumnIndex("id");
		int indexName = cursor.getColumnIndex("nome");
		int indexHashMap = cursor.getColumnIndex("hashMapUm");
		int indexPhoto = cursor.getColumnIndex("photo");
		int indexIdade = cursor.getColumnIndex("idade");
		int indexHetero = cursor.getColumnIndex("heterosexual");

		Long id = cursor.getLong(indexID);
		String nome = cursor.getString(indexName);
		String hashMapStr = cursor.getString(indexHashMap);
		HashMap<String, String> hashMap = getDataSerializer().toObject(hashMapStr, HashMap.class);
		byte[] photo = cursor.getBlob(indexPhoto);
		byte idade = (byte)cursor.getInt(indexIdade);
		boolean hetero = cursor.getInt(indexHetero)>0;

		Pessoa pessoa = new Pessoa(nome, hashMap);
		pessoa.setId(id);
		pessoa.setPhoto(photo);
		pessoa.setIdade(idade);
		pessoa.setHeterosexual(hetero);

		return pessoa;
	}

}
