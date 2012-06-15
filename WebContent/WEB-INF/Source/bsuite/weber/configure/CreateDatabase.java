package bsuite.weber.configure;

import java.util.ArrayList;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DbDirectory;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.View;
import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.tools.BSUtil;

import com.ibm.security.pkcs7.Data;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class CreateDatabase extends BsuiteWorkFlow {

	public Database createDB(String dbName) {
		try {
			System.out.println("inside CreateDB mehtod");
			// Session session=ExtLibUtil.getCurrentSession();
			// //NotesFactory.createSession();

			Database db1 = null;
			Database currentdb = ExtLibUtil.getCurrentDatabase();
			String path = BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			boolean found = false;
			String dbpath = path + dbName.toLowerCase().replace(" ", "");

			DbDirectory dir = session.getDbDirectory(null);

			Database db = dir.getFirstDatabase(DbDirectory.DATABASE);
			while (db != null) {
				String fn = db.getFileName();
				String currentpath = BSUtil.getBsuitePath(db);
				String fulldb=currentpath+fn;
				//System.out.println("Current dbpth,"+fulldb);
				//System.out.println("dbpath,"+dbpath);
				//fn.equalsIgnoreCase(dbName.replace(" ","") + ".nsf")
				if (fulldb.equalsIgnoreCase(dbpath+".nsf")) {
					found = true;
					db1 = db;
					break;
				}
				db = dir.getNextDatabase();
			}
			if (!found) {
				System.out.println("Not found, creating db");
				db1 = dir.createDatabase(dbpath);
				db1.setTitle(dbName);
				View view1 = db1.createView("AllDocuments");
				view1.setSelectionFormula("SELECT @All");
			} else
				System.out.println("found, not creating db");

			return db1;

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/*
	 * public void createIndex(Database db){
	 * System.out.println("Inside createIndex method"); int options =
	 * Database.FTINDEX_ALL_BREAKS + Database.FTINDEX_CASE_SENSITIVE; try{
	 * 
	 * if (db.isFTIndexed()) { System.out.println("Db is indexed");
	 * db.createFTIndex(options, true);
	 * System.out.println("Database index recreated"); } else {
	 * System.out.println("Db is not indexed"); db.createFTIndex(options,
	 * false); System.out.println("New database index created"); }
	 * 
	 * }catch (Exception e) { // TODO: handle exception }
	 * 
	 * 
	 * }
	 */

	public View createView(Database db, String viewName, String selFormula) {
		
		try {
			if(!db.isOpen()){
			db.open();
			}
			// String selFormula
			System.out.println("View Creating");
			if(db.getView(viewName)!=null){
				System.out.println("View Exists, not creting");
				return db.getView(viewName);
			}
				
		View view1=db.createView(viewName);
		view1.setSelectionFormula(selFormula);

			System.out.println("View Created");
			return view1;

		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}
	
	public void createViewColumn(View view,int pos,String title,String formula){
		try{
			System.out.println("inside createViewColumn");
			System.out.println("View Name "+view.getName());
			System.out.println("View Column Pos "+pos);
			System.out.println("View Column title "+title);
			System.out.println("View Column Formula  "+formula);
			view.createColumn(pos,title,formula);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

public void createDatabases(Vector<String> modules) {
		DefineModule dmodule = new DefineModule();
		View view1=null;
		for (String x : modules) 
		{
			Database db = createDB(x);			
			/*Vector<String> entities = dmodule.getEntityNames(x);
			if (entities != null) 
			{
				for (String entity : entities) 
				{
					System.out.println("ENtity: " + entity);
					 String selFormula="SELECT Form=\""+entity+"\"";		
					 System.out.println("View Selection Formula " + selFormula);
					 view1= createView(db, entity, selFormula);					
				}
			} else
				{
				System.out.println("No eneties");
				}*/
			System.out.println("2nd part");
			ArrayList<Entity> entityList=dmodule.getEntities(x);
			System.out.println("2nd part  1111");
			
			if (entityList != null) 
			{
				for(Entity e : entityList)
				{
					ArrayList<Field> fields=e.getFields();
					String selFormula="SELECT Form=\""+e.getEntityName()+"\"";
					view1=createView(db,e.getEntityName(), selFormula);
					for(int i=1;i<=fields.size();i++)
					{
						createViewColumn(view1,i,(String)fields.get(i-1).getFieldName(),(String)fields.get(i-1).getFieldName());
					}
				}
			}else
				{
					System.out.println("Entities1 is null");
				}
		}
	}
	public void RegisterEmployee(String name){
		try{
		Document persondoc=getPerson(name);
		String personDocId=persondoc.getUniversalID();
	System.out.println("UNID "+personDocId);
			Database empdb = session.getDatabase("", bsuitepath
					+ "employees.nsf");
			
			Document empdoc=empdb.createDocument();
			empdoc.replaceItemValue("Form","Employee");
			empdoc.save(true,false);	
		
			String relId = getRelationNameUnid("IS_A");
			createRelationship(persondoc.getUniversalID(),"names.nsf","Person","Employee.nsf","Employee",empdoc.getUniversalID(),relId);
			
			
			String src_data=name;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data="Admin";
			Database securityDb  = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			View profileview= securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid=profiledoc.getUniversalID();
			String srcunid=personDocId;
			//creating relationship between person and admin profile
			createRelationship(srcunid,"names.nsf",src_data,securityDb.getFileName(),trg_data,targetid,relationid);
			
			//Create Role Association with the Person
			createRoleAssociation(name,"CEO" );
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	
		
	}
	
	public void createRoleAssociation(String username, String roleName){
		
		String src_data=username;
		Document persondoc=getPerson(username);
		try{	
		
		String personDocId=persondoc.getUniversalID();
		String relationid = getRelationNameUnid("HAS_ROLE");
		String rolename=roleName;
		Database securityDb  = session.getDatabase("", bsuitepath
				+ "Security.nsf");
		View roleview=  securityDb.getView("RolesView");
		Document roledoc= roleview.getDocumentByKey(rolename);
		String roleunid=roledoc.getUniversalID();
		createRelationship(personDocId,"names.nsf",src_data,securityDb.getFileName(),rolename,roleunid,relationid);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private Document getPerson(String username) {
		try {

			Database namesdb = session.getDatabase("", "names.nsf");
			View peopleview = namesdb.getView("($VIMPeople)");
			Document userdoc = peopleview.getDocumentByKey(username);
			return userdoc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public String  getRelationNameUnid(String relationName){
		try{
			 Database relationDb = session.getDatabase("", bsuitepath
						+ "Relation.nsf");
					View relview=relationDb.getView("CategoryRelation");
					Document reldoc= relview.getDocumentByKey(relationName);
					return reldoc.getUniversalID();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
		}
	
	
	
	public void createRelationship(String srcunid,String sourcedb,String src_data,String targetdb,String trg_data,String targetid,String relationid){
	
		try{
			 Database relationDb = session.getDatabase("", bsuitepath
						+ "Relation.nsf");
				
			 Document reldoc=relationDb.createDocument();
			 reldoc.replaceItemValue("Form","association");
			 reldoc.replaceItemValue("sourceid", srcunid);
			reldoc.replaceItemValue("sourcedb",sourcedb);
			reldoc.replaceItemValue("src_data", src_data);
			reldoc.replaceItemValue("targetdb",targetdb);
			reldoc.replaceItemValue("trg_data",trg_data);
			reldoc.replaceItemValue("targetid",targetid);
			
		reldoc.replaceItemValue("relationid",relationid);
			reldoc.save(true,false);
			 
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	
	}
	private String getFormattedName(String currentuser, String param) {
		try {			

			Name user = session.createName(currentuser);
			if (param.equals("abr")) {
				return user.getAbbreviated();
			}

			else if (param.equals("canonical")) {
				return user.getCanonical();
			} else if (param.equals("common")) {
				return user.getCommon();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void createDatabases(){
		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		//CreateDatabase cd = new CreateDatabase();
		//cd.createDatabases(moduleNames);
		createDatabases(moduleNames);
		
	}
	public void deploy(){
		createDatabases();//Create databases for modules
		
		
	}
	public void createEmployee(String personName,String edocid,String profileName, String role){
		try{
		Document persondoc=getPerson(personName);
		String personDocId=persondoc.getUniversalID();
	System.out.println("UNID "+personDocId);
			Database empdb = session.getDatabase("", bsuitepath
				+ "employees.nsf");
			
			//Document empdoc=empdb.createDocument();
			//empdoc.replaceItemValue("Form","Employee");
			//empdoc.save(true,false);	
			Document empdoc = empdb.getDocumentByUNID(edocid);
			String relId = getRelationNameUnid("IS_A");
			createRelationship(persondoc.getUniversalID(),"names.nsf","Person","Employee.nsf","Employee",empdoc.getUniversalID(),relId);
			
			
			String src_data=personName;
			String relationid = getRelationNameUnid("HAS_A");
			String trg_data=profileName;
			Database securityDb  = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			View profileview= securityDb.getView("ProfileView");
			Document profiledoc = profileview.getDocumentByKey(trg_data);
			String targetid=profiledoc.getUniversalID();
			String srcunid=personDocId;
			//creating relationship between person and admin profile
			createRelationship(srcunid,"names.nsf",src_data,securityDb.getFileName(),trg_data,targetid,relationid);
			
			//Create Role Association with the Person
			createRoleAssociation(personName, role);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
	
		
	}
}
