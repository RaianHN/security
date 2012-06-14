package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.NotesException;
import lotus.domino.View;
import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.tools.JSFUtil;

public class Security extends BsuiteWorkFlow {

	private Profile profile;
	private Role role;
	private ArrayList<String> modules;
	private HashMap<String, ArrayList<String>> modulesEntities;
	
	public Security(){
		try {
		//	profile=(Profile)getAssociatedProfile(session.getEffectiveUserName());
			//role = new Role();//getAssociatedRoleName(session.getEffectiveUserName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//modules=profile.getVisibleModulesNames();
		modules.add("EMployees");
		modules.add("Documents");
		
		for(String x:modules){
		ArrayList entities=new ArrayList();	
		entities = profile.getCreatableEntitiesNames(x);			
		modulesEntities.put(x,entities);					
		}
		
		
	}
	
	public Profile getProfile() {
		return profile;
	}
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public ArrayList<String> getModules() {
		return modules;
	}
	public void setModules(ArrayList<String> modules) {
		this.modules = modules;
	}
	public HashMap<String, ArrayList<String>> getModulesEntities() {
		return modulesEntities;
	}
	public void setModulesEntities(
			HashMap<String, ArrayList<String>> modulesEntities) {
		this.modulesEntities = modulesEntities;
	}
	
	
	
}
