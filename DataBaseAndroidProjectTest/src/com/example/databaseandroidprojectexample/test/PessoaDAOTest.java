package com.example.databaseandroidprojectexample.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.databaseandroidprojectexample.Pessoa;
import com.example.databaseandroidprojectexample.PessoaDAO;
import com.lvc.database.NoElementFoundException;

public class PessoaDAOTest extends AndroidTestCase {

	private static final String EDITED = "EDITADED";
	private static final byte[] PHOTO_DATA_EXAMPLE = {1,2,3,4,5,6,7,8};
	private static final byte IDADE_INICIAL = 12;
	private static final byte IDADE_EDITED = 22;
	
	
	public void testSaveIds() {
		deleteAllPeople();
		
		Pessoa pessoa = createPessoa();
		Long id = PessoaDAO.getInstance(getContext()).save(pessoa);
		assertEquals(pessoa.getId(), id);
		
		Long idRetrieved = PessoaDAO.getInstance(getContext()).getAllElements().get(0).getId();
		assertEquals(pessoa.getId(), idRetrieved);
	}
	
	public void testReturnOneElement() throws NoElementFoundException {
		deleteAllPeople();
		saveAPerson();
	    Pessoa pessoa = PessoaDAO.getInstance(getContext()).retrieveOneElementOrThrowException("nome = 'Leonardo Viana'");
	    assertNotNull(pessoa);
	    assertEquals(createPessoa(), pessoa);
	    
	    try {
	    	Pessoa pessoa2 = PessoaDAO.getInstance(getContext()).retrieveOneElementOrThrowException("nome = 'Leonardo'");	
	    } catch (NoElementFoundException e) {
	    	assertTrue(true);
	    } 
	}
	
	public void testRetrieveGetElements() {
		deleteAllPeople();
		saveAPerson();
		PessoaDAO dao = PessoaDAO.getInstance(getContext());
	
		Pessoa pessoa = dao.getElements("select * from " + dao.getTableName() + " where nome = ?", new String[] {"Leonardo Viana"}).get(0);
		assertNotNull(pessoa);
		assertTrue(pessoa.getIdade() == IDADE_INICIAL);
		assertTrue(pessoa.getNome().equals("Leonardo Viana"));
	}
	
	public void testSavePerformance() {
		deleteAllPeople(); 
		saveToValidatePerformance();  
	}
	
	public void testRetrievePerformance() {
		deleteAllPeople(); 
		saveToValidatePerformance();
		
		List<Pessoa> pessoas = PessoaDAO.getInstance(getContext()).getAllElements();
		assertEquals(TestConfiguration.NUMBER_REPETITION, pessoas.size());
	}
	
	private void saveToValidatePerformance() {
		for(int i = 0; i < TestConfiguration.NUMBER_REPETITION; i ++) {
			saveAPerson();
		}
	} 
	
	public void testDeleteSimple() {
		testSaveALotOFPeople();
		
		List<Pessoa> peopleRetrieved = PessoaDAO.getInstance(getContext()).getAllElements();
		
		for(Pessoa pessoa : peopleRetrieved) {
			PessoaDAO.getInstance(getContext()).delete(pessoa);
		}
		
	}
	
	public void testSaveALotOFPeople() {
		deleteAllPeople();
		
		List<Pessoa> newBornPeople = createALotOfPeople();
		PessoaDAO.getInstance(getContext()).saveAll(newBornPeople);
		
		List<Pessoa> peopleRetrieved = PessoaDAO.getInstance(getContext()).getAllElements();
		
		assertTrue(peopleRetrieved.size() == newBornPeople.size());
		
		assertTrue(newBornPeople.containsAll(peopleRetrieved));
	}
	 
	public void testSave() {
		deleteAllPeople();
	    long id = saveAPerson();
	    int count = PessoaDAO.getInstance(getContext()).count();
		assertTrue(count == 1);
		
		List<Pessoa> pessoas = PessoaDAO.getInstance(getContext()).getAllElements();
		
		Log.i("PESSOA", "ID: " + id + " PESSOAS = " + pessoas.get(0).getId());
	}
	 
	
	public void testDelete() {
	    saveAPerson();
		deleteAllPeople();
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 0);
	}
	
	private void deleteAllPeople() {
		PessoaDAO.getInstance(getContext()).deleteAll();
	}
	
	public void testUpdate() {
		
		testDelete();
		testSave();
		
		List<Pessoa> pessoasRecuperadas = PessoaDAO.getInstance(getContext()).getAllElements();
		for(Pessoa pessoa : pessoasRecuperadas) {
			
			assertEquals(pessoa.getIdade(), IDADE_INICIAL);
			Log.i("IDADE", "IDADE " + pessoa.getIdade());
			assertFalse(pessoa.isHeterosexual());
			pessoa.setNome(EDITED);
			pessoa.setHeterosexual(true);
			pessoa.setIdade(IDADE_EDITED); 
			PessoaDAO.getInstance(getContext()).update(pessoa);	
			
			assertEquals(byteToString(pessoa.getPhoto()), byteToString(PHOTO_DATA_EXAMPLE));
		}
		
		List<Pessoa> pessoasRecuperadasDeNovo = PessoaDAO.getInstance(getContext()).getAllElements();
		for(Pessoa pessoa : pessoasRecuperadasDeNovo) {
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
	
	private long saveAPerson() {
		Pessoa pessoa = createPessoa();
		return PessoaDAO.getInstance(getContext()).save(pessoa);
	}
	
	
	private Pessoa createPessoa() {
		return createPessoa("Leonardo Viana");
	}
	 
	private Pessoa createPessoa(String nome) {
		HashMap<String, String> hashMapUm = new HashMap<String, String>();
		hashMapUm.put("Carlos", "Gordinho");
		hashMapUm.put("Andr��", "Castro");
		hashMapUm.put("Rafael", "Silveira");
		Pessoa pessoa = new Pessoa("Leonardo Viana", hashMapUm);
		byte[] photo = PHOTO_DATA_EXAMPLE;
		pessoa.setPhoto(photo);
		
		pessoa.setIdade(IDADE_INICIAL);
		return pessoa;
	}
	
	
	private List<Pessoa> createALotOfPeople() {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		pessoas.add(createPessoa("Roberto Carlos"));
		pessoas.add(createPessoa("Erasmo Carlos"));
		pessoas.add(createPessoa("Robertinho"));
		pessoas.add(createPessoa("Carlinhos"));
		
		return pessoas;
	}

}
