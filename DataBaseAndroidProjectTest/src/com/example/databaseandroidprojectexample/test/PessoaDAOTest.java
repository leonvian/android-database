package com.example.databaseandroidprojectexample.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.databaseandroidprojectexample.Pessoa;
import com.example.databaseandroidprojectexample.PessoaDAO;
import com.lvc.database.NoElementFoundException;

public class PessoaDAOTest extends AndroidTestCase {

	public static final String FORMAT_DATE = "yyyy-MM-dd HH:mm:ss";
	private static final String EDITED = "EDITADED";
	private static final byte[] PHOTO_DATA_EXAMPLE = {1,2,3,4,5,6,7,8};
	private static final byte IDADE_INICIAL = 12;
	private static final byte IDADE_EDITED = 22;
	
	private static final byte IDADE_MENOR = 16;
	private static final byte IDADE_MENOR_2 = 15;
	private static final byte IDADE_MAIOR = 24;
	byte[] photo = {1,2,3,4,5,6};

	Pessoa pessoaUm = new Pessoa("Marcus", createHashMap(), createHashMap(), photo, IDADE_INICIAL, true);
	Pessoa pessoaDois = new Pessoa("Calixto", createHashMap(), createHashMap(), photo, IDADE_INICIAL, true);
	
	Pessoa pessoaMenorIdade = new Pessoa("Marcus", createHashMap(), createHashMap(), photo, IDADE_MENOR, true);
	Pessoa pessoaMenorIdadeDois = new Pessoa("Marcus", createHashMap(), createHashMap(), photo, IDADE_MENOR_2, true);
	Pessoa pessoaMaior = new Pessoa("Calixto", createHashMap(), createHashMap(), photo, IDADE_MAIOR, true);

	
	
	public void testInsert() {
		deleteAllPeople();
		
		PessoaDAO.getInstance(getContext()).insert(pessoaUm);
		assertEquals(1, PessoaDAO.getInstance(getContext()).count());
		PessoaDAO.getInstance(getContext()).insert(pessoaUm);
		assertEquals(2, PessoaDAO.getInstance(getContext()).count());
	}
	
	public void testSaveOrUpdate() {
		deleteAllPeople();
		
		PessoaDAO.getInstance(getContext()).saveOrUpdade(pessoaUm);
		assertEquals(1, PessoaDAO.getInstance(getContext()).count());
		PessoaDAO.getInstance(getContext()).saveOrUpdade(pessoaUm);
		assertEquals(1, PessoaDAO.getInstance(getContext()).count());
	}
	
	public void testSaveAndRetreiveDate() {
		
		PessoaDAO.getInstance(getContext()).deleteAll();
		
		Pessoa pessoaUm = new Pessoa("Marcus", createHashMap(), createHashMap(), photo, IDADE_INICIAL, true);
		Calendar calendar = Calendar.getInstance();
		calendar.set(1990, 02, 01, 12, 00);
		Date dataNascimento = calendar.getTime();
		pessoaUm.setDataNascimento(dataNascimento);
		
		PessoaDAO.getInstance(getContext()).save(pessoaUm);
		
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 1);
		
		Pessoa pessoaRecuperada = PessoaDAO.getInstance(getContext()).getAllElements().get(0);
		
		assertEquals(toDateTime(pessoaUm.getDataNascimento()),toDateTime(pessoaRecuperada.getDataNascimento()));
		
		//WHERE my_date >= #2008-09-01 00:00:00#  AND my_date < #2010-09-01 00:00:00#;
		assertEquals(0,PessoaDAO.getInstance(getContext()).getElementsByWhereClause("dataNascimento > '2008-09-01 00:00:00'").size());
		assertEquals(1,PessoaDAO.getInstance(getContext()).getElementsByWhereClause("dataNascimento > '1980-09-01 00:00:00'").size());
		
	}
	
	
	public void testDeleteByQueryPassingWhere() {
		deleteAllPeople();		
		saveMarcusAndCalixto();
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 2);
		PessoaDAO.getInstance(getContext()).deleteByQueryPassingWhere("nome = 'Marcus'");
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 1);
	}

	public void testDeleteByQueryPassingWhereAndWhereArgs() {
		deleteAllPeople();		
		saveMarcusAndCalixto();
		
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 2);
		PessoaDAO.getInstance(getContext()).deleteByQueryPassingWhere("nome = ?", new String[] {"Marcus"});
		assertTrue(PessoaDAO.getInstance(getContext()).count() == 1);
	}

	private void saveMarcusAndCalixto() {
		PessoaDAO.getInstance(getContext()).save(pessoaUm);
		PessoaDAO.getInstance(getContext()).save(pessoaDois);

	}

	//public List<T> getElements(String where, String[] whereArgs, String orderBy, int limit) throws AndroidDataBaseException  {
	public void testQueryByAPI() {
		deleteAllPeople();

		PessoaDAO.getInstance(getContext()).save(pessoaMenorIdade);
		PessoaDAO.getInstance(getContext()).save(pessoaMenorIdadeDois);
		PessoaDAO.getInstance(getContext()).save(pessoaMaior);
		
		String[] whereArgs = {
				"Marcus"
		};
		
		List<Pessoa> pessoas = PessoaDAO.getInstance(getContext()).getElements("nome = ?", whereArgs, "idade", "1");
				
		assertEquals(pessoas.size(), 1);
		Pessoa pessoaRetrieved = pessoas.get(0);
		verifyIfSameAttributes("testQueryByAPI()",pessoaRetrieved, pessoaMenorIdadeDois);
	}

	public void testQueryByWhereClauseSelectionArgs() {
		deleteAllPeople();

		Pessoa pessoaToSave = createPessoa();
		PessoaDAO.getInstance(getContext()).save(pessoaToSave);
		//.getElementsPassingWhereClause("nome = ''");
		List<Pessoa> pessoas = PessoaDAO.getInstance(getContext()).getElementsByWhereClause("nome = ? and idade = ?", new String[] {
				"Leonardo Viana",
				String.valueOf(IDADE_INICIAL)
		});
		assertEquals(pessoas.size(), 1);
		Pessoa pessoaRetrieved = pessoas.get(0);
		verifyIfSameAttributes("testQueryByWhereClauseSelectionArgs()",pessoaRetrieved, pessoaToSave);
	}


	public void testQueryByWhereClause() {
		deleteAllPeople();

		Pessoa pessoaToSave = createPessoa();
		PessoaDAO.getInstance(getContext()).save(pessoaToSave);

		List<Pessoa> pessoas = PessoaDAO.getInstance(getContext()).getElementsByWhereClause("nome = 'Leonardo Viana'");
		assertEquals(pessoas.size(), 1);
		Pessoa pessoaRetrieved = pessoas.get(0);

		verifyIfSameAttributes("testQueryByWhereClause()",pessoaRetrieved, pessoaToSave);
	}

	private void verifyIfSameAttributes(String methodName, Pessoa pessoaOne, Pessoa pessoaTwo) {
		assertNotNull(methodName,pessoaOne.getHashMapUm());
		assertEquals(methodName,pessoaOne.getHashMapUm(), pessoaTwo.getHashMapUm());

		assertEquals(methodName,pessoaOne.getIdade(), pessoaTwo.getIdade());

		assertNotNull(methodName,pessoaOne.getHashMapDois());
		assertEquals(methodName,pessoaOne.getHashMapDois(), pessoaTwo.getHashMapDois());

		assertEquals(methodName,pessoaOne, pessoaTwo);
	}

	public void testCount() {
		PessoaDAO.getInstance(getContext()).deleteAll();

		saveToValidatePerformance();

		assertEquals(TestConfiguration.NUMBER_REPETITION, PessoaDAO.getInstance(getContext()).count());
		
		PessoaDAO.getInstance(getContext()).deleteAll();
		
		assertEquals(0, PessoaDAO.getInstance(getContext()).count());
	}

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

	public void testRetrieveGetElementsRawQuery() {
		deleteAllPeople();
		saveAPerson();
		PessoaDAO dao = PessoaDAO.getInstance(getContext());

		Pessoa pessoa = dao.getElementsRawQuery("select * from " + dao.getTableName() + " where nome = ?", new String[] {"Leonardo Viana"}).get(0);
		assertNotNull(pessoa);
		assertTrue(pessoa.getIdade() == IDADE_INICIAL);
		assertTrue(pessoa.getNome().equals("Leonardo Viana"));
	}
	
	public void testRetrieveGetElements() {
		deleteAllPeople();
		saveAPerson();
		PessoaDAO dao = PessoaDAO.getInstance(getContext());

		Pessoa pessoa = dao.getElements("nome = ?", new String[] {"Leonardo Viana"}).get(0);
		
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
		HashMap<String, String> hashMapUm = createHashMap();
		Pessoa pessoa = new Pessoa("Leonardo Viana", hashMapUm);
		byte[] photo = PHOTO_DATA_EXAMPLE;
		pessoa.setPhoto(photo);
		pessoa.setHashMapDois(hashMapUm);

		pessoa.setIdade(IDADE_INICIAL);
		return pessoa;
	}

	private HashMap<String, String> createHashMap() {
		HashMap<String, String> hashMapUm = new HashMap<String, String>();
		hashMapUm.put("Carlos", "Gordinho");
		hashMapUm.put("André", "Castro");
		hashMapUm.put("Rafael", "Silveira");
		return hashMapUm;
	}


	private List<Pessoa> createALotOfPeople() {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();

		pessoas.add(createPessoa("Roberto Carlos"));
		pessoas.add(createPessoa("Erasmo Carlos"));
		pessoas.add(createPessoa("Robertinho"));
		pessoas.add(createPessoa("Carlinhos"));

		return pessoas;
	}
	
	private String toDateTime(Date data) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE); 
		String dateTime = dateFormat.format(data);

		return dateTime;
	}

	private Date toDateTime(String data) {
		if(data == null)
			return null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE); 
			Date dateTime = dateFormat.parse(data);

			return dateTime;	
		} catch(ParseException e) {
			e.printStackTrace();
			return null;
		} 
	}

}
