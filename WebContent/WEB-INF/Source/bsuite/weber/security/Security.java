package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;

public class Security {

	private Profile profile;
	private Role role;
	private ArrayList<String> modules;
	private HashMap<String, ArrayList<String>> modulesEntities;
	
	public Security(){
		profile=new Profile();
		role=new Role();
		modules=profile.getVisibleModulesNames();
		
		for(String x:modules){
		ArrayList entities=new ArrayList();	
		entities	=profile.getCreatableEntitiesNames(x);			
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
