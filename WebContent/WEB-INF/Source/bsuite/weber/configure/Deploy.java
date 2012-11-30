package bsuite.weber.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.bsuite.utility.Utility;
import com.ibm.xsp.extlib.util.ExtLibUtil;


import bsuite.weber.jsonparsing.ProfileJson;


import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

public class Deploy {

	public void createRoleDocs(){
		
			
			createRoleDocuments(Utility.getDatabase("Security.nsf"));
		
	}
	public void createProfileDocs(){
		
			createProfileDocument(Utility.getDatabase("Security.nsf"), "Admin");
			createProfileDocument(Utility.getDatabase("Security.nsf"), "Standard");
	
	}
	public void testView(){
		try {
			ExtLibUtil.getCurrentDatabase().createView("Test View");
			
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
				roleDoc.save();
			} catch (NotesException e) {
				e.printStackTrace();
			}

		}
	}

	@SuppressWarnings("unchecked")
	public void createProfileDoc(String profileName) throws NotesException{
		createProfileDocument(Utility.getDatabase("Security.nsf"), profileName);
		
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
		pf.setProfName(profileName);
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
	private bsuite.weber.jsonparsing.Entity defineEntityPermission( String entityName, String c, String r, String u, String d,String access ){
		bsuite.weber.jsonparsing.Entity entity = new bsuite.weber.jsonparsing.Entity();
		entity.setEntityName(entityName);
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
		//createDatabases();//Create databases for modules
		createRoleDocs();//Create role documents for the role hierarchy created in security.nsf
		createProfileDocs();//Create the profile documents, standard and admin in Security.nsf
		
	}
	public ArrayList<Document> getProfileDocs(){
		Database db = null;
		View view = null;
		ArrayList<Document> dc = new ArrayList<Document>();
		Document doc = null;
		try {
			db = Utility.getDatabase("Security.nsf");
			view = db.getView("ProfileView");
			doc = view.getFirstDocument();
			while(doc!=null){
				dc.add(doc);
				doc = view.getNextDocument(doc);			
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return dc;
	}

	private ProfileJson getJsonObject(Document profDoc){
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
	
	private ProfileJson updateModule(ProfileJson jsonObj,Module module){
		
		return null;
	}
	private ProfileJson updateFeature(ProfileJson jsonObj,String moduleName, String featureName){
		System.out.println("21..");
		ArrayList<bsuite.weber.jsonparsing.Module> modules = jsonObj.getModules();
		System.out.println("22..");
		bsuite.weber.jsonparsing.Module module1 = null;
		if(modules.size()==0){
			return jsonObj;
		}
		
		for(bsuite.weber.jsonparsing.Module module:modules){
			
			if(module.getModuleName().equals(moduleName)){
				
				System.out.println("Modulename: "+module.getModuleName());
				module1 = module;
				break;
			}
		}
		System.out.println("23..");
		ArrayList<bsuite.weber.jsonparsing.Feature> featureList = module1.getFeatures();
		System.out.println("24..");
		for(bsuite.weber.jsonparsing.Feature feature:featureList){
			if(feature.getFeatureName().equals(featureName)){
				System.out.println("The feature "+featureName+" already exists");
				return jsonObj;
			}
		}
		System.out.println("25..");
		bsuite.weber.jsonparsing.Feature feature = defineFeaturePermission(featureName, "1");
		System.out.println("26..");
		featureList.add(feature);
		module1.setFeatures(featureList);
		
		return jsonObj;
	}
	private ProfileJson updateEntity(ProfileJson jsonObj,String moduleName, String entityName, Database db){
		System.out.println("21..");
		 View view = null;
		ArrayList<bsuite.weber.jsonparsing.Module> modules = jsonObj.getModules();
		System.out.println("22..");
		bsuite.weber.jsonparsing.Module module1 = null;
		if(modules.size()==0){
			return jsonObj;
		}
		
		for(bsuite.weber.jsonparsing.Module module:modules){
			
			if(module.getModuleName().equals(moduleName)){
				
				System.out.println("Modulename: "+module.getModuleName());
				module1 = module;
				break;
			}
		}
		System.out.println("23..");
		ArrayList<bsuite.weber.jsonparsing.Entity> entityList = module1.getEntities();
		System.out.println("24..");
		System.out.println("24.."+entityList.size()+" "+module1.getModuleName());
		for(bsuite.weber.jsonparsing.Entity entity:entityList){
			System.out.println("entityName"+entity.getEntityName()+" "+entityName);
			if(entity!=null && entity.getEntityName()!=null){
				if(entity.getEntityName().equals(entityName)){
					System.out.println("The entity "+entityName+" already exists");
					return jsonObj;
				}
			}
			
		}
		System.out.println("25..");
		bsuite.weber.jsonparsing.Entity entity = defineEntityPermission(entityName, "1", "1", "1", "1","1");
		System.out.println("26..");
		entityList.add(entity);
		module1.setEntities(entityList);
		System.out.println("added entity"+entity.getEntityName()+" "+entity.getCreate());
		//Create the view here after adding new entity for the existing fields
		
		String selFormula="SELECT Form=\""+entity.getEntityName()+"\"";
	
		CreateDatabase cd = new CreateDatabase();
	
		view = cd.createView(db,entity.getEntityName(), selFormula);
		System.out.println("after create view call");
		return jsonObj;
		
		
	}
	
	private ProfileJson updateField(ProfileJson jsonObj,String moduleName, String entityName, String fieldName, Database db, int vColumn){
		System.out.println("21..");
		View view = null;
		ArrayList<bsuite.weber.jsonparsing.Module> modules = jsonObj.getModules();
		System.out.println("22..");
		bsuite.weber.jsonparsing.Module module1 = null;
		if(modules.size()==0){
			return jsonObj;
		}
		
		for(bsuite.weber.jsonparsing.Module module:modules){
			
			if(module.getModuleName().equals(moduleName)){
				
				System.out.println("Modulename: "+module.getModuleName());
				module1 = module;
				break;
			}
		}
		bsuite.weber.jsonparsing.Entity entity1 = null;
		System.out.println("23..");
		ArrayList<bsuite.weber.jsonparsing.Entity> entityList = module1.getEntities();
		System.out.println("24..");
		System.out.println("24.."+entityList.size()+" "+module1.getModuleName());
		for(bsuite.weber.jsonparsing.Entity entity:entityList){
			System.out.println("entityName"+entity.getEntityName()+" "+entityName);
			if(entity!=null && entity.getEntityName()!=null){
				if(entity.getEntityName().equals(entityName)){
					entity1 = entity;
					break;
				}
			}
			
		}
		System.out.println("25..");
		//bsuite.weber.jsonparsing.Entity entity = defineEntityPermission(entityName, "1", "1", "1", "1","1");
		ArrayList<bsuite.weber.jsonparsing.Field> fieldList = null;
		if(entity1.getFields()==null){
			fieldList = new ArrayList<bsuite.weber.jsonparsing.Field>();
		}else{
		 fieldList= entity1.getFields();
		}
		for(bsuite.weber.jsonparsing.Field field:fieldList){
			if(field!=null && field.getFieldName()!=null){
				if(field.getFieldName().equals(fieldName)){
					System.out.println("The Field "+fieldName+" already exists");	
					return jsonObj;
				}
			}
		}
		
		
		bsuite.weber.jsonparsing.Field field = defineFieldPermission(fieldName, "0", "1");
		System.out.println("26..");
		fieldList.add(field);
		entity1.setFields(fieldList);
		System.out.println("added entity"+field.getFieldName()+" "+field.getVisible());
		
		CreateDatabase cd = new CreateDatabase();
		view = cd.getView(db, entityName);
		try {
			if(view.getColumnNames().contains(fieldName)){
				return jsonObj;
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cd.createViewColumn(view,vColumn,fieldName,fieldName);
		return jsonObj;
		
		
	}
	
	
	public void updateAllProfiles() throws NotesException, JsonGenerationException, JsonMappingException, IOException{
		System.out.println("1..");
		ArrayList<Document> dc = getProfileDocs();//Get document collection of all profiles
		System.out.println("2..");
		Document doc = null;
		String moduleName = null;
		String featureName = null;
		DefineModule df = new DefineModule();
		
		ProfileEdit pf = new ProfileEdit();
		CreateDatabase cd = new CreateDatabase();
		Database db = null;
		
		Vector<String> modules = df.getModules();//Get the list of module name from the schema
		System.out.println("3..");
		Vector<String> features = null;
		Vector<String> entities = null;
		Vector<String> fields = null;
		ProfileJson jsonObj =null; 
		View view = null;
		String entityName = "";
		String fieldName = "";
		ObjectMapper mapper = new ObjectMapper();
		for(int i=0;i<dc.size();i++){//For each profile document
			try {
				doc = dc.get(i);
				System.out.println("4..");
				jsonObj = getJsonObject(doc);//Get the json object of profile
				System.out.println("5..");
				for(int j=0;j<modules.size();j++){//For each module in the schema
					moduleName = modules.get(j);
					//db = pf.getDatabase(moduleName+".nsf");
					db = Utility.getDatabase(moduleName+".nsf");
					features = df.getFeatures(moduleName); //for the features defined in each module
					 entities = df.getEntityNames(moduleName);
					 view = null;
					System.out.println("6..");
					for(int k=0;k<features.size();k++){
						featureName = features.get(k);
						System.out.println("7..");
						System.out.println("module: "+moduleName+" feature: "+featureName);
						jsonObj = updateFeature(jsonObj,moduleName,featureName);	//update features
						System.out.println("8..");
					}
					
					
					
					for(int l=0;l<entities.size();l++){
						System.out.println("9..");
						entityName = entities.get(l);
						System.out.println("--->"+entities+"<--");
						System.out.println("module: "+moduleName+" entityName: "+entityName);
						jsonObj = updateEntity(jsonObj, moduleName,entityName,db);
						System.out.println("10..");
						
						fields = df.getFields(moduleName, entityName);
						System.out.println("---F>"+fields+"<--");
						for(int m=0;m<fields.size();m++){
							fieldName = fields.get(m);
							System.out.println("module: "+moduleName+" entityName: "+entityName+" FieldName"+fieldName);
							jsonObj = updateField(jsonObj, moduleName, entityName, fieldName,db,m+1);
							
							
							
						}
						doc.replaceItemValue("JsonString", mapper
								.writeValueAsString(jsonObj));
						doc.save();
						jsonObj = getJsonObject(doc);
						
						
					}
					doc.replaceItemValue("JsonString", mapper
							.writeValueAsString(jsonObj));
					doc.save();
					
				}
				
			} catch (NotesException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
