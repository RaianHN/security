package bsuite.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import bsuite.jsonparsing.ProfileEdit;
import bsuite.jsonparsing.ProfileJson;
import bsuite.utility.Utility;
import bsuite.relationship.Association;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;


 /**This class is used to create profile, create role and update permissions
  *@author JPrakash
  *@created Oct 6, 2013
 */
public class Deploy {

	public boolean createRoleDocs() {

		
		createRoleDocuments(Utility.getDatabase("Security.nsf"));
		return true;
	}

	public boolean createProfileDocs() {

		createProfileDocument(Utility.getDatabase("Security.nsf"), "Admin");
		createProfileDocument(Utility.getDatabase("Security.nsf"), "Standard");
		return true;
	}

	public void testView() {
		try {
			Utility.getCurrentDatabase().createView("Test View");

		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public void createRoleDocuments(Database db) {
		
		DefineModule define = new DefineModule();
		ArrayList<bsuite.configure.Role> roles = (ArrayList<bsuite.configure.Role>) define
				.getRoleList();
		
		View roleView = null;
		try {
			roleView = Utility.getCurrentDatabase().getView("RolesView");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		for (bsuite.configure.Role role : roles) {
			try {
				if (roleView.getDocumentByKey(role.getRoleName()) == null) {
					try {
						Document roleDoc = db.createDocument();
						roleDoc.replaceItemValue("Form", "Role");
						roleDoc.replaceItemValue("role_name", role
								.getRoleName());
						roleDoc.replaceItemValue("role_to", role
								.getRoleParent());
						roleDoc.save();
					} catch (NotesException e) {
						e.printStackTrace();
					}
				}
			} catch (NotesException e) {
				e.printStackTrace();
			}

		}
	}

	public void createProfileDoc(String profileName) throws NotesException {
		createProfileDocument(Utility.getDatabase("Security.nsf"), profileName);

	}

	@SuppressWarnings("unchecked")
	public void createProfileDocument(Database db, String profileName) {
		// Creates the profile document in security.nsf

		// get all the modules defined in security.nsf
		// and the schema defined for each entity
		// for each field and feature in entity set the permissions and add it
		// to entity list of profile and finally add the modules in profile
		// object
		// save it as a document in security database, by replacing JsonString
		// value

		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		ProfileJson pf = new ProfileJson();
		ArrayList<bsuite.jsonparsing.Module> modules = new ArrayList<bsuite.jsonparsing.Module>();
		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper mapper2 = new ObjectMapper();

		String JsonString = "";

		for (String moduleName : moduleNames) { // For each module set
												// permission
			
			
			bsuite.jsonparsing.Module moduleNew = defineModulePermission(
					moduleName, "1");
			

			String JsonModule = def.getModuleJson(moduleName);
			

			Module module = null;
			try {
				module = mapper.readValue(JsonModule, Module.class);
			} catch (JsonParseException e1) {
				e1.printStackTrace();
			} catch (JsonMappingException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// Added for the module features
			

			ArrayList<Feature> features = module.getFeatures();
			

			if (features != null) {
				ArrayList<bsuite.jsonparsing.Feature> featureList = new ArrayList<bsuite.jsonparsing.Feature>();// Create
																												// empty
																												// feature
																												// list
																												// for
																												// this
																												// module
				for (Feature f : features) {
					bsuite.jsonparsing.Feature feature = defineFeaturePermission(
							f.getFeatureName(), "1");
					featureList.add(feature);
				}
				moduleNew.setFeatures(featureList);
			}

			

			ArrayList<Entity> entities = module.getEntities();
			if (entities != null) {
				
				ArrayList<bsuite.jsonparsing.Entity> entityList = new ArrayList<bsuite.jsonparsing.Entity>();// Create
																												// empty
																												// EntityList
																												// for
																												// this
																												// module
				for (Entity e : entities) {// For each entity set permission
					bsuite.jsonparsing.Entity entity = defineEntityPermission(
							moduleName, e.getEntityName(), "1", "1", "1", "1",
							"1");
					ArrayList<Field> fields = e.getFields();

					ArrayList<bsuite.jsonparsing.Field> fieldList = new ArrayList<bsuite.jsonparsing.Field>();// create
																												// empty
																												// FieldList
																												// for
																												// this
																												// entity
					for (Field f : fields) {
						bsuite.jsonparsing.Field field = defineFieldPermission(
								f.getFieldName(), "0", "1");
						fieldList.add(field);
					}
					

					entity.setEntityName(e.getEntityName());
					entity.setFields(fieldList);
					entityList.add(entity);
					
				}
				moduleNew.setEntities(entityList);

			}
			modules.add(moduleNew);
			

		}

		pf.setModules(modules);
		pf.setProfName(profileName);
		
		try {
			JsonString = mapper2.writeValueAsString(pf);
			
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
			doc.replaceItemValue("JsonString", JsonString);
			doc.save();

		} catch (NotesException e) {

			e.printStackTrace();
		}

	}

	public void updateModule(String moduleName) {
		ArrayList<Document> dc = getProfileDocs();// Get document collection of
													// all profiles
		Document doc = null;
		for (int i = 0; i < dc.size(); i++) {// For each profile document
			doc = dc.get(i);
			updateProfileDocument(doc, moduleName);
		}

	}

	private void updateProfileDocument(Document doc, String moduleName) {
		// updates the already created profile document in security.nsf

		// get all the modules defined in security.nsf
		// and the schema defined for each entity
		// for each field and feature in entity set the permissions and add it
		// to entity list of profile and finally add the modules in profile
		// object
		// save it as a document in security database, by replacing JsonString
		// value

		DefineModule def = new DefineModule();
		ProfileJson pf = getJsonObject(doc);

		ArrayList<bsuite.jsonparsing.Module> modules = pf.getModules();

		ObjectMapper mapper = new ObjectMapper();
		ObjectMapper mapper2 = new ObjectMapper();

		String JsonString = "";

		bsuite.jsonparsing.Module moduleNew = defineModulePermission(
				moduleName, "1");// Default make the module visible after adding
									// it

		String JsonModule = def.getModuleJson(moduleName);

		Module module = null;
		try {
			module = mapper.readValue(JsonModule, Module.class);
		} catch (JsonParseException e1) {
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Added for the module features
		

		ArrayList<Feature> features = module.getFeatures();
		

		if (features != null) {
			ArrayList<bsuite.jsonparsing.Feature> featureList = new ArrayList<bsuite.jsonparsing.Feature>();// Create
																											// empty
																											// feature
																											// list
																											// for
																											// this
																											// module
			for (Feature f : features) {
				bsuite.jsonparsing.Feature feature = defineFeaturePermission(f
						.getFeatureName(), "1");
				featureList.add(feature);
			}
			moduleNew.setFeatures(featureList);
		}

		ArrayList<Entity> entities = module.getEntities();
		if (entities != null) {
			
			ArrayList<bsuite.jsonparsing.Entity> entityList = new ArrayList<bsuite.jsonparsing.Entity>();// Create
																											// empty
																											// EntityList
																											// for
																											// this
																											// module
			for (Entity e : entities) {// For each entity set permission
				bsuite.jsonparsing.Entity entity = defineEntityPermission(
						moduleName, e.getEntityName(), "1", "1", "1", "1", "1");
				ArrayList<Field> fields = e.getFields();

				ArrayList<bsuite.jsonparsing.Field> fieldList = new ArrayList<bsuite.jsonparsing.Field>();// create
																											// empty
																											// FieldList
																											// for
																											// this
																											// entity
				for (Field f : fields) {
					bsuite.jsonparsing.Field field = defineFieldPermission(f
							.getFieldName(), "0", "1");
					fieldList.add(field);
				}
				

				entity.setEntityName(e.getEntityName());
				entity.setFields(fieldList);
				entityList.add(entity);
				
			}
			moduleNew.setEntities(entityList);

		}

		ArrayList<SchemaGroup> groups = module.getGroups();

		if (groups != null) {
			ArrayList<bsuite.jsonparsing.GroupPermission> groupPermissions = new ArrayList<bsuite.jsonparsing.GroupPermission>();// Create
																																	// empty
																																	// grouppermissions
																																	// for
																																	// this
																																	// module
			for (SchemaGroup sg : groups) {
				bsuite.jsonparsing.GroupPermission groupPerm = defineGroupPermission(
						sg.getGroupName(), "1");
				ArrayList<String> groupentries = sg.getGroupEntries();
				ArrayList<bsuite.jsonparsing.GroupEntry> groupEntries = new ArrayList<bsuite.jsonparsing.GroupEntry>();
				
				if(groupentries==null){
					continue;
				}
				
				
				for (String group : groupentries) {
					bsuite.jsonparsing.GroupEntry groupentry = null;
					if (group.substring(0, 1).equals("e")) {
						groupentry = defineGroupEntryPermission(group
								.substring(2), "e", "1");
					} else {
						groupentry = defineGroupEntryPermission(group
								.substring(2), "f", "1");
					}
					groupEntries.add(groupentry);
				}
				
				groupPerm.setEntries(groupEntries);
				groupPermissions.add(groupPerm);
				
			}
			moduleNew.setGroups(groupPermissions);
			
		}

		modules.add(moduleNew);

		

		pf.setModules(modules);
		
		try {
			JsonString = mapper2.writeValueAsString(pf);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			
			doc.replaceItemValue("JsonString", JsonString);
			doc.save();

		} catch (NotesException e) {

			e.printStackTrace();
		}

	}

	private bsuite.jsonparsing.Module defineModulePermission(String moduleName,
			String visibility) {
		
		bsuite.jsonparsing.Module module = new bsuite.jsonparsing.Module();
		module.setModuleName(moduleName);
		module.setTabvis(visibility);
		return module;

	}

	private bsuite.jsonparsing.Entity defineEntityPermission(String moduleName,
			String entityName, String c, String r, String u, String d,
			String access) {
		bsuite.jsonparsing.Entity entity = new bsuite.jsonparsing.Entity();
		entity.setCreate(c);
		entity.setRead(r);
		entity.setUpdate(u);
		entity.setDelete(d);
		entity.setAccessType(access);
		return entity;
	}

	private bsuite.jsonparsing.Entity defineEntityPermission(String entityName,
			String c, String r, String u, String d, String access) {
		bsuite.jsonparsing.Entity entity = new bsuite.jsonparsing.Entity();
		entity.setEntityName(entityName);
		entity.setCreate(c);
		entity.setRead(r);
		entity.setUpdate(u);
		entity.setDelete(d);
		entity.setAccessType(access);
		return entity;
	}

	private bsuite.jsonparsing.Feature defineFeaturePermission(
			String featureName, String visible) {
		bsuite.jsonparsing.Feature feature = new bsuite.jsonparsing.Feature();
		feature.setFeatureName(featureName);
		feature.setVisible(visible);
		return feature;
	}

	private bsuite.jsonparsing.GroupPermission defineGroupPermission(
			String groupName, String visible) {
		bsuite.jsonparsing.GroupPermission gp = new bsuite.jsonparsing.GroupPermission();
		gp.setName(groupName);
		gp.setVisible(visible);
		return gp;
	}

	private bsuite.jsonparsing.GroupEntry defineGroupEntryPermission(
			String entryName, String entryType, String visible) {
		bsuite.jsonparsing.GroupEntry ge = new bsuite.jsonparsing.GroupEntry();
		ge.setName(entryName);
		ge.setType(entryType);
		ge.setVisible(visible);
		return ge;
	}

	private bsuite.jsonparsing.Field defineFieldPermission(String fieldName,
			String readOnly, String visible) {
		bsuite.jsonparsing.Field field = new bsuite.jsonparsing.Field();
		field.setFieldName(fieldName);
		field.setReadonly(readOnly);
		field.setVisible(visible);
		return field;

	}

	private bsuite.jsonparsing.EntityAction defineEntityActionPerm(
			String actionName, String visible) {
		bsuite.jsonparsing.EntityAction action = new bsuite.jsonparsing.EntityAction();
		action.setActionName(actionName);
		action.setVisible(visible);
		return action;

	}

	@SuppressWarnings("unchecked")
	public void createDatabases() {
		DefineModule def = new DefineModule();
		Vector<String> moduleNames = def.getModules();
		CreateDatabase cd = new CreateDatabase();
		cd.createDatabases(moduleNames);

	}

	public boolean deploy() {
		createRoleDocs();// Create role documents for the role hierarchy created
							// in security.nsf
		createProfileDocs();// Create the profile documents, standard and admin
							// in Security.nsf

		return true;
	}



	public void deploy(String user) {
		
		createRoleDocs();
		
		createProfileDocs();

		
		if (user != null && !user.equals("")) {




		} else {
			
		}

	}

	public ArrayList<Document> getProfileDocs() {
		Database db = null;
		View view = null;
		ArrayList<Document> dc = new ArrayList<Document>();
		Document doc = null;
		try {
			db = Utility.getDatabase("Security.nsf");
			view = db.getView("ProfileView");
			doc = view.getFirstDocument();
			while (doc != null) {
				dc.add(doc);
				doc = view.getNextDocument(doc);
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return dc;
	}

	private ProfileJson getJsonObject(Document profDoc) {
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

	private ProfileJson updateFeature(ProfileJson jsonObj, String moduleName,
			String featureName) {
		
		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				
				module1 = module;
				break;
			}
		}
		
		ArrayList<bsuite.jsonparsing.Feature> featureList = module1
				.getFeatures();
		
		if (featureList != null) {
			for (bsuite.jsonparsing.Feature feature : featureList) {

				if (feature.getFeatureName().equals(featureName)) {

					return jsonObj;
				}
			}
		} else {
			featureList = new ArrayList<bsuite.jsonparsing.Feature>();
		}

		
		bsuite.jsonparsing.Feature feature = defineFeaturePermission(
				featureName, "1");
		
		featureList.add(feature);
		module1.setFeatures(featureList);

		return jsonObj;
	}

	private ProfileJson updateEntity(ProfileJson jsonObj, String moduleName,
			String entityName, Database db) {
		
		@SuppressWarnings("unused")
		View view = null;
		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				
				module1 = module;
				break;
			}
		}
		
		ArrayList<bsuite.jsonparsing.Entity> entityList = module1.getEntities();
		

		if (entityList != null) {

			for (bsuite.jsonparsing.Entity entity : entityList) {

				if (entity != null && entity.getEntityName() != null) {
					if (entity.getEntityName().equals(entityName)) {

						return jsonObj;
					}
				}

			}
		} else {
			entityList = new ArrayList<bsuite.jsonparsing.Entity>();
		}

		
		bsuite.jsonparsing.Entity entity = defineEntityPermission(entityName,
				"1", "1", "1", "1", "1");
		
		entityList.add(entity);
		module1.setEntities(entityList);

	

		

		return jsonObj;

	}

	private ProfileJson updateField(ProfileJson jsonObj, String moduleName,
			String entityName, String fieldName, Database db, int vColumn) {
		
		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				
				module1 = module;
				break;
			}
		}
		bsuite.jsonparsing.Entity entity1 = null;
		
		ArrayList<bsuite.jsonparsing.Entity> entityList = module1.getEntities();
		

		for (bsuite.jsonparsing.Entity entity : entityList) {

			if (entity != null && entity.getEntityName() != null) {
				if (entity.getEntityName().equals(entityName)) {
					entity1 = entity;
					break;
				}
			}

		}
		

		ArrayList<bsuite.jsonparsing.Field> fieldList = null;
		if (entity1.getFields() == null) {
			fieldList = new ArrayList<bsuite.jsonparsing.Field>();
		} else {
			fieldList = entity1.getFields();
		}
		for (bsuite.jsonparsing.Field field : fieldList) {
			if (field != null && field.getFieldName() != null) {
				if (field.getFieldName().equals(fieldName)) {

					return jsonObj;
				}
			}
		}

		bsuite.jsonparsing.Field field = defineFieldPermission(fieldName, "0",
				"1");
		
		fieldList.add(field);
		entity1.setFields(fieldList);


		
		return jsonObj;

	}

	private ProfileJson updateEntityAction(ProfileJson jsonObj,
			String moduleName, String entityName, String entityAction) {

		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				
				module1 = module;
				break;
			}
		}
		bsuite.jsonparsing.Entity entity1 = null;
		
		ArrayList<bsuite.jsonparsing.Entity> entityList = module1.getEntities();
		

		for (bsuite.jsonparsing.Entity entity : entityList) {

			if (entity != null && entity.getEntityName() != null) {
				if (entity.getEntityName().equals(entityName)) {
					entity1 = entity;
					break;
				}
			}

		}

		ArrayList<bsuite.jsonparsing.EntityAction> actionList = null;
		if (entity1.getActions() == null) {
			actionList = new ArrayList<bsuite.jsonparsing.EntityAction>();
		} else {
			actionList = entity1.getActions();
		}
		for (bsuite.jsonparsing.EntityAction ea : actionList) {
			if (ea != null && ea.getActionName() != null) {
				if (ea.getActionName().equals(entityAction)) {
					return jsonObj;
				}

			}
		}

		bsuite.jsonparsing.EntityAction action = defineEntityActionPerm(
				entityAction, "1");

		actionList.add(action);
		entity1.setActions(actionList);

		return jsonObj;

	}

	/**
	 *[This method is used to update the given profile document once a new profile document is created.]
	 *@return
	 */
	@SuppressWarnings("unchecked")
	public boolean updateProfileDoc(String profileName)throws NotesException, JsonGenerationException, JsonMappingException, IOException{
		
		Document doc = null;
		String moduleName = null;
		String featureName = null;
		DefineModule df = new DefineModule();
		
		Database db = null;
		
		Vector<String> modules = df.getModules();//Get the list of module name from the schema
		
		Vector<String> features = null;
		Vector<String> entities = null;
		Vector<String> fields = null;
		Vector<String> actions = null;
		Vector<String> groups = null;
		ArrayList groupActions = null;
		
		
		ProfileJson jsonObj =null; 
		String entityName = "";
		String fieldName = "";
		String groupName = "";
		ObjectMapper mapper = new ObjectMapper();
		ProfileEdit pe = new ProfileEdit();
		
			try {
				doc = pe.getProfileDoc(Utility.getCurrentDatabase(), profileName);
				
				jsonObj = getJsonObject(doc);//Get the json object of profile
				
				for(int j=0;j<modules.size();j++){//For each module in the schema
					moduleName = modules.get(j);
					db = Utility.getDatabase(moduleName+".nsf");
					features = df.getFeatures(moduleName); //for the features defined in each module
					 entities = df.getEntityNames(moduleName);
					 groups = df.getGroupNames(moduleName);
					 if(features==null){
						
					}
					
					
					if(features!=null){
						for(int k=0;k<features.size();k++){
							featureName = features.get(k);
							
							
							jsonObj = updateFeature(jsonObj,moduleName,featureName);	//update features
							
						}
					}
					
					
					if(groups!=null){
						
						for(int g=0;g<groups.size();g++){
							
							groupName = groups.get(g);
							
							jsonObj = updateGroup(jsonObj,moduleName,groupName);
							
							groupActions = df.getGroupEntryNames(moduleName, groupName);
							
							if(groupActions!=null){
								for(int ga=0;ga<groupActions.size();ga++){
									
									if (((String) groupActions.get(ga)).substring(0, 1).equals("e")) {
										
										jsonObj = updateGroupAction(jsonObj,moduleName,groupName,((String) groupActions.get(ga)).substring(2),"e");
										
									} else {
										jsonObj = updateGroupAction(jsonObj,moduleName,groupName,((String) groupActions.get(ga)).substring(2),"f");
									}
									
								}
							}
						}
					}
					
					
					
					if(entities!=null){
						for(int l=0;l<entities.size();l++){
							
							entityName = entities.get(l);
							
							
							jsonObj = updateEntity(jsonObj, moduleName,entityName,db);
							
							
							fields = df.getFields(moduleName, entityName);
							actions = df.getEntityActions(moduleName, entityName);
							
							if(actions!=null){
								for(int n=0;n<actions.size();n++){
									jsonObj = updateEntityAction(jsonObj, moduleName, entityName, actions.get(n));
								}
							}
							
							
							
							if(fields!=null){
								for(int m=0;m<fields.size();m++){
									fieldName = fields.get(m);
									
									jsonObj = updateField(jsonObj, moduleName, entityName, fieldName,db,m+1);
								}
								
								doc.replaceItemValue("JsonString", mapper
										.writeValueAsString(jsonObj));
								doc.save();
								jsonObj = getJsonObject(doc);	
							}
							
							
							
						}	
					}
					
					
					
					doc.replaceItemValue("JsonString", mapper
							.writeValueAsString(jsonObj));
					doc.save();
					
				}
				
			} catch (NotesException e) {
				e.printStackTrace();
			}
		return true;
	}
	@SuppressWarnings("unchecked")
	public void updateAllProfiles() throws NotesException, JsonGenerationException, JsonMappingException, IOException{
		
		ArrayList<Document> dc = getProfileDocs();//Get document collection of all profiles
		
		Document doc = null;
		String moduleName = null;
		String featureName = null;
		DefineModule df = new DefineModule();
		
		Database db = null;
		
		Vector<String> modules = df.getModules();//Get the list of module name from the schema
		
		Vector<String> features = null;
		Vector<String> entities = null;
		Vector<String> fields = null;
		Vector<String> actions = null;
		Vector<String> groups = null;
		ArrayList groupActions = null;
		
		
		ProfileJson jsonObj =null; 
		String entityName = "";
		String fieldName = "";
		String groupName = "";
		ObjectMapper mapper = new ObjectMapper();
		for(int i=0;i<dc.size();i++){//For each profile document
			try {
				doc = dc.get(i);
				
				jsonObj = getJsonObject(doc);//Get the json object of profile
				
				for(int j=0;j<modules.size();j++){//For each module in the schema
					moduleName = modules.get(j);
					db = Utility.getDatabase(moduleName+".nsf");
					features = df.getFeatures(moduleName); //for the features defined in each module
					 entities = df.getEntityNames(moduleName);
					 groups = df.getGroupNames(moduleName);
					 if(features==null){
						
					}
					
					
					if(features!=null){
						for(int k=0;k<features.size();k++){
							featureName = features.get(k);
							
							
							jsonObj = updateFeature(jsonObj,moduleName,featureName);	//update features
							
						}
					}
					
					
					if(groups!=null){
						
						for(int g=0;g<groups.size();g++){
							
							groupName = groups.get(g);
							
							jsonObj = updateGroup(jsonObj,moduleName,groupName);
							
							groupActions = df.getGroupEntryNames(moduleName, groupName);
							
							if(groupActions!=null){
								for(int ga=0;ga<groupActions.size();ga++){
									
									if (((String) groupActions.get(ga)).substring(0, 1).equals("e")) {
										
										jsonObj = updateGroupAction(jsonObj,moduleName,groupName,((String) groupActions.get(ga)).substring(2),"e");
										
									} else {
										jsonObj = updateGroupAction(jsonObj,moduleName,groupName,((String) groupActions.get(ga)).substring(2),"f");
									}
									
								}
							}
						}
					}
					
					
					
					if(entities!=null){
						for(int l=0;l<entities.size();l++){
							
							entityName = entities.get(l);
							
							
							jsonObj = updateEntity(jsonObj, moduleName,entityName,db);
							
							
							fields = df.getFields(moduleName, entityName);
							actions = df.getEntityActions(moduleName, entityName);
							
							if(actions!=null){
								for(int n=0;n<actions.size();n++){
									jsonObj = updateEntityAction(jsonObj, moduleName, entityName, actions.get(n));
								}
							}
							
							
							
							if(fields!=null){
								for(int m=0;m<fields.size();m++){
									fieldName = fields.get(m);
									
									jsonObj = updateField(jsonObj, moduleName, entityName, fieldName,db,m+1);
								}
								
								doc.replaceItemValue("JsonString", mapper
										.writeValueAsString(jsonObj));
								doc.save();
								jsonObj = getJsonObject(doc);	
							}
							
							
							
						}	
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

	private ProfileJson updateGroupAction(ProfileJson jsonObj,
			String moduleName, String groupName, String actionName, String entryType) {
		
		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				module1 = module;
				break;
			}
		}
		bsuite.jsonparsing.GroupPermission group1 = null;

		ArrayList<bsuite.jsonparsing.GroupPermission> groupList = module1.getGroups();
		
		for(bsuite.jsonparsing.GroupPermission groupP : groupList){
			if(groupP!=null && groupP.getName()!=null){
				if(groupP.getName().equals(groupName)){
					group1 = groupP;
					break;
				}
				
			}
		}
		ArrayList<bsuite.jsonparsing.GroupEntry> actionList = null;
		if(group1.getEntries()==null){
			actionList = new ArrayList<bsuite.jsonparsing.GroupEntry>();
		}else{
			actionList = group1.getEntries();
		}
		
		for(bsuite.jsonparsing.GroupEntry ge: actionList){
			if(ge!=null && ge.getName()!=null){
				if(ge.getName().equals(actionName)){
					return jsonObj;
				}
			}
		}
		bsuite.jsonparsing.GroupEntry entry = defineGroupEntryPermission(actionName, entryType, "1");

		

		actionList.add(entry);
		group1.setEntries(actionList);

		return jsonObj;
	}

	private ProfileJson updateGroup(ProfileJson jsonObj, String moduleName,
			String groupName) {
		
		ArrayList<bsuite.jsonparsing.Module> modules = jsonObj.getModules();
		
		bsuite.jsonparsing.Module module1 = null;
		if (modules.size() == 0) {
			return jsonObj;
		}

		for (bsuite.jsonparsing.Module module : modules) {

			if (module.getModuleName().equals(moduleName)) {

				module1 = module;
				break;
			}
		}
		ArrayList<bsuite.jsonparsing.GroupPermission> groupList = module1.getGroups();
		if(groupList!=null){
			for(bsuite.jsonparsing.GroupPermission group:groupList){
				if(group!=null && group.getName()!=null){
					if(group.getName().equals(groupName)){
						return jsonObj;
					}
				}
			}
		}else{
			groupList = new ArrayList<bsuite.jsonparsing.GroupPermission>();
		}
		bsuite.jsonparsing.GroupPermission groupP = defineGroupPermission(groupName, "1");
		groupList.add(groupP);
		module1.setGroups(groupList);
		
		return jsonObj;
		
		
	}
	public  static Document getFirsProfileDoc(){
		Database db = null;
		View view = null;
		Document doc = null;
		try {
			db = Utility.getCurrentDatabase();
			view = db.getView("ProfileView");
			doc = view.getFirstDocument();
			
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Used to delete the profile by giving an alternate profile
	 *@param profileName deletable profile
	 *@param replPrfName replacement profile name
	 */
	public static boolean deleteProfile(String profileName, String replPrfName){
		//validate if profile exists
		//validate if replacement profile exists
		//Get all associated persons to this profile
		//For each person change the profile association
		//remove and return the result
		
		ProfileEdit pf = new ProfileEdit();
		Document profileDoc = pf.getProfileDoc(Utility.getCurrentDatabase(),profileName);
		
		if(pf==null){
			Utility.addErrorMessage("Selected profile is not found");
			return false;
		}
		
		
		if(pf.getProfileDoc(Utility.getCurrentDatabase(),profileName)==null){
			Utility.addErrorMessage("Selected replacement profile is not found");
			return false;
		}
		
		Association assoc = new Association();		
		ArrayList<String> associatedProfileUsers = assoc.getAssociatedProfileUsers(profileName);
		
		CreateDatabase cd = new CreateDatabase();
		
		
		for(String user:associatedProfileUsers){
			cd.createProfileAssociation(user, replPrfName);
		}
		
		try
		{
			if(profileDoc.remove(true)){
				Utility.addConfirmMessage("Profile document has been deleted successfully");
			}else{
				Utility.addErrorMessage("Error in deleting the profile, check delete permission ");
			}
		}
		catch (NotesException e)
		{
			Utility.addErrorMessage("Error in deleting the profile");
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
}
