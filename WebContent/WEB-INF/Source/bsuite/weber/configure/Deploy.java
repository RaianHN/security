package bsuite.weber.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import bsuite.weber.jsonparsing.ProfileJson;
import bsuite.weber.model.BsuiteWorkFlow;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

public class Deploy extends BsuiteWorkFlow{

	public void createRoleDocs(){
		try {
			createRoleDocuments(session.getDatabase("", bsuitepath+"Security.nsf"));
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createProfileDocs(){
		try {
			createProfileDocument(session.getDatabase("", bsuitepath+"Security.nsf"), "Admin");
			createProfileDocument(session.getDatabase("", bsuitepath+"Security.nsf"), "Standard");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void testView(){
		try {
			currentdb.createView("Test View");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void createRoleDocuments(Database db) {
		DefineModule define = new DefineModule();
		ArrayList<Role> roles = define.getRoleList();

		for (Role role : roles) {
			try {
				Document roleDoc = db.createDocument();
				roleDoc.replaceItemValue("Form", "Role");
				roleDoc.replaceItemValue("role_name", role.getRoleName());
				roleDoc.replaceItemValue("role_to", role.getRoleParent());
				//Added by Tenzin on 20July'12 for ShareDataWithPeers
				roleDoc.replaceItemValue("sharewithpeers", "0");
				roleDoc.save();
			} catch (NotesException e) {
				e.printStackTrace();
			}

		}
	}

	public void createProfileDocument(Database db, String profileName) {
		// Creates the profile document in security.nsf

		// get all the modules defined in xmployee.nsf
		// and the schema defined for each entity
		// for each field and feature in entity set the permissions and add it
		// to entity list of profile and finally add the modules in profile
		// object
		// save it as a document in security database, by replacing JsonString
		// value

		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		ProfileJson pf = new ProfileJson();
		ArrayList<bsuite.weber.jsonparsing.Module> modules = new ArrayList<bsuite.weber.jsonparsing.Module>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper mapper2 = new ObjectMapper();

		String JsonString = "";
		

		for (String moduleName : moduleNames) {		//For each module set permission
			System.out.println("modulenames"+moduleName);
			System.out.println("modulenames1");
			bsuite.weber.jsonparsing.Module moduleNew = defineModulePermission(moduleName, "1");
			System.out.println("modulenames211");

			String JsonModule = def.getModuleJson(moduleName);
			System.out.println("modulenames212 jsonModule "+JsonModule);

			Module module=null;
			try {
				module = mapper.readValue(JsonModule, Module.class);
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//Added for the module features
			System.out.println("modulenames21234");

			ArrayList<Feature>features = module.getFeatures();
			System.out.println("modulenames212www ");

			if(features!=null){				
			ArrayList<bsuite.weber.jsonparsing.Feature> featureList = new ArrayList<bsuite.weber.jsonparsing.Feature>();//Create empty feature list for this module
			for(Feature f:features){
				bsuite.weber.jsonparsing.Feature feature = defineFeaturePermission(f.getFeatureName(), "1");
				featureList.add(feature);
			}			
			moduleNew.setFeatures(featureList);
			}
			
			System.out.println("modulenames212jjj");

			
			
			ArrayList<Entity> entities = module.getEntities();
			if(entities!=null){
				System.out.println("modulenames2");
				ArrayList<bsuite.weber.jsonparsing.Entity> entityList = new ArrayList<bsuite.weber.jsonparsing.Entity>();//Create empty EntityList for this module
				for(Entity e:entities){//For each entity set permission
					bsuite.weber.jsonparsing.Entity entity = defineEntityPermission(moduleName, e.getEntityName(), "1", "1", "1", "1","1");
					ArrayList<Field> fields = e.getFields();
					
					ArrayList<bsuite.weber.jsonparsing.Field> fieldList = new ArrayList<bsuite.weber.jsonparsing.Field>();//create empty FieldList for this entity
					for(Field f:fields){
						bsuite.weber.jsonparsing.Field field = defineFieldPermission(f.getFieldName(), "0", "1");
						fieldList.add(field);
					}	
					System.out.println("modulenames2");
					
					entity.setEntityName(e.getEntityName());
					entity.setFields(fieldList);					
					entityList.add(entity);
					System.out.println("modulenames3");
				}
				moduleNew.setEntities(entityList);
				
			}
			modules.add(moduleNew);
			System.out.println("modulenames4");
			
		}
		
		pf.setModules(modules);
		System.out.println("modulenames5");
		try {
			JsonString = mapper2.writeValueAsString(pf);
			System.out.println("Profile Json"+JsonString);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Document doc = db.createDocument();
			doc.replaceItemValue("Form", "permissions");
			doc.replaceItemValue("prof_name", profileName);
			doc.replaceItemValue("JsonString",JsonString );
			doc.save();
			
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		
	}

	private bsuite.weber.jsonparsing.Module defineModulePermission(String moduleName, String visibility) {
		System.out.println("definemodulepermission");
		bsuite.weber.jsonparsing.Module module = new bsuite.weber.jsonparsing.Module();
		module.setModuleName(moduleName);
		module.setTabvis(visibility);		
		return module;
		
	}
	private bsuite.weber.jsonparsing.Entity defineEntityPermission(String moduleName, String entityName, String c, String r, String u, String d,String access ){
		bsuite.weber.jsonparsing.Entity entity = new bsuite.weber.jsonparsing.Entity();
		entity.setCreate(c);
		entity.setRead(r);
		entity.setUpdate(u);
		entity.setDelete(d);
		entity.setAccessType(access);
		return entity;
	}
	private bsuite.weber.jsonparsing.Feature defineFeaturePermission(String featureName,String visible){
		bsuite.weber.jsonparsing.Feature feature = new bsuite.weber.jsonparsing.Feature();
		feature.setFeatureName(featureName);
		feature.setVisible(visible);		
		return feature;
	}
	
	private bsuite.weber.jsonparsing.Field defineFieldPermission(String fieldName, String readOnly, String visible){
		bsuite.weber.jsonparsing.Field field = new bsuite.weber.jsonparsing.Field();
		field.setFieldName(fieldName);
		field.setReadonly(readOnly);
		field.setVisible(visible);
		return field;
		
	}

	public void createDatabases(){
		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		CreateDatabase cd = new CreateDatabase();
		cd.createDatabases(moduleNames);
		
	}
	public void deploy(){
		createDatabases();//Create databases for modules
		createRoleDocs();//Create role documents for the role hierarchy created in security.nsf
		createProfileDocs();//Create the profile documents, standard and admin in Security.nsf
		
	}

}
