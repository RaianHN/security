package bsuite.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lotus.domino.Document;
import lotus.domino.NotesException;


import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import bsuite.relationship.Association;
import bsuite.utility.*;

/**
 * [Class description. The first sentence should be a meaningful summary of the
 * class since it will be displayed as the class summary on the Javadoc package
 * page.]
 * 
 * [Other notes, including guaranteed invariants, usage instructions and/or
 * examples, reminders about desired improvements, etc.]
 * 
 * @author JPrakash
 */
public class Security {

	private Profile profile;
	private Role role;
	private ArrayList<String> modules;
	private HashMap<String, ArrayList<String>> modulesEntities;
	private String profileName;
	private String roleName;
	private ArrayList<String> visibleFeatures;
	private ArrayList<String> visibleFields;
	private ArrayList<String> editableFields;
	private String module;
	private String myDocs;// Holds the search string based on role hierarchy and
							// data sharing

	public String getMyDocs() {

		return role.getSearchString();
	}

	public void setMyDocs(String myDocs) {
		this.myDocs = myDocs;
	}

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

		// gettinf visible feature names;

		System.out.println("RoleName " + role.getRoleName());
		System.out.println("ProfileName " + profile.getProfileName());
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

	public boolean isFeatureVisible(String featureName) {
		visibleFeatures = profile.getVisibleFeaturesNames(module);
		if (visibleFeatures.indexOf(featureName) < 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isFieldVisible(String entityyName, String fieldName) {
		visibleFields = profile.getVisibleFieldsNames(module, entityyName);
		System.out.println("visible fields" + visibleFields);
		if (visibleFields.indexOf(fieldName) < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isViewActionVisible(String groupName){
		return profile.isViewActionVisible(module,groupName);
	}

	public boolean isFieldReadonly(String entityName, String fieldName) {
		editableFields = profile.getEditableFieldsNames(module, entityName);
		System.out.println("editable fields" + editableFields);
		if (editableFields.indexOf(fieldName) < 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isEntityCreatable(String entityName) {
		return profile.isEntityCreate(module, entityName);

	}

	public boolean isEntityReadable(String entityName) {
		return profile.isEntityRead(module, entityName);
	}

	public boolean isEntityEditable(String entityName) {
		
		return profile.isEntityUpdate(module, entityName) || isMyDoc();
	}

	public boolean isEntityDeletable(String entityName) {
		return profile.isEntityDelete(module, entityName) || isMyDoc();
	}

	public boolean isModuleVisible() {
		ArrayList<String> visibleModules = profile.getVisibleModulesNames();
		if (visibleModules.indexOf(module) < 0) {
			return false;
		} else {
			return true;
		}
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return module;
	}
	private boolean isMyDoc(){
		//Used in isEntityEditable and isEntityDeletable, to return true if the document belongs to current user
		//get currentDocument variable 
		DominoDocument domDoc = (DominoDocument)JSFUtil.getVariableValue("currentDocument");
		Document doc = null;
		if(domDoc!=null){
			 doc = domDoc.getDocument();
		}else{
			 System.out.println("currentDocument is null ");
			 return false;
		}
		
		String currentUName = "";//current users name
		Association as = new Association();
		try {
			 currentUName = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			 System.out.println("currentUName "+currentUName);
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		String uName = "";//Document creators name
		try {
			if(doc.hasItem("DocumentCreator")){
				uName = as.getFormattedName(doc.getItemValueString("DocumentCreator"), "canonical");
				 System.out.println("doc Creator "+uName);
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		System.out.println("Comparing  "+currentUName+" "+uName);
		if(currentUName.equals(uName)){//Compare 
			return true;
		}else{
			return false;
		}
		
	}

}
