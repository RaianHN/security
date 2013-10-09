package bsuite.configure;

import java.util.ArrayList;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DbDirectory;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.View;

import bsuite.relationship.Association;
import bsuite.utility.Utility;

import bsuite.utility.Utility;

@SuppressWarnings("unused")
public class CreateDatabase
{
	public StringBuilder strLog;

	/**Used in configuring dynamic security, creates the database and all documents view
	 *@param dbName name of the databse
	 *@return database
	 */
	public Database createDB(String dbName)
	{
		try
		{
			
			
			

			Database db1 = null;
			Database currentdb = Utility.getCurrentDatabase();
			String path = Utility.getBsuitePath(Utility.getCurrentDatabase());
			boolean found = false;
			String dbpath = path + dbName.toLowerCase().replace(" ", "");

			DbDirectory dir = Utility.getCurrentSession().getDbDirectory(null);

			Database db = dir.getFirstDatabase(DbDirectory.DATABASE);
			while (db != null)
			{
				String fn = db.getFileName();
				String currentpath = Utility.getBsuitePath(db);
				String fulldb = currentpath + fn;
				//
				//
				//fn.equalsIgnoreCase(dbName.replace(" ","") + ".nsf")
				if (fulldb.equalsIgnoreCase(dbpath + ".nsf"))
				{
					found = true;
					db1 = db;
					break;
				}
				db = dir.getNextDatabase();
			}
			if (!found)
			{
				
				db1 = dir.createDatabase(dbpath);
				db1.setTitle(dbName);
				View view1 = db1.createView("AllDocuments");
				view1.setSelectionFormula("SELECT @All");
			}
			else
				

			return db1;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	

	/**Returns the view of a given database
	 *@param db database
	 *@param viewName viewname
	 *@return database
	 */
	public View getView(Database db, String viewName)
	{
		try
		{
			if (!db.isOpen())
			{
				db.open();
			}

			if (db.getView(viewName) != null)
			{
				
				return db.getView(viewName);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**Creates the view in the given database with the given selection formula
	 *@param db database
	 *@param viewName name of the view
	 *@param selFormula view selection formula
	 *@return view
	 */
	public View createView(Database db, String viewName, String selFormula)
	{

		try
		{
			if (!db.isOpen())
			{
				db.open();
			}
			// String selFormula
			
			if (db.getView(viewName) != null)
			{
				
				return db.getView(viewName);
			}

			View view1 = db.createView(viewName);
			view1.setSelectionFormula(selFormula);

			
			return view1;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**Creates a column in the given view with the given position and title
	 *@param view view object
	 *@param pos position
	 *@param title title
	 *@param formula column formula
	 */
	@SuppressWarnings("unchecked")
	public void createViewColumn(View view, int pos, String title,
			String formula)
	{
		try
		{		
			Vector columns = view.getColumns();
			if (!(columns.contains(title)))
			{
				view.createColumn(pos, title, formula);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**To create databases for list of module names passed and view will be created with the view selection formula,
	 * for all the entities present in the view
	 *@param modules list of moudule names
	 */
	@SuppressWarnings("unchecked")
	public void createDatabases(Vector<String> modules)
	{
		DefineModule dmodule = new DefineModule();
		View view1 = null;
		for (String x : modules)
		{
			Database db = createDB(x);
			
			
			ArrayList<Entity> entityList = dmodule.getEntities(x);
			

			if (entityList != null)
			{
				for (Entity e : entityList)
				{
					ArrayList<Field> fields = e.getFields();
					String selFormula = "SELECT Form=\"" + e.getEntityName()
							+ "\"";
					view1 = createView(db, e.getEntityName(), selFormula);
					for (int i = 1; i <= fields.size(); i++)
					{
						createViewColumn(view1, i, (String) fields.get(i - 1)
								.getFieldName(), (String) fields.get(i - 1)
								.getFieldName());
					}
				}
			}
			
		}
	}

	/**When an employee is registerd, ceo and admin will be associated.
	 *@param name name of the employee
	 */
	public void RegisterEmployee(String name)
	{
		try
		{
			
			Document persondoc = getPerson(name);
			String personDocId = persondoc.getUniversalID();
			
			

			String src_data = name;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data = "Admin";
			Database securityDb = Utility.getDatabase("Security.nsf");
			
			View profileview = securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid = profiledoc.getUniversalID();
			String srcunid = personDocId;
			
			//creating relationship between person and admin profile
			createRelationship(srcunid, "admntool.nsf", src_data, securityDb
					.getFileName(), trg_data, targetid, relationid);
			
			//Create Role Association with the Person
			
			createRoleAssociation(name, "CEO");

		}
		catch (Exception e)
		{
		}

	}

	/**Associates given user to the given role
	 *@param username name of the user
	 *@param roleName rolename
	 */
	public void createRoleAssociation(String username, String roleName)
	{

		String src_data = username;
		
		Document persondoc = getPerson(username);
		String personDocId = "";
		try
		{
			personDocId = persondoc.getUniversalID();
		}
		catch (NotesException e1)
		{
			e1.printStackTrace();
		}
		Association as = new Association();
		String rolen = as.getAssociatedRoleName(username);
		if (rolen != null)
		{
			as.deleteRoleDoc(personDocId);
		}
		try
		{

			
			String relationid = getRelationNameUnid("HAS_ROLE");
			
			String rolename = roleName;
			
			Database securityDb = Utility.getDatabase("Security.nsf");
			View roleview = securityDb.getView("RolesView");
			
			Document roledoc = roleview.getDocumentByKey(rolename);
			String roleunid = roledoc.getUniversalID();
			
			
			
					
			createRelationship(personDocId, "admntool.nsf", src_data,
					securityDb.getFileName(), rolename, roleunid, relationid);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**Gets the person document
	 *@param username name of the user
	 *@return person document from admntool.nsf
	 */
	private Document getPerson(String username)
	{
		try
		{

			Database namesdb = Utility.getDatabase("admntool.nsf");
			View peopleview = namesdb.getView("employeeprofile");
			Document userdoc = peopleview.getDocumentByKey(username);
			return userdoc;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**Returns the unid of the given relation
	 *@param relationName hasa or isa
	 *@return unid
	 */
	public String getRelationNameUnid(String relationName)
	{
		try
		{
			Database relationDb = Utility.getDatabase("Relation.nsf");

			View relview = relationDb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relationName);
			return reldoc.getUniversalID();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**Creates the relationshp document with the given data
	 *@param srcunid source unid
	 *@param sourcedb source database
	 *@param src_data src data
	 *@param targetdb target database
	 *@param trg_data target database
	 *@param targetid target id
	 *@param relationid relation unid
	 */
	public void createRelationship(String srcunid, String sourcedb,
			String src_data, String targetdb, String trg_data, String targetid,
			String relationid)
	{

		try
		{

			Database relationDb = Utility.getDatabase("Relation.nsf");
			Document reldoc = relationDb.createDocument();
			reldoc.replaceItemValue("Form", "association");
			reldoc.replaceItemValue("sourceid", srcunid);
			reldoc.replaceItemValue("sourcedb", sourcedb);
			reldoc.replaceItemValue("src_data", src_data);
			reldoc.replaceItemValue("targetdb", targetdb);
			reldoc.replaceItemValue("trg_data", trg_data);
			reldoc.replaceItemValue("targetid", targetid);

			reldoc.replaceItemValue("relationid", relationid);
			reldoc.save(true, false);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**Returns the formatted name for a given name
	 *@param currentuser current user name
	 *@param param format
	 *@return name 
	 */
	private String getFormattedName(String currentuser, String param)
	{
		try
		{

			Name user = Utility.getCurrentSession().createName(currentuser);
			if (param.equals("abr"))
			{
				return user.getAbbreviated();
			}

			else if (param.equals("canonical"))
			{
				return user.getCanonical();
			}
			else if (param.equals("common"))
			{
				return user.getCommon();
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *Creates databases for all the modules
	 */
	@SuppressWarnings("unchecked")
	public void createDatabases()
	{
		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		
		
		createDatabases(moduleNames);

	}

	/**
	 *Create databases for modules

	 */
	public void deploy()
	{
		createDatabases();//Create databases for modules

	}

	/**Creates relationship for person, profile and role
	 *@param personName name of the person
	 *@param edocid person doc unid
	 *@param profileName profilename
	 *@param role role name
	 */
	public void createEmployee(String personName, String edocid,
			String profileName, String role)
	{
		try
		{
			Document persondoc = getPerson(personName);
			String personDocId = persondoc.getUniversalID();
			
			Database empdb = Utility.getDatabase("employees.nsf");

				
			Document empdoc = empdb.getDocumentByUNID(edocid);
			String relId = getRelationNameUnid("IS_A");
			createRelationship(persondoc.getUniversalID(), "admntool.nsf",
					"Person", "Employee.nsf", "Employee", empdoc
							.getUniversalID(), relId);
			//Store the Person Name in the employee document so that user names should be shown while manually sharing the records
			empdoc.replaceItemValue("FullName", personName);
			empdoc.save(true, false);

			String src_data = personName;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data = profileName;
			Database securityDb = Utility.getDatabase("Security.nsf");
			View profileview = securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid = profiledoc.getUniversalID();
			String srcunid = personDocId;
			//creating relationship between person and admin profile
			createRelationship(srcunid, "admntool.nsf", src_data, securityDb
					.getFileName(), trg_data, targetid, relationid);

			//Create Role Association with the Person
			createRoleAssociation(personName, role);

		}
		catch (Exception e)
		{
		}

	}

	/**Associates the person from admntool.nsf to the given profile
	 *@param personName peron name
	 *@param profileName profilename
	 */
	public void createProfileAssociation(String personName, String profileName)
	{
		Document persondoc = getPerson(personName);
		String personDocId = "";
		try
		{
			personDocId = persondoc.getUniversalID();
		}
		catch (NotesException e)
		{
			e.printStackTrace();
		}

		Association as = new Association();
		Document doc = as.getAssociatedProfile(personName);
		if (doc != null)
		{
			as.deleteProfileDoc(personDocId);
		}

		try
		{

			String src_data = personName;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data = profileName;
			Database securityDb = Utility.getDatabase("Security.nsf");
			View profileview = securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid = profiledoc.getUniversalID();
			String srcunid = personDocId;
			//creating relationship between person and admin profile
			createRelationship(srcunid, "admntool.nsf", src_data, securityDb
					.getFileName(), trg_data, targetid, relationid);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**Creates the profile and role relationship for a given username
	 *@param personName name of the person
	 *@param profileName name of the profile
	 *@param roleName rolename
	 */
	public void createRoleProfileAssociation(String personName,
			String profileName, String roleName)
	{
		createProfileAssociation(personName, profileName);
		createRoleAssociation(personName, roleName);
	}
}
