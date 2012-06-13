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

public class Profile extends BsuiteWorkFlow {
	private ProfileJson profileJson;

	public Profile() {
		super();
		this.profileJson = getProfileJsonObject();
	}

	public ArrayList<Module> getVisibleModules(){
		ArrayList<Module> vModules = new ArrayList<Module>();
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module);
			}
		}
		return vModules;
	}

	public ArrayList<Entity> getCreatableEntities(String moduleName){
		ArrayList<Entity> cEntities = new ArrayList<Entity>();
		Module mod = getModule(moduleName);
		
		for(Entity entity:mod.getEntities()){
			if(entity.getCreate().equals("1")){
				cEntities.add(entity);
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

	public ArrayList<Feature> getVisibleFeatures(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList<Feature> vFeatures = new ArrayList<Feature>();
		for(Feature f:entity.getFeatures()){
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

	private Module getModule(String moduleName){
		
		for(Module module:profileJson.getModules()){
			if(module.getModuleName().equals(moduleName)){
				return module;
			}
		}
		return null;
	}
	public ArrayList<String> getVisibleModulesNames(){
		ArrayList<String> vModules = new ArrayList<String>();
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module.getModuleName());
			}
		}
		return vModules;
	}

	public ArrayList<String> getCreatableEntitiesNames(String moduleName){
		ArrayList<String> cEntities = new ArrayList<String>();
		Module mod = getModule(moduleName);
		
		for(Entity entity:mod.getEntities()){
			if(entity.getCreate().equals("1")){
				cEntities.add(entity.getEntityName());
			}
		}
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
		ArrayList<String> eFields = new ArrayList<String>();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("0")){
				eFields.add(f.getFieldName());
			}
		}
		return eFields;
		
	}

	public ArrayList<String> getVisibleFeaturesNames(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList<String> vFeatures = new ArrayList<String>();
		for(Feature f:entity.getFeatures()){
			if(f.getVisible().equals("1")){
				vFeatures.add(f.getFeatureName());
			}
		}
		
		return vFeatures;
	}
	
	
	
	private Entity getEntity(String moduleName, String entityName){
		Module module = getModule(moduleName);
		for(Entity entity:module.getEntities()){
			if(entity.getEntityName().equals(entityName)){
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
		Document profDoc = as.getAssociatedProfile(currentuser.getBsuiteuser());
		ObjectMapper mapper = new ObjectMapper();
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
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
	
}
