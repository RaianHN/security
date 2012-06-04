package bsuite.weber.security;

import java.util.ArrayList;

import bsuite.weber.jsonparsing.ProfileJson;
import bsuite.weber.model.BsuiteWorkFlow;

public class Profile extends BsuiteWorkFlow {
	private ProfileJson profileJson;

	public Profile() {
		super();
		this.profileJson = null;
	}

	public ArrayList getVisibleModules(){
		
		return null;
	}

	public ArrayList getCreatableEntities(String moduleName){
		return null;
	}


	public ArrayList getReadableFields(String moduleName, String entityName){
		return null;
	}
	public ArrayList getEditableFields(String moduleName, String entityName){
		return null;
	}

	public ArrayList getVisibleFeatures(String moduleName, String entityName){
		return null;
	}

	public boolean isEntityRead(String moduleName, String entityName){
		return false;
	}
	public boolean isEntityUpdate(String moduleName, String entityName) {
		return false;
	}
	public boolean isEntityDelete(String moduleName, String entityName) {
		return false;
	}

	
	
	
}
