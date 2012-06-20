package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;

public class Security {

	private Profile profile;
	private Role role;
	private ArrayList<String> modules;
	private HashMap<String, ArrayList<String>> modulesEntities;

	public Security() {
		profile = new Profile();
		System.out.println("-------1");
		role = new Role();
		System.out.println("-------2");
		
		if(profile!=null){
			
		
		modules = profile.getVisibleModulesNames();
		modulesEntities = new HashMap<String, ArrayList<String>>();
		System.out.println("-------3");
		if(modules!=null){
			for (String x : modules) {
				System.out.println("-------4");
				ArrayList<String> entities = new ArrayList<String>();
				System.out.println("-------4x1");
				entities = profile.getCreatableEntitiesNames(x);
				System.out.println("-------4x2");
				//if (entities != null) {
					modulesEntities.put(x, entities);
				//}

				System.out.println("-------5");
			}
			System.out.println("-------6");
			}	
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
