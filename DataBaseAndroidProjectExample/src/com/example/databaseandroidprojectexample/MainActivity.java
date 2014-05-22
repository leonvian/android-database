package com.example.databaseandroidprojectexample;

import java.util.HashMap;
import java.util.List;

import br.com.lvc.utility.screen.BaseActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
 
	
	public void onClickTesteDataBase(View view) {
		try {
			HashMap<String, String> value = new HashMap<String, String>();
			value.put("KEY UM", "VALUE UM");
			value.put("KEY DOIS", "VALUE 2");
			value.put("KEY TRES", "VALUE 3");
			value.put("KEY QUATRO", "VALUE 4");
			
			Pessoa pessoa = new Pessoa("leo", value);
			PessoaDAO pessoaDAO = PessoaDAO.getInstance(this); 
			pessoaDAO.save(pessoa);
			
			Log.i("COUNT", "COUNT: " + pessoaDAO.count());
			
		
		   List<Pessoa> pessoas = pessoaDAO.getAllElements();
			for(Pessoa pessoa2 : pessoas) {
				HashMap<String, String> value2 = pessoa2.getHashMapUm();
				Log.i("HASH", "HASH DESSERIALIZE: " + value2.toString());
			}
			
			goToNextScreen(MainActivityDois.class);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
