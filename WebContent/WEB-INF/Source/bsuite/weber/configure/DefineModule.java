package bsuite.weber.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import lotus.domino.cso.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

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
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);
			String jsonInput=moduleDoc.getItemValueString("JsonString");
			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);
			ArrayList<Entity> entities = module.getEntities();
			
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
			
			entity.setFields(fieldsList);
			entity.setFeatures(featureList);
			entities.add(entity);
			
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
	
}
