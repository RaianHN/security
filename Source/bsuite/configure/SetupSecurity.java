package bsuite.configure;

import bsuite.utility.Utility;
import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;

 /**
  * [This class is used in setting up the security system in the new environment, setup method is used to create required documents and databases]
  *@author JPrakash
  *@created Dec 5, 2012
 */

public class SetupSecurity extends CreateDatabase{
	
	//1. Create relation.nsf in the same folder
	//
	@SuppressWarnings("unused")
	private boolean createRelationDb(){
		
		return true;
	}
	@SuppressWarnings("unused")
	private boolean createRoleDocs(){
		
		return true;
	}
	@SuppressWarnings("unused")
	private boolean createProfileDocs(){
		
		return true;
	}
	@SuppressWarnings("unused")
	private boolean isDBExist(String dbName){
		if(Utility.getDatabase(dbName)!=null){
			return true;
		}else{
			return false;
		}
				
	}
	@SuppressWarnings("unused")
	private boolean isViewExist(Database db, String viewName  ){
		try {
			if(db.getView(viewName)!=null){
				return true;
			}else{
				return false;
			}
		} catch (NotesException e) {
			strLog.append('\n');
			e.printStackTrace();
		}
		return false;
	}
	@SuppressWarnings("unused")
	private boolean isDocExist(Database db, View view, String key){
		return true;
	}
	
	public boolean setup(){
		
		return true;
	}
	
}
