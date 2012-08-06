package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bsuite.weber.tools.JSFUtil;

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

	public Security() {
		profile = new Profile();
		System.out.println("-------1");
		role = new Role();
		System.out.println("-------2");
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		Map sessionScope = (Map) JSFUtil.getVariableValue("sessionScope");
		if(profile!=null){
			
		profileName = profile.getProfileName();
		sessionScope.put("profileName",profileName);
		System.out.println("profilename"+profileName);
			//profileName = "";
		if(role!=null){
			roleName = role.getRoleName();
			sessionScope.put("roleName",roleName);
		System.out.println("rolename"+roleName);
			//roleName = "";
		}
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
