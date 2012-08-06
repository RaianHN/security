package bsuite.weber.security;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import lotus.domino.Document;
import lotus.domino.NotesException;


import bsuite.weber.jsonparsing.*;

import bsuite.weber.model.BsuiteWorkFlow;

import bsuite.weber.relationship.Association;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.BsuiteMain;

public class Profile extends BsuiteMain  {
	private ProfileJson profileJson;
	private String profileName;

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public Profile() {
		super();
		this.profileJson = getProfileJsonObject();
		
	}

	public ArrayList<Module> getVisibleModules(){
		ArrayList<Module> vModules = new ArrayList<Module>();
		if(profileJson==null){
			return null;
		}
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module);
			}
		}
		System.out.println(vModules);
		return vModules;
	}

	public ArrayList<Entity> getCreatableEntities(String moduleName){
		ArrayList<Entity> cEntities = new ArrayList<Entity>();
		Module mod = getModule(moduleName);
		
		for(Entity entity:mod.getEntities()){
			if(entity!=null){
				if(entity.getCreate().equals("1")){
					cEntities.add(entity);
				}	
			}
			
		}
		return cEntities;
	}


	public ArrayList<Field> getReadonlyFields(String moduleName, String entityName){
		ArrayList<Field> rFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("1")){
				rFields.add(f);
			}
		}
		
		return rFields;
	}
	
	
	public ArrayList<Field> getEditableFields(String moduleName, String entityName){
		ArrayList<Field> eFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("0")){
				eFields.add(f);
			}
		}
		return eFields;
		
	}

	/*public ArrayList<Feature> getVisibleFeatures(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList<Feature> vFeatures = new ArrayList<Feature>();
		for(Feature f:entity.getFeatures()){
			if(f.getVisible().equals("1")){
				vFeatures.add(f);
			}
		}
		
		return vFeatures;
	}*/

	
	public ArrayList<Feature> getVisibleFeatures(String moduleName){
		//Entity entity = getEntity(moduleName);
		ArrayList<Feature> vFeatures = new ArrayList<Feature>();
		Module mod = getModule(moduleName);
		
		for(Feature f:mod.getFeatures()){		
			if(f.getVisible().equals("1")){
				vFeatures.add(f);
			}
		}
		
		return vFeatures;
	}

	public boolean isEntityRead(String moduleName, String entityName){
		if(getEntity(moduleName,entityName).getRead().equals("1")){
			return true;
		}
		else{
			return false;
		}
	}
	public boolean isEntityUpdate(String moduleName, String entityName) {
		if(getEntity(moduleName,entityName).getUpdate().equals("1")){
			return true;
		}
		else{
			return false;
		}
		
	}
	public boolean isEntityDelete(String moduleName, String entityName) {
		if(getEntity(moduleName,entityName).getDelete().equals("1")){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public String getEntityAccessType(String moduleName, String entityName) {
		String accessType=getEntity(moduleName,entityName).getAccessType();
		System.out.println("--------222222");
		System.out.println("--------44Access Type "+accessType);
		return accessType;
		
	}

	private Module getModule(String moduleName){
		//System.out.println("--------44");
		//System.out.println("ModuleName From createFeatures "+moduleName);
		if(moduleName.contains("_")){
			moduleName = moduleName.replace("_"," ");
		}
		if(profileJson==null){
			return null;
		}
		for(Module module:profileJson.getModules()){
			//System.out.println("--------45");
			//System.out.println("ModuleName comparison with Module class"+module.getModuleName());
			if(module.getModuleName().equals(moduleName)){
				//System.out.println("--------46");
				return module;
			}
		}
		return null;
	}
	public ArrayList<String> getVisibleModulesNames(){
		ArrayList<String> vModules = new ArrayList<String>();
		if(profileJson==null){
			return null;
		}
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module.getModuleName());
			}
		}
		System.out.println(vModules);
		return vModules;
	}

	public ArrayList<String> getCreatableEntitiesNames(String moduleName){
		System.out.println("modulename"+moduleName);
		ArrayList<String> cEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		System.out.println("--------41");
		ArrayList<Entity> entities = mod.getEntities();
		if(entities==null){
			return null;
		}
		for(Entity entity:entities){
			System.out.println("--------42");
			if(entity!=null){
				if(entity.getCreate().equals("1")){
					System.out.println("--------43");
					cEntities.add(entity.getEntityName());
				}
			}
			
		}
		System.out.println("c enti"+cEntities);
		return cEntities;
	}


	public ArrayList<String> getReadonlyFieldsNames(String moduleName, String entityName){
		ArrayList<String> rFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("1")){
				rFields.add(f.getFieldName());
			}
		}
		
		return rFields;
	}
	
	
	public ArrayList<String> getEditableFieldsNames(String moduleName, String entityName){
		System.out.println("inside getEditable Fields");
		ArrayList<String> eFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("0")){
				eFields.add(f.getFieldName());
			}
		}
		return eFields;
		
	}

	/*public ArrayList<String> getVisibleFeaturesNames(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList<String> vFeatures = new ArrayList<String>();
		for(Feature f:entity.getFeatures()){
			if(f.getVisible().equals("1")){
				vFeatures.add(f.getFeatureName());
			}
		}
		
		return vFeatures;
	}*/
	
	
	public ArrayList<String> getVisibleFeaturesNames(String moduleName){		
		
		System.out.println("inside visible Feastures "+moduleName);
		if(moduleName.contains("_")){
			moduleName = moduleName.replace("_", " ");
		}
		System.out.println("inside visible Feastures after replace "+moduleName);
		ArrayList<String> vFeatures = new ArrayList<String>();
		Module mod = getModule(moduleName);
		System.out.println("mod "+mod.getModuleName());
		
		for(Feature f:mod.getFeatures()){		
			if(f.getVisible().equals("1")){
				System.out.println("Visible Feature "+f.getFeatureName());
				vFeatures.add(f.getFeatureName());
			}
		}
		
		return vFeatures;

	
}
	
	
	
	
	private Entity getEntity(String moduleName, String entityName){
		Module module = getModule(moduleName);
		for(Entity entity:module.getEntities()){
			if(entity.getEntityName().equals(entityName)){
				System.out.println("EntityName "+entity.getEntityName());
				System.out.println("moduleName "+moduleName);
				return entity;
			}
		}
		return null;
	}
	
	
	public ArrayList<String> getVisibleFieldsNames(String moduleName, String entityName){
		ArrayList<String> vFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getVisible().equals("1")){
				vFields.add(f.getFieldName());
			}
		}
		
		return vFields;
	}
	
	
	
	
	public ArrayList<Field> getVisibleFields(String moduleName, String entityName){
		ArrayList<Field> vFields = new ArrayList<Field>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getVisible().equals("1")){
				vFields.add(f);
			}
		}
		
		return vFields;
	}
	
	private ProfileJson getProfileJsonObject(){
		Association as = new Association();
		Document profDoc = as.getAssociatedProfile(currentuser);
		if(profDoc==null){
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		try {
			this.profileName = profDoc.getItemValueString("prof_name");
			System.out.println("pjsonname"+profileName);
		} catch (NotesException e) {
			// TODO Auto-generated catch block
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
	
	public ArrayList<String> getReadableEntitiesNames(String moduleName){
		System.out.println("modulename"+moduleName);
		ArrayList<String> rEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		System.out.println("--------411");
		ArrayList<Entity> entities = mod.getEntities();
		if(entities==null){
			return null;
		}
		for(Entity entity:entities){
			System.out.println("--------421"+entity.getEntityName());
			if(entity!=null){
				if(entity.getRead().equals("1")){
					System.out.println("--------431");
					rEntities.add(entity.getEntityName());
				}
			}
			
		}
		System.out.println("r enti"+rEntities);
		return rEntities;
	}
	
	public ArrayList<String> getDeletableEntitiesNames(String moduleName){
		System.out.println("modulename"+moduleName);
		ArrayList<String> dEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		System.out.println("--------411");
		ArrayList<Entity> entities = mod.getEntities();
		if(entities==null){
			return null;
		}
		for(Entity entity:entities){
			System.out.println("--------421"+entity.getEntityName());
			if(entity!=null){
				if(entity.getDelete().equals("1")){
					System.out.println("--------431");
					dEntities.add(entity.getEntityName());
				}
			}
			
		}
		System.out.println("r enti"+dEntities);
		return dEntities;
	}
	
	public ArrayList<String> getAllEntitiesNames(String moduleName){
		System.out.println("modulename"+moduleName);
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		System.out.println("--------411");
		ArrayList<Entity> entities = mod.getEntities();
		if(entities==null){
			return null;
		}
		for(Entity entity:entities){
			System.out.println("--------421"+entity.getEntityName());
			if(entity!=null){
				
					aEntities.add(entity.getEntityName());
			
			}
			
		}
		
	
		System.out.println("r enti"+aEntities);
		return aEntities;
	}
	public String getProfileName(){
		
		return profileName;
	}
	
	
	
	//to get all the public R/W entities in the given module
	public ArrayList<String> getPublicRWEntities(String moduleName){
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod=getModule(moduleName);
		ArrayList<Entity> entities=mod.getEntities();
		for(Entity entity:entities){	
		
				String access=entity.getAccessType();
				System.out.println("Access Type in RWEntities "+access);
				if(access.equals("2"))
					aEntities.add(entity.getEntityName());	
			
			
		}
		return aEntities;
		
	}
	
	//to get all the public Read entities in the given module
	public ArrayList<String> getPublicReadEntities(String moduleName){
		ArrayList<String> aEntities = new ArrayList<String>();
		Module mod=getModule(moduleName);
		ArrayList<Entity> entities=mod.getEntities();
		for(Entity entity:entities){	
		
				String access=entity.getAccessType();
				if(access.equals("3"))
					aEntities.add(entity.getEntityName());	
			
			
		}
		return aEntities;
		
	}
	
	
	public ArrayList<String> getAllFieldsNames(String moduleName, String entityName){
		ArrayList<String> vFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){		
				vFields.add(f.getFieldName());
			
		}
		
		return vFields;
	}
	
	
}
