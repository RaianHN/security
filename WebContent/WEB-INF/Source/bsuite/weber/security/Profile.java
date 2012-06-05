package bsuite.weber.security;

import java.util.ArrayList;

import bsuite.weber.jsonparsing.*;

import bsuite.weber.model.BsuiteWorkFlow;

public class Profile extends BsuiteWorkFlow {
	private ProfileJson profileJson;

	public Profile() {
		super();
		this.profileJson = null;
	}

	public ArrayList getVisibleModules(){
		ArrayList<Module> vModules = new ArrayList<Module>();
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module);
			}
		}
		return vModules;
	}

	public ArrayList getCreatableEntities(String moduleName){
		ArrayList<Entity> cEntities = new ArrayList<Entity>();
		Module mod = getModule(moduleName);
		
		for(Entity entity:mod.getEntities()){
			if(entity.getCreate().equals("1")){
				cEntities.add(entity);
			}
		}
		return cEntities;
	}


	public ArrayList getReadonlyFields(String moduleName, String entityName){
		ArrayList rFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("1")){
				rFields.add(f);
			}
		}
		
		return rFields;
	}
	
	
	public ArrayList getEditableFields(String moduleName, String entityName){
		ArrayList eFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("0")){
				eFields.add(f);
			}
		}
		return eFields;
		
	}

	public ArrayList getVisibleFeatures(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList vFeatures = new ArrayList();
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
	public ArrayList getVisibleModulesNames(){
		ArrayList<Module> vModules = new ArrayList<Module>();
		for(Module module:profileJson.getModules()){
			if(module.getTabvis().equals("1")){
				vModules.add(module);
			}
		}
		return vModules;
	}

	public ArrayList getCreatableEntitiesNames(String moduleName){
		ArrayList cEntities = new ArrayList();
		Module mod = getModule(moduleName);
		
		for(Entity entity:mod.getEntities()){
			if(entity.getCreate().equals("1")){
				cEntities.add(entity.getEntityName());
			}
		}
		return cEntities;
	}


	public ArrayList getReadonlyFieldsNames(String moduleName, String entityName){
		ArrayList rFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("1")){
				rFields.add(f.getFieldName());
			}
		}
		
		return rFields;
	}
	
	
	public ArrayList getEditableFieldsNames(String moduleName, String entityName){
		ArrayList eFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getReadonly().equals("0")){
				eFields.add(f.getFieldName());
			}
		}
		return eFields;
		
	}

	public ArrayList getVisibleFeaturesNames(String moduleName, String entityName){
		Entity entity = getEntity(moduleName,entityName);
		ArrayList vFeatures = new ArrayList();
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
	
	
	public ArrayList getVisibleFieldsNames(String moduleName, String entityName){
		ArrayList vFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getVisible().equals("1")){
				vFields.add(f.getFieldName());
			}
		}
		
		return vFields;
	}
	public ArrayList getVisibleFields(String moduleName, String entityName){
		ArrayList vFields = new ArrayList();
		Entity entity = getEntity(moduleName,entityName);
		for(Field f:entity.getFields()){
			if(f.getVisible().equals("1")){
				vFields.add(f);
			}
		}
		
		return vFields;
	}
	
	
}
