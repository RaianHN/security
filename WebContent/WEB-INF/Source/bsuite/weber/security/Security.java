package bsuite.weber.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.bsuite.utility.*;




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
	private  ArrayList<String> visibleFeatures; 
	private  ArrayList<String> visibleFields; 
	private  ArrayList<String> editableFields; 
	
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
		System.out.println("in security1");
		profile = new Profile();
		System.out.println("in security2");
		role = new Role();
		System.out.println("in security3");

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
		
		//gettinf visible feature names;
		
		
		System.out.println("RoleName "+role.getRoleName());
		System.out.println("ProfileName "+profile.getProfileName());
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
	public boolean isFeatureVisible(String moduleName,String featureName){
		visibleFeatures = profile.getVisibleFeaturesNames(moduleName);
		if(visibleFeatures.indexOf(featureName)<0){
			return false;
		}
		else{
			return true;
		}
	}
	public boolean isFieldVisible(String moduleName,String entityyName, String fieldName){
		visibleFields = profile.getVisibleFieldsNames(moduleName, entityyName);
		System.out.println("visible fields"+visibleFields);
		if(visibleFields.indexOf(fieldName)<0){
			return false;
		}else{
			return true;
		}
	}
	public boolean isFieldReadonly(String moduleName,String entityName, String fieldName){
		editableFields = profile.getEditableFieldsNames(moduleName, entityName);
		System.out.println("editable fields"+editableFields);
		if(editableFields.indexOf(fieldName)<0){
			return true;
		}else{
			return false;
		}
	}
	public boolean isEntityCreatable(String moduleName,String entityName){
		return profile.isEntityCreate(moduleName,entityName);
		
	}
	public boolean isEntityReadable(String moduleName,String entityName){
		return profile.isEntityRead(moduleName, entityName);
	}
	public boolean isEntityEditable(String moduleName,String entityName){
		return profile.isEntityUpdate(moduleName, entityName);
	}
	public boolean isEntityDeletable(String moduleName,String entityName){
		return profile.isEntityDelete(moduleName, entityName);
	}
	public boolean isModuleVisible(String moduleName){
		  ArrayList<String> visibleModules = profile.getVisibleModulesNames();
		  if(visibleModules.indexOf(moduleName)<0){
			  return false;
		  }else{
			  return true;
		  }
	}

}
