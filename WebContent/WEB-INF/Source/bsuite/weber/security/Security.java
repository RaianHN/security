package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bsuite.weber.tools.JSFUtil;




/**
[Class description.  The first sentence should be a meaningful summary of the class since it
   will be displayed as the class summary on the Javadoc package page.]
   
   [Other notes, including guaranteed invariants, usage instructions and/or examples, reminders
   about desired improvements, etc.]
   
 	@author JPrakash
  
 */
public class Security {

	
	private Profile profile;
	private Role role;
	private ArrayList<String> modules;
	private HashMap<String, ArrayList<String>> modulesEntities;
	private String profileName;
	private String roleName;

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	
	public String getRoleName() {
		return roleName;
	}

	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	
	
	@SuppressWarnings("unchecked")
	public Security() {
		
		profile = new Profile();
		role = new Role();

		Map sessionScope = (Map) JSFUtil.getVariableValue("sessionScope");
		if (profile != null) {

			profileName = profile.getProfileName();
			sessionScope.put("profileName", profileName);
			if (role != null) {
				roleName = role.getRoleName();
				sessionScope.put("roleName", roleName);
			}
			modules = profile.getVisibleModulesNames();
			modulesEntities = new HashMap<String, ArrayList<String>>();
			if (modules != null) {
				for (String x : modules) {
					ArrayList<String> entities = new ArrayList<String>();
					entities = profile.getCreatableEntitiesNames(x);
					modulesEntities.put(x, entities);
				}
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
