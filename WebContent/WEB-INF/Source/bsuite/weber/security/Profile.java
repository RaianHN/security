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
	
	
	public String getEntityAccessType(String moduleName, String entityName) {
		String accessType=getEntity(moduleName,entityName).getAccessType();
		return accessType;
		
	}

	private Module getModule(String moduleName){
		System.out.println("--------44");
		if(profileJson==null){
			return null;
		}
		for(Module module:profileJson.getModules()){
			System.out.println("--------45");
			if(module.getModuleName().equals(moduleName)){
				System.out.println("--------46");
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
	
}
