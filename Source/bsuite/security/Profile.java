package bsuite.security;

import java.io.IOException;
import java.util.ArrayList;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import lotus.domino.Document;
import lotus.domino.NotesException;

import bsuite.jsonparsing.*;
import bsuite.relationship.Association;

import bsuite.utility.Utility;


 /**
  * Profile object contains module information in profileJson object
  * 
  *@author JPrakash
  *@created Sep 20, 2013
 */
public class Profile{
	private ProfileJson profileJson;
	private String profileName;

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * Profile constructor which initializes jsonProfile object
	 */
	public Profile() {
		super();
		this.profileJson = getProfileJsonObject();

	}

	/**
	 *returns the list of visible modules in the current profile
	 *@return List of module objects
	 */
	public ArrayList<Module> getVisibleModules() {
		ArrayList<Module> vModules = new ArrayList<Module>();
		if (profileJson == null) {
			return null;
		}
		for (Module module : profileJson.getModules()) {
			if (module.getTabvis().equals("1")) {
				vModules.add(module);
			}
		}
		return vModules;
	}

	/**
	 *Returns the list of creatable entities
	 *@param moduleName
	 *@return list of creatable entities
	 */
	public ArrayList<Entity> getCreatableEntities(String moduleName) {
		ArrayList<Entity> cEntities = new ArrayList<Entity>();
		Module mod = getModule(moduleName);

		for (Entity entity : mod.getEntities()) {
			if (entity != null) {
				if (entity.getCreate().equals("1")) {
					cEntities.add(entity);
				}
			}

		}
		return cEntities;
	}

	/**
	 *Returns the list of readable fields
	 *@param moduleName modulName as string
	 *@param entityName entity name as string
	 *@return
	 */
	public ArrayList<Field> getReadonlyFields(String moduleName,
			String entityName) {
		ArrayList<Field> rFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getReadonly().equals("1")) {
				rFields.add(f);
			}
		}

		return rFields;
	}

	/**
	 *Returns the list of editable fields
	 *@param moduleName module name as string
	 *@param entityName entity name as string
	 *@return list of editable fields
	 */
	public ArrayList<Field> getEditableFields(String moduleName,
			String entityName) {
		ArrayList<Field> eFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getReadonly().equals("0")) {
				eFields.add(f);
			}
		}
		return eFields;

	}

	/**
	 *Returns the list of visible features
	 *@param moduleName module name as string
	 *@return list of visible features
	 */
	public ArrayList<Feature> getVisibleFeatures(String moduleName) {
		ArrayList<Feature> vFeatures = new ArrayList<Feature>();
		Module mod = getModule(moduleName);

		for (Feature f : mod.getFeatures()) {
			if (f.getVisible().equals("1")) {
				vFeatures.add(f);
			}
		}

		return vFeatures;
	}

	/**
	 *Returns true if a particular entity is readable
	 *@param moduleName module name as string
	 *@param entityName module name as string
	 *@return true if readable false otherwise
	 */
	public boolean isEntityRead(String moduleName, String entityName) {
		if (getEntity(moduleName, entityName).getRead().equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true if a particular entity is readable	 
	 *@param moduleName module name as string
	 *@param entityName module name as string
	 *@return true if editable false otherwise
	 */
	public boolean isEntityUpdate(String moduleName, String entityName) {
		if (getEntity(moduleName, entityName).getUpdate().equals("1")) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Returns true if a particular entity is deletable	 
	 *@param moduleName module name as string
	 *@param entityName module name as string
	 *@return true if deletable false otherwise
	 */
	public boolean isEntityDelete(String moduleName, String entityName) {
		if (getEntity(moduleName, entityName).getDelete().equals("1")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 *Entity access type is used in dynamic entities to specify prive, private ronly, public etc
	 *1 private, 2 public, 3 public ronly
	 *@param moduleName module name as string
	 *@param entityName module name as string
	 *@return 
	 */
	public String getEntityAccessType(String moduleName, String entityName) {
		String accessType = getEntity(moduleName, entityName).getAccessType();
		return accessType;

	}

	/**
	 *Returns the moduleobject 
	 *@param moduleName as string
	 *@return module object
	 */
	private Module getModule(String moduleName) {
		if (moduleName.contains("_")) {
			moduleName = moduleName.replace("_", " ");
		}
		if (profileJson == null) {
			return null;
		}
		for (Module module : profileJson.getModules()) {
			if (module.getModuleName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}

	/**
	 *Returns the list of visible modules for this profile
	 *@return list of visible module names
	 */
	public ArrayList<String> getVisibleModulesNames() {
		ArrayList<String> vModules = new ArrayList<String>();
		if (profileJson == null) {
			return null;
		}
		for (Module module : profileJson.getModules()) {
			if (module.getTabvis().equals("1")) {
				vModules.add(module.getModuleName());
			}
		}
		return vModules;
	}

	/**Returns the list of creatable entity names for this module
	 *@param moduleName as string
	 *@return list of module names
	 */
	public ArrayList<String> getCreatableEntitiesNames(String moduleName) {
		ArrayList<String> cEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		if (entities == null) {
			return null;
		}
		for (Entity entity : entities) {
			if (entity != null) {
				if (entity.getCreate().equals("1")) {
					cEntities.add(entity.getEntityName());
				}
			}

		}
		return cEntities;
	}

	/**returns the list of field names which is readable for this entity
	 *@param moduleName as string
	 *@param entityName as string
	 *@return list of readable field names
	 */
	public ArrayList<String> getReadonlyFieldsNames(String moduleName,
			String entityName) {
		ArrayList<String> rFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getReadonly().equals("1")) {
				rFields.add(f.getFieldName());
			}
		}

		return rFields;
	}
	
	/**returns the list of field names which is editable for this entity
	 *@param moduleName as string
	 *@param entityName as string
	 *@return list of editable field names
	 */
	public ArrayList<String> getEditableFieldsNames(String moduleName,
			String entityName) {
		ArrayList<String> eFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getReadonly().equals("0")) {
				eFields.add(f.getFieldName());
			}
		}
		return eFields;

	}


	/**
	 *Returns the list of visible feature names for this module
	 *@param moduleName as string
	 *@return list of visible features
	 */
	public ArrayList<String> getVisibleFeaturesNames(String moduleName) {
		if (moduleName.contains("_")) {
			moduleName = moduleName.replace("_", " ");
		}
		ArrayList<String> vFeatures = new ArrayList<String>();
		Module mod = getModule(moduleName);

		for (Feature f : mod.getFeatures()) {
			if (f.getVisible().equals("1")) {
				vFeatures.add(f.getFeatureName());
			}
		}

		return vFeatures;

	}

	/**
	 * Returns the Entity object for the given module and entity name
	 *@param moduleName as string
	 *@param entityName as string
	 *@return Entity object
	 */
	private Entity getEntity(String moduleName, String entityName) {
		Module module = getModule(moduleName);
		for (Entity entity : module.getEntities()) {
			if (entity.getEntityName().equals(entityName)) {
				return entity;
			}
		}
		return null;
	}
	
	/**
	 *@param entity returns the entity action object associated with the current entity
	 *@param actionName as string
	 *@return entityAction object
	 */
	private EntityAction getEntityAction(Entity entity, String actionName){
		if(entity==null ){
			return null;
		}
		ArrayList<EntityAction> eactions = entity.getActions();
		if(eactions==null){
			return null;
		}
		for(EntityAction ea:eactions){
			if(ea!=null){
				if(ea.getActionName().equals(actionName)){
					return ea;
				}
			}
		}
		return null;
	}
	
	/**
	 *Returns the GroupPermission object for the given group name, ie viewAction
	 *@param moduleName as string
	 *@param viewActionName as string
	 *@return
	 */
	private GroupPermission getViewAction(String moduleName, String viewActionName){
		Module module = getModule(moduleName);
		for(GroupPermission gp:module.getGroups()){
			if(gp.getName().equals(viewActionName)){
				return gp;
			}
		}
		return null;
	}

	/**
	 * returns the list of visible field names for a given module and entity
	 *@param moduleName as string
	 *@param entityName as string
	 *@return list of visible field names
	 */
	public ArrayList<String> getVisibleFieldsNames(String moduleName,
			String entityName) {
		ArrayList<String> vFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getVisible().equals("1")) {
				vFields.add(f.getFieldName());
			}
		}

		return vFields;
	}

	/**
	 * returns the list of visible field objects for a given module and entity
	 *@param moduleName as string
	 *@param entityName as string
	 *@return list of visible field objects
	 */
	public ArrayList<Field> getVisibleFields(String moduleName,
			String entityName) {
		ArrayList<Field> vFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			if (f.getVisible().equals("1")) {
				vFields.add(f);
			}
		}

		return vFields;
	}

	/**
	 * returns the profile json object which contains the module objects 
	 *@return profileJson object
	 */
	private ProfileJson getProfileJsonObject() {
		
		Association as = new Association();
		
		String currentuser=null;
		try {
			
			currentuser = Utility.getCurrentSession().getEffectiveUserName();
			
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		Document profDoc = as.getAssociatedProfile(currentuser);
		if (profDoc == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		try {
			this.profileName = profDoc.getItemValueString("prof_name");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		try {
			return mapper.readValue(jsonString, ProfileJson.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Returns the list of readable entity names
	 *@param moduleName as string
	 *@return list of entitynames
	 */
	public ArrayList<String> getReadableEntitiesNames(String moduleName) {
		ArrayList<String> rEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		if (entities == null) {
			return null;
		}
		for (Entity entity : entities) {
			if (entity != null) {
				if (entity.getRead().equals("1")) {
					rEntities.add(entity.getEntityName());
				}
			}

		}
		return rEntities;
	}

	/**
	 * Returns the list of deletable entity names
	 *@param moduleName as string
	 *@return list of entitynames
	 */
	public ArrayList<String> getDeletableEntitiesNames(String moduleName) {
		ArrayList<String> dEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		if (entities == null) {
			return null;
		}
		for (Entity entity : entities) {
			if (entity != null) {
				if (entity.getDelete().equals("1")) {
					dEntities.add(entity.getEntityName());
				}
			}

		}
		return dEntities;
	}

	/**Returns the list of all entity names in the module
	 *@param moduleName as string
	 *@return list of entitynames
	 */
	public ArrayList<String> getAllEntitiesNames(String moduleName) {
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		if (entities == null) {
			return null;
		}
		for (Entity entity : entities) {
			if (entity != null) {

				aEntities.add(entity.getEntityName());

			}

		}

		return aEntities;
	}

	/**Returns the profile name
	 *@return as string
	 */
	public String getProfileName() {

		return profileName;
	}

	/**
	 * to get all the public R/W entities in the given module
	 *@param moduleName as string
	 *@return list of names 
	 */
	public ArrayList<String> getPublicRWEntities(String moduleName) {
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		for (Entity entity : entities) {

			String access = entity.getAccessType();
			if (access.equals("2"))
				aEntities.add(entity.getEntityName());

		}
		return aEntities;

	}

	/**returns the public Readonly entitynames
	 *@param moduleName as string
	 *@return listof entitynames
	 */
	public ArrayList<String> getPublicReadEntities(String moduleName) {
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		ArrayList<Entity> entities = mod.getEntities();
		for (Entity entity : entities) {

			String access = entity.getAccessType();
			if (access.equals("3"))
				aEntities.add(entity.getEntityName());

		}
		return aEntities;

	}

	/**Returns all fieldnames for a given module and entityname
	 *@param moduleName as string
	 *@param entityName as string
	 *@return list of field names
	 */
	public ArrayList<String> getAllFieldsNames(String moduleName,
			String entityName) {
		ArrayList<String> vFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName, entityName);
		for (Field f : entity.getFields()) {
			vFields.add(f.getFieldName());

		}

		return vFields;
	}
	

	/**
	 * Returns true if the given entity is creatable
	 *@param moduleName as string
	 *@param entityName as string
	 *@return true if creatable false otherwise 
	 */
	public boolean isEntityCreate(String moduleName, String entityName){
		if (getEntity(moduleName, entityName).getCreate().equals("1")) {
			return true;
		} else {
			return false;
		}
		
	}

	/**Returns true if a given viewAction is visible
	 *@param moduleName as string
	 *@param groupName as string, ie viewAction name
	 *@return true if visibe false otherwise
	 */
	public boolean isViewActionVisible(String moduleName, String groupName) {
		GroupPermission gp = getViewAction(moduleName, groupName);
		if(gp==null)return false;
		if(gp.getVisible().equals("1")){
			return true;
		}else{
		return false;
		}
	}
	
	/**Returns true if a given entityAction is visible, ie objectAction
	 *@param moduleName as string
	 *@param entityName as string
	 *@param actionName as string
	 *@return true or false
	 */
	public boolean isEntityActionVisible(String moduleName, String entityName,String actionName){
		EntityAction ea = getEntityAction(getEntity(moduleName, entityName), actionName);
		if(ea==null){
			return true;
		}
		if(ea.getVisible().equals("1")){
			return true;
		}else{
			return false;
		}
	}



}
