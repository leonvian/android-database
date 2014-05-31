package com.example.databaseandroidprojectexample.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.databaseandroidprojectexample.OutraPessoa;
import com.example.databaseandroidprojectexample.OutraPessoaDAO;

// Essa entidade vai ser recuperada totalmente com reflexão
public class OutraPessoaDAOTest extends AndroidTestCase   {

	private static final String EDITED = "EDITADED";
	private static final byte[] PHOTO_DATA_EXAMPLE = {1,2,3,4,5,6,7,8};
	private static final byte IDADE_INICIAL = 12;
	private static final byte IDADE_EDITED = 22;
	
	
	
	
	public void testSavePerformance() {
		deleteAllPeople(); 
		saveToValidatePerformance();  
	}
	
	public void testRetrievePerformance() {
		deleteAllPeople(); 
		saveToValidatePerformance();
		
		List<OutraPessoa> pessoas = OutraPessoaDAO.getInstance(getContext()).getAllElements();
		assertEquals(TestConfiguration.NUMBER_REPETITION, pessoas.size());
	}
	
	private void saveToValidatePerformance() {
		for(int i = 0; i < TestConfiguration.NUMBER_REPETITION; i ++) {
			saveAPerson();
		}
	}
	
	public void testDeleteSimple() {
		testSaveALotOFPeople();
		
		List<OutraPessoa> peopleRetrieved = OutraPessoaDAO.getInstance(getContext()).getAllElements();
		
		for(OutraPessoa pessoa : peopleRetrieved) {
			OutraPessoaDAO.getInstance(getContext()).delete(pessoa);
		}
		
	}
	
	public void testSaveALotOFPeople() {
		deleteAllPeople();
		
		List<OutraPessoa> newBornPeople = createALotOfPeople();
		OutraPessoaDAO.getInstance(getContext()).saveAll(newBornPeople);
		
		List<OutraPessoa> peopleRetrieved = OutraPessoaDAO.getInstance(getContext()).getAllElements();
		
		assertTrue(peopleRetrieved.size() == newBornPeople.size());
		
		assertTrue(newBornPeople.containsAll(peopleRetrieved));
	}
	 
	public void testSave() {
	    saveAPerson();
	    int count = OutraPessoaDAO.getInstance(getContext()).count();
		assertTrue(count > 0);
	}
	 
	
	public void testDelete() {
	    saveAPerson();
		deleteAllPeople();
		assertTrue(OutraPessoaDAO.getInstance(getContext()).count() == 0);
	}
	
	private void deleteAllPeople() {
		OutraPessoaDAO.getInstance(getContext()).deleteAll();
	}
	
	public void testUpdate() {
		
		testDelete();
		testSave();
		
		List<OutraPessoa> pessoasRecuperadas = OutraPessoaDAO.getInstance(getContext()).getAllElements();
		for(OutraPessoa pessoa : pessoasRecuperadas) {
			
			assertEquals(pessoa.getIdade(), IDADE_INICIAL);
			Log.i("IDADE", "IDADE " + pessoa.getIdade());
			assertFalse(pessoa.isHeterosexual());
			pessoa.setNome(EDITED);
			pessoa.setHeterosexual(true);
			pessoa.setIdade(IDADE_EDITED); 
			OutraPessoaDAO.getInstance(getContext()).update(pessoa);	
			
			assertEquals(byteToString(pessoa.getPhoto()), byteToString(PHOTO_DATA_EXAMPLE));
		}
		
		List<OutraPessoa> pessoasRecuperadasDeNovo = OutraPessoaDAO.getInstance(getContext()).getAllElements();
		for(OutraPessoa pessoa : pessoasRecuperadasDeNovo) {
			Log.i("IDADE", "IDADE " + pessoa.getIdade());
			assertEquals(pessoa.getNome(), EDITED);	
			assertEquals(pessoa.getIdade(), IDADE_EDITED);	
			assertTrue(pessoa.isHeterosexual());	
			assertEquals(byteToString(pessoa.getPhoto()), byteToString(PHOTO_DATA_EXAMPLE));
		} 
	}
	
	private String byteToString(byte[] value) {
		StringBuilder stringBuilder = new StringBuilder();
		for(byte val : value) {
			stringBuilder.append(val);	
		}
		
		return stringBuilder.toString();
	}
	
	private void saveAPerson() {
		OutraPessoa pessoa = createOutraPessoa();
		OutraPessoaDAO.getInstance(getContext()).save(pessoa);
	}
	
	
	private OutraPessoa createOutraPessoa() {
		return createOutraPessoa("Leonardo Viana");
	}
	 
	private OutraPessoa createOutraPessoa(String nome) {
		HashMap<String, String> hashMapUm = new HashMap<String, String>();
		hashMapUm.put("Carlos", "Gordinho");
		hashMapUm.put("André", "Castro");
		hashMapUm.put("Rafael", "Silveira");
		OutraPessoa pessoa = new OutraPessoa("Leonardo Viana", hashMapUm);
		byte[] photo = PHOTO_DATA_EXAMPLE;
		pessoa.setPhoto(photo);
		
		pessoa.setIdade(IDADE_INICIAL);
		return pessoa;
	}
	
	
	private List<OutraPessoa> createALotOfPeople() {
		List<OutraPessoa> pessoas = new ArrayList<OutraPessoa>();
		
		pessoas.add(createOutraPessoa("Roberto Carlos"));
		pessoas.add(createOutraPessoa("Erasmo Carlos"));
		pessoas.add(createOutraPessoa("Robertinho"));
		pessoas.add(createOutraPessoa("Carlinhos"));
		
		return pessoas;
	}

}