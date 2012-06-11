package bsuite.weber.configure;

import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.DbDirectory;
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
				if (fn.equalsIgnoreCase(dbName.replace(" ","") + ".nsf")) {
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
				view1.setSelectionFormula("SELECT @ALL");
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

	public void createView(Database db, String viewName, String selFormula) {
		
		try {
			if(!db.isOpen()){
			db.open();
			}
			// String selFormula
			System.out.println("View Creating");
		View view1=db.createView(viewName);
		view1.setSelectionFormula(selFormula);

			System.out.println("View Created");

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void createDatabases(Vector<String> modules) {
		DefineModule dmodule = new DefineModule();
		for (String x : modules) {
			Database db = createDB(x);
			
			Vector<String> entities = dmodule.getEntityNames(x);
			if (entities != null) {
				for (String entity : entities) {
					System.out.println("ENtity: " + entity);
					 String selFormula="SELECT Form="+entity+"\"";
					
					createView(db, entity, selFormula);
				}
			} else {
				System.out.println("No eneties");
			}
		}
	}

}
