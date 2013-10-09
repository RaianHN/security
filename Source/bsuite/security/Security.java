package bsuite.security;

import java.util.ArrayList;
import java.util.Arrays;
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
	private HashMap<String, ArrayList<String>> readableEntities;
	private String profileName;
	private String roleName;
	private ArrayList<String> visibleFeatures;
	private ArrayList<String> visibleFields;
	private ArrayList<String> editableFields;
	private String module;
	

	/**
	 *Returns the search string for the current module and entity based on the datasharing rule,
	 *can be used as the search formula in any view
	 *@return view selection formula as string
	 */
	public String getSearchString(String entityName) {

		return role.getSearchString(module,entityName);
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

		profile = new Profile();//Instantiate profile object for the current user
		role = new Role();//Instantiate role object for the current user

		Map sessionScope = (Map) JSFUtil.getVariableValue("sessionScope");
		if (profile != null) {
			profileName = profile.getProfileName();
			sessionScope.put("profileName", profileName);//Put the profile name in sessionScoped variable
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

	/**getter for profile
	 *@return profile object
	 */
	public Profile getProfile() {
		return profile;
	}

	/**setter for profile
	 *@param profile
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**getter for role object
	 *@return Role object
	 */
	public Role getRole() {
		return role;
	}

	/**setter for role object
	 *@param role
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**getter for all visible modules
	 *@return
	 */
	public ArrayList<String> getModules() {
		return modules;
	}

	/**setter for visible modules
	 *@param modules
	 */
	public void setModules(ArrayList<String> modules) {
		this.modules = modules;
	}

	/**getter for creatable entities
	 *@return
	 */
	public HashMap<String, ArrayList<String>> getModulesEntities() {
		return modulesEntities;
	}

	/**setter for creatable entities
	 *@param modulesEntities
	 */
	public void setModulesEntities(
			HashMap<String, ArrayList<String>> modulesEntities) {
		this.modulesEntities = modulesEntities;
	}

	/**returns the feature visibility for the given modulename
	 *@param featureName name of the feature
	 *@return true or false
	 */
	public boolean isFeatureVisible(String featureName) {
		visibleFeatures = profile.getVisibleFeaturesNames(module);
		if (visibleFeatures.indexOf(featureName) < 0) {
			return false;
		} else {
			return true;
		}
	}

	/**Returns the field visibility for a given module's entity's field
	 *@param entityyName name of the entity
	 *@param fieldName field name
	 *@return true or false
	 */
	public boolean isFieldVisible(String entityyName, String fieldName) {
		visibleFields = profile.getVisibleFieldsNames(module, entityyName);
		
		if (visibleFields.indexOf(fieldName) < 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/**Returns if menu is visible
	 *@param groupName name of the menu
	 *@return true or false
	 */
	public boolean isMenuVisible(String groupName){
		return profile.isViewActionVisible(module,groupName);
	}

	/**Returns if the field is readonly
	 *@param entityName name of the entity
	 *@param fieldName name of the field
	 *@return true or false
	 */
	public boolean isFieldReadonly(String entityName, String fieldName) {
		editableFields = profile.getEditableFieldsNames(module, entityName);
		
		if (editableFields.indexOf(fieldName) < 0) {
			return true;
		} else {
			return false;
		}
	}

	/**Returns if the object is creatable
	 *@param entityName name of the entity(Object)
	 *@return true or false
	 */
	public boolean isObjectCreatable(String entityName) {
		return profile.isEntityCreate(module, entityName);

	}

	/**Returns if the object is creatable
	 *@param entityName name of the entity(Object)
	 *@return true or false
	 */
	public boolean isObjectReadable(String entityName) {
		return profile.isEntityRead(module, entityName);
	}
	
	/**Returns if the object is editable
	 *@param entityName name of the entity(Object)
	 *@return true or false
	 */
	public boolean isObjectEditable(String entityName) {
		
		return profile.isEntityUpdate(module, entityName) || isMyDoc();
	}
	
	/**Returns if the object is deletable
	 *@param entityName name of the entity(Object)
	 *@return true or false
	 */
	public boolean isObjectDeletable(String entityName) {
		return profile.isEntityDelete(module, entityName) || isMyDoc();
	}

	
	/**Returns if the module is visible
	 *@return true or false
	 */
	public boolean isModuleVisible() {
		ArrayList<String> visibleModules = profile.getVisibleModulesNames();
		if (visibleModules.indexOf(module) < 0) {
			return false;
		} else {
			return true;
		}
	}

	/**setter for the module name
	 *@param module
	 */
	public void setModule(String module) {
		this.module = module;
	}

	/**Returns the module name
	 *@return module name as string
	 */
	public String getModule() {
		return module;
	}
	
	
	/**Used in isEntityEditable and isEntityDeletable, to return true if the document belongs to current user
	 *@return true or false
	 */
	private boolean isMyDoc(){
		//get currentDocument variable 
		DominoDocument domDoc = (DominoDocument)JSFUtil.getVariableValue("currentDocument");
		Document doc = null;
		if(domDoc!=null){
			 doc = domDoc.getDocument();
		}else{
			 
			 return false;
		}
		
		String currentUName = "";//current users name
		Association as = new Association();
		try {
			 currentUName = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			 
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		String uName = "";//Document creators name
		try {
			if(doc.hasItem("DocumentCreator")){
				uName = as.getFormattedName(doc.getItemValueString("DocumentCreator"), "canonical");
				 
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		if(currentUName.equals(uName)){//Compare 
			return true;
		}else{
			return false;
		}
		
	}
	/**Returns if the objectAction is visible (Action inside the form)
	 *@param objectName name of the entity
	 *@param actionName name of the action in the entity
	 *@return true or false
	 */
	public boolean isObjectActionVisible(String objectName, String actionName){
		return profile.isEntityActionVisible(module, objectName, actionName);
	}

	/**Returns the list of module names which is visible for the current user, used in global search
	 *@return list of module names
	 */
	public String[] getVisibleModules(){
		//Used in global search
		if(modules==null){
			return null;
		}
		Object[] ObjectList = modules.toArray();
		String[] stringArray = Arrays.copyOf(ObjectList,ObjectList.length,String[].class);
		return stringArray;
		
		
		
	}
	/**Returns the list of entity names which is visible for the current user, used in global search
	 *@return list of entity names
	 */
	public String[] getReadableEntities(String module){
		//Used in global search
		ArrayList<String> entities = readableEntities.get(module);
		if(entities!=null){
			Object[] ObjectList = entities.toArray();
			String[] stringArray = Arrays.copyOf(ObjectList,ObjectList.length,String[].class);
			return stringArray;
		}
		return null;
	}
}
