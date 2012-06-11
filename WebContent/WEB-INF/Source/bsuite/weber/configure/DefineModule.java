package bsuite.weber.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import lotus.domino.cso.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

import bsuite.weber.model.BsuiteWorkFlow;

public class DefineModule extends BsuiteWorkFlow{
	public void addModules(Vector<String> modules) {
		for(String moduleName:modules){
			String moduleJson = createJsonString(moduleName);
			createModuleDocument(moduleJson, moduleName);
		}
			
	}
	/**
	 * @param moduleJson: Json string created for this module
	 * This method creates a document in the current database
	 */
	private void createModuleDocument(String moduleJson, String moduleName) {
		
		try {
			Document doc = currentdb.createDocument();
			doc.replaceItemValue("obid","module");
			doc.replaceItemValue("moduleName", moduleName);
			doc.replaceItemValue("JsonString", moduleJson);
			doc.replaceItemValue("Form", "module");
			doc.save();
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		
	}
	/**
	 * @param moduleName
	 * @return JsonString 
	 * This method generates the json string for this module
	 */
	private String createJsonString(String moduleName) {
		
		Module module = new Module();
		module.setModuleName(moduleName);
		ObjectMapper mapper=new ObjectMapper();
		 
		  try{
			return mapper.writeValueAsString(module);
		  }catch (Exception e) {
			
		}
		
		return null;
	}
	public void addEntity(String moduleName,String entityName, Vector<String> fields, Vector<String> features){
		
	
		try {
			
			
			View modulesView =currentdb.getView("Modules");
			
			//System.out.println("Document name"+modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);
			
			String jsonInput=moduleDoc.getItemValueString("JsonString");
			
			ObjectMapper mapper = new ObjectMapper();
		
			Module module = mapper.readValue(jsonInput, Module.class);
			
		
			ArrayList<Entity> entities = null;
			if(module.getEntities()==null){
				 entities = new ArrayList();
			}else{
				entities = module.getEntities();
			}
			
			Entity entity = new Entity();
			Field field = new Field();
			Feature feature  = new Feature();
			
			ArrayList<Field> fieldsList = new ArrayList<Field>() ;
			ArrayList<Feature> featureList = new ArrayList<Feature>();
			
			for(String s:fields){
				//Add fields to the new entity
				field.setFieldName(s);
				fieldsList.add(field);
			}
			for(String s:features){
				//Add features to the new entity
				feature.setFeatureName(s);
				featureList.add(feature);
			}
			entity.setEntityName(entityName);
			entity.setFields(fieldsList);
			entity.setFeatures(featureList);
			entities.add(entity);
			module.setEntities(entities);
			
			moduleDoc.replaceItemValue("JsonString", mapper.writeValueAsString(module));
			moduleDoc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		
		
	}
	public Vector getModules(){
		try{			
		View modulesView =currentdb.getView("Modules");
		ViewEntryCollection vc = modulesView.getAllEntries();
		Document doc=null;
		String mname="";
		ViewEntry entry = vc.getFirstEntry();
		Vector modules=new Vector();
		while(entry!=null){
			doc=entry.getDocument();
			mname=doc.getItemValueString("ModuleName");
			modules.add(mname);
			entry=vc.getNextEntry(entry);
		}
		return modules;
		
		}catch (NotesException e) {
			// TODO: handle exception
		}
		
		return null;
	}
	
	
	public String getModuleJson(String moduleName){
		Document moduleDoc = getModuleDoc(moduleName);
		try {
			return moduleDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}		
		return null;
	}
	
	public Vector getEntityNames(String moduleName){
		String moduleJson = getModuleJson(moduleName);
		ObjectMapper mapper = new ObjectMapper();
		Module module = null;
		try {
			module = mapper.readValue(moduleJson, Module.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Entity> entityList = module.getEntities();

		if(entityList!=null){
			Vector entityNames = new Vector();
			for(Entity e:entityList){
				entityNames.add(e.getEntityName());
			}
			return entityNames;
		}


		return null;
	}
	
	
	private Document getModuleDoc(String moduleName){
		try {
			View moduleView = currentdb.getView("Modules");
			Document moduleDoc = moduleView.getDocumentByKey(moduleName);		
			return moduleDoc;
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}
}
