<h1>Database Android Project</h1>

It's a simple framework for encapsulation of database part in a project.

<h2>How to Use</h2>

Create your entities class and make them implement EntitiePersistable interface.

public class Person implements EntitiePersistable {
	
	private static final long serialVersionUID = -7229425725674363296L;
	
	@PrimaryKey
	private int id;
	private String name;
	private int age;
	
	public Person() {
	}
	
// getters and setters	
}

Now, for each entitie class you have to create a DAO class to encapsulate persistence responsabilities.

public class PersonDAO extends BaseDAO<Person>{

	public PersonDAO(Context context) {
		super(context);
	}

	@Override
	public SQLiteOpenHelper getDataBaseHelper() {
		return new OpenHelper(getContext());
	}

	@Override
	public Class<Person> getEntitieClass() {
		return Person.class;
	} 
}

A example of a implementation of OpenHelperDatabase class using the framework.


public class MyDatabaseHelper extends DatabaseHelper {
	
	private static final String DATABASE_NAME = "PersonEntry";
	private static final int DATABASE_VERSION = 1;

	public MyDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, DATABASE_VERSION); 
	}
	 
	@Override
	public String[] getScriptsCreateDataBase() throws ReflectionException {
	 
		   String[] creationScript = {
				TableUtils.createTableScript(Person.class)	
			};
			
			return creationScript;
	}

	@Override
	public String[] getScriptsUpdateDataBase() throws ReflectionException {
		
		String[] updateScript = {
			TableUtils.createDropTableScript(Person.class)	
		};
		
		return updateScript;
	}
}

In your project you just have to instantiate PersonDAO and use the utilities methods to save, delete, retrieve, and edit data.

Note: This project should be a good idea for small projects. Big projects requires more elaborate projects. 

