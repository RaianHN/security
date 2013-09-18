package bsuite.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lotus.domino.Document;
import lotus.domino.NotesException;


import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;

import bsuite.relationship.Association;
import bsuite.utility.*;

/**Security bean which encapsulates profile and role objects,
 * provides the api to get accessible modules, features, entities, fields.
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

	/**
	 *Returns the search string for the current module and entity based on the datasharing rule,
	 *can be used as the search formula in any view
	 *@return view selection formula as string
	 */
	public String getMyDocs() {

		return role.getSearchString();
	}

	
	
	/**
	 *Returns the profile name of the current user
	 *@return profile name as string
	 */
	public String getProfileName() {
		return profileName;
	}

	/**
	 *Setter for the profileName property
	 *@param profileName string 
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 *Returns the role name of the current user
	 *@return role as string
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 *Setter for roleName property
	 *@param roleName as string
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	
	/**
	 * Security Bean constructor
	 * Initializes profile and role object for the current user
	 */
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
	
	public boolean isMenuVisible(String groupName){
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

	public boolean isObjectCreatable(String entityName) {
		return profile.isEntityCreate(module, entityName);

	}

	public boolean isObjectReadable(String entityName) {
		return profile.isEntityRead(module, entityName);
	}

	public boolean isObjectEditable(String entityName) {
		
		return profile.isEntityUpdate(module, entityName) || isMyDoc();
	}

	public boolean isObjectDeletable(String entityName) {
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
	public boolean isObjectActionVisible(String objectName, String actionName){
		return profile.isEntityActionVisible(module, objectName, actionName);
	}

}
