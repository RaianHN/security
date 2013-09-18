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

public class CreateDatabase
{
	public StringBuilder strLog;

	public Database createDB(String dbName)
	{
		try
		{
			System.out.println("inside CreateDB mehtod");
			// Session session=Utility.getCurrentSession();
			// //NotesFactory.createSession();

			Database db1 = null;
			@SuppressWarnings("unused")
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
				//System.out.println("Current dbpth,"+fulldb);
				//System.out.println("dbpath,"+dbpath);
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
				System.out.println("Not found, creating db");
				db1 = dir.createDatabase(dbpath);
				db1.setTitle(dbName);
				View view1 = db1.createView("AllDocuments");
				view1.setSelectionFormula("SELECT @All");
			}
			else
				System.out.println("found, not creating db");

			return db1;

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return null;
	}

	

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
				System.out.println("View Exists, not creting");
				return db.getView(viewName);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	public View createView(Database db, String viewName, String selFormula)
	{

		try
		{
			if (!db.isOpen())
			{
				db.open();
			}
			// String selFormula
			System.out.println("View Creating");
			if (db.getView(viewName) != null)
			{
				System.out.println("View Exists, not creting");
				return db.getView(viewName);
			}

			View view1 = db.createView(viewName);
			view1.setSelectionFormula(selFormula);

			System.out.println("View Created");
			return view1;

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void createViewColumn(View view, int pos, String title,
			String formula)
	{
		try
		{
			System.out.println("inside createViewColumn");
			System.out.println("View Name " + view.getName());
			System.out.println("View Column Pos " + pos);
			System.out.println("View Column title " + title);
			System.out.println("View Column Formula  " + formula);
			Vector columns = view.getColumns();
			if (!(columns.contains(title)))
			{
				view.createColumn(pos, title, formula);
			}

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unchecked")
	public void createDatabases(Vector<String> modules)
	{
		DefineModule dmodule = new DefineModule();
		View view1 = null;
		for (String x : modules)
		{
			Database db = createDB(x);
			
			System.out.println("2nd part");
			ArrayList<Entity> entityList = dmodule.getEntities(x);
			System.out.println("2nd part  1111");

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
			else
			{
				System.out.println("Entities1 is null");
			}
		}
	}

	public void RegisterEmployee(String name)
	{
		try
		{
			System.out.println("1");
			Document persondoc = getPerson(name);
			String personDocId = persondoc.getUniversalID();
			System.out.println("UNID " + personDocId);
			

			String src_data = name;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data = "Admin";
			Database securityDb = Utility.getDatabase("Security.nsf");
			System.out.println("2");
			View profileview = securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid = profiledoc.getUniversalID();
			String srcunid = personDocId;
			System.out.println("3");
			//creating relationship between person and admin profile
			createRelationship(srcunid, "admntool.nsf", src_data, securityDb
					.getFileName(), trg_data, targetid, relationid);
			System.out.println("4");
			//Create Role Association with the Person
			System.out.println("in role assoc" + name);
			createRoleAssociation(name, "CEO");

		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	public void createRoleAssociation(String username, String roleName)
	{

		String src_data = username;
		System.out.println("role1 username " + src_data);
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

			System.out.println("role1 persondocid " + personDocId);
			String relationid = getRelationNameUnid("HAS_ROLE");
			System.out.println("role2 relationid " + relationid);
			String rolename = roleName;
			System.out.println("role3 " + rolename);
			Database securityDb = Utility.getDatabase("Security.nsf");
			View roleview = securityDb.getView("RolesView");
			System.out.println("role4 " + roleview.getName());
			Document roledoc = roleview.getDocumentByKey(rolename);
			String roleunid = roledoc.getUniversalID();
			System.out.println("role5 " + roleunid);
			System.out.println("role association details");
			System.out.println(personDocId + " " + src_data + " "
					+ securityDb.getFileName() + " " + rolename + " "
					+ relationid);
			createRelationship(personDocId, "admntool.nsf", src_data,
					securityDb.getFileName(), rolename, roleunid, relationid);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

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
			// TODO: handle exception
		}
		return null;
	}

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
			// TODO: handle exception
		}

	}

	@SuppressWarnings("unused")
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
			// TODO: handle exception
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public void createDatabases()
	{
		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		//CreateDatabase cd = new CreateDatabase();
		//cd.createDatabases(moduleNames);
		createDatabases(moduleNames);

	}

	public void deploy()
	{
		createDatabases();//Create databases for modules

	}

	public void createEmployee(String personName, String edocid,
			String profileName, String role)
	{
		try
		{
			Document persondoc = getPerson(personName);
			String personDocId = persondoc.getUniversalID();
			System.out.println("UNID " + personDocId);
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
			// TODO: handle exception
		}

	}

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

	public void createRoleProfileAssociation(String personName,
			String profileName, String roleName)
	{
		createProfileAssociation(personName, profileName);
		createRoleAssociation(personName, roleName);
	}
}
