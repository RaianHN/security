package bsuite.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import bsuite.utility.Utility;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;

@SuppressWarnings("unused")
public class DefineModule {
	Database currentdb;

	public DefineModule() {
		currentdb = Utility.getCurrentDatabase();
	}

	public void addModules(Vector<String> modules) {
		System.out.println("inside add module");
		for (String moduleName : modules) {
			String moduleJson = createJsonString(moduleName);
			createModuleDocument(moduleJson, moduleName);
		}

	}

	public void addModules(String module) {
		System.out.println("inside add module");
		String moduleJson = createJsonString(module);
		createModuleDocument(moduleJson, module);
	}

	/**
	 * @param moduleJson
	 *            : Json string created for this module This method creates a
	 *            document in the current database
	 */
	private void createModuleDocument(String moduleJson, String moduleName) {

		try {
			Document doc = Utility.getCurrentDatabase().createDocument();
			doc.replaceItemValue("obid", "module");
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
	 * @return JsonString This method generates the json string for this module
	 */
	private String createJsonString(String moduleName) {

		Module module = new Module();
		module.setModuleName(moduleName);

		ObjectMapper mapper = new ObjectMapper();
	
		try {
			return mapper.writeValueAsString(module);
		} catch (Exception e) {

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public void addFeatures(String moduleName, Vector<String> featurenames) {
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Feature> features = null;
			if (module.getFeatures() == null) {
				features = new ArrayList();
			} else {
				features = module.getFeatures();
			}

			// ArrayList<Feature> featureList = new ArrayList<Feature>();

			for (String s : featurenames) {
				// Add features to the new entity
				Feature feature = new Feature();
				feature.setFeatureName(s);

				features.add(feature);

				// Add to entFeat list also
				ManageGroup.addEntFeat(module, "f:"+feature.getFeatureName());
			}
			module.setFeatures(features);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public boolean addFeatureGroup(String moduleName, String groupName) {
		// To add a new feature group for this module
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			module = ManageGroup.createGroup(module, groupName);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

		return true;
	}
	
	public boolean removeFeatureGroup(String moduleName, String groupName){
		// To remove feature group for this module
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			module = ManageGroup.deleteGroup(module, groupName);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

		return true;
		
	}

	public boolean addObjectToGrp(String moduleName, String groupName,
			String type, String name) {
		// Called from outside to add an entry into the group
		// type is feature or entity
		// name is the name of the feature or entity

		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<SchemaGroup> groupList = module.getGroups();
			String entFea = null;

			if (type.equals("f")) {
				entFea = "f:"+getFeatureObj(module, name).getFeatureName();
			} else if (type.equals("e")) {
				
				entFea = "e:"+getEntityObj(module, name).getEntityName();
			}

			if (entFea == null) {
				return false;
			}

			if (groupList != null) {
				SchemaGroup group = null;
				for (SchemaGroup s : groupList) {
					if (s.getGroupName().equals(groupName)) {
						group = s;
						break;
					}
					
				}
				addGroupEntry(module, group, entFea);
			}

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

		return true;
	}
	private boolean addGroupEntry(Module module, SchemaGroup group, String entry){
		ManageGroup.addObjToGrp(module, group,entry);
		return true;
	}

	/*private boolean addGroupEntryFeature(Module module, SchemaGroup group, Feature feature) {
		// To add entity or feature to this group
		ManageGroup.addObjToGrp(module, group,"f:"+feature.getFeatureName());

		return true;
	}*/

	public boolean renameFeatureGroup(String moduleName, String groupName,
			String newName) {
		// To rename feature group for this module
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			SchemaGroup grp = null;

			ArrayList<SchemaGroup> grps = null;
			if (module.getGroups() == null) {
				return false;
			} else {
				grps = module.getGroups();
			}

			for (SchemaGroup g : grps) {

				if (g.getGroupName().equals(groupName)) {
					grp = g;
				}
			}

			module = ManageGroup.renameGroup(module, grp, newName);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

		return true;
	}

	@SuppressWarnings("unchecked")
	public void addFeatures(String moduleName, String featurename) {
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Feature> features = null;
			if (module.getFeatures() == null) {
				features = new ArrayList();
			} else {
				features = module.getFeatures();
			}

			// ArrayList<Feature> featureList = new ArrayList<Feature>();

			// Add features to the new entity
			Feature feature = new Feature();
			feature.setFeatureName(featurename);

			features.add(feature);

			// Add to entFeat list also
			ManageGroup.addEntFeat(module, "f:"+feature.getFeatureName());

			module.setFeatures(features);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	
	
	@SuppressWarnings("unchecked")
	public void addEntity(String moduleName, String entityName,
			Vector<String> fields) {

		try {

			View modulesView = currentdb.getView("Modules");

			// System.out.println("Document name"+modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}

			Entity entity = new Entity();

			ArrayList<Field> fieldsList = new ArrayList<Field>();

			for (String s : fields) {
				// Add fields to the new entity
				Field field = new Field();
				field.setFieldName(s);
				fieldsList.add(field);
			}

			entity.setEntityName(entityName);
			entity.setFields(fieldsList);
			entities.add(entity);

			// Add to entFeat list also
			ManageGroup.addEntFeat(module, "e:"+entity.getEntityName());

			module.setEntities(entities);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public void addEntity(String moduleName, String entityName) {

		try {

			View modulesView = currentdb.getView("Modules");

			// System.out.println("Document name"+modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}

			Entity entity = new Entity();

			entity.setEntityName(entityName);
			entities.add(entity);

			// Add to entFeat list also
			ManageGroup.addEntFeat(module, "e:"+entity.getEntityName());

			module.setEntities(entities);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public void addField(String moduleName, String entityName, String field) {

		try {
			System.out.println("adding field1");
			View modulesView = currentdb.getView("Modules");

			System.out.println("Document name"
					+ modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);
			System.out.println("adding field2");
			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}
			Entity entity = null;
			System.out.println("adding field3");
			for (Entity e : entities) {
				if (entities != null) {
					if (e.getEntityName().equals(entityName)) {
						entity = e;
						break;
					}
				}
			}

			ArrayList<Field> fieldsList = entity.getFields();
			System.out.println("adding field4");
			if (fieldsList == null) {
				fieldsList = new ArrayList<Field>();
			}
			// Add fields to the new entity
			Field field1 = new Field();
			field1.setFieldName(field);
			fieldsList.add(field1);

			entity.setFields(fieldsList);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public void addEntityAction(String moduleName, String entityName, String actionName) {

		try {
			View modulesView = currentdb.getView("Modules");

			
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);
			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}
			Entity entity = null;
			for (Entity e : entities) {
				if (entities != null) {
					if (e.getEntityName().equals(entityName)) {
						entity = e;
						break;
					}
				}
			}

			ArrayList<EntityAction> actionList = entity.getActions();
			
			if (actionList == null) {
				actionList = new ArrayList<EntityAction>();
			}
			// Add actions to the new entity
			EntityAction action  = new EntityAction();
			action.setActionName(actionName);
			actionList.add(action);
			
			
			entity.setActions(actionList);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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
	
	@SuppressWarnings("unchecked")
	public Vector getModules() {
		System.out.println("inside get modules");
		View moduleView = null;
		try {
			moduleView = currentdb.getView("Modules");
		} catch (NotesException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Document doc;
		Vector moduleNames = new Vector();
		try {
			doc = moduleView.getFirstDocument();
			while (doc != null) {
				moduleNames.add(doc.getItemValueString("moduleName"));
				doc = moduleView.getNextDocument(doc);
			}

		} catch (NotesException e) {

			e.printStackTrace();
		}

		return moduleNames;
	}

	public void addRole(String roleName, String roleParent) {
		Document roleDoc = getRoleDocument();
		String roleJson = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e) {

			e.printStackTrace();
		}
		System.out.println("add r1");
		if (roleJson.equals("")) {
			RoleHierarchy rH = new RoleHierarchy();
			ArrayList<Role> roleList = new ArrayList<Role>();
			Role role = new Role();
			role.setRoleName(roleName);
			role.setRoleParent(roleParent);
			roleList.add(role);
			rH.setRoleList(roleList);

			try {
				roleDoc.replaceItemValue("JsonString", mapper
						.writeValueAsString(rH));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (NotesException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("add r2");
			try {
				RoleHierarchy rH = mapper.readValue(roleJson,
						RoleHierarchy.class);
				ArrayList<Role> roleList = rH.getRoleList();
				Role role = new Role();
				role.setRoleName(roleName);
				role.setRoleParent(roleParent);
				roleList.add(role);
				rH.setRoleList(roleList);

				try {
					roleDoc.replaceItemValue("JsonString", mapper
							.writeValueAsString(rH));
				} catch (NotesException e) {
					e.printStackTrace();
				}

			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		try {
			roleDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("add r3");

	}

	@SuppressWarnings("unchecked")
	public Vector getRoles() {
		Document roleDoc = getRoleDocument();
		String roleJson = "";
		ObjectMapper mapper = new ObjectMapper();
		RoleHierarchy rH = null;
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}

		try {
			if (!roleJson.equals("")) {
				rH = mapper.readValue(roleJson, RoleHierarchy.class);
			} else {
				return null;
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Role> roleList = rH.getRoleList();

		if (roleList != null) {
			Vector roleNames = new Vector();
			for (Role r : roleList) {
				roleNames.add(r.getRoleName());
			}
			return roleNames;
		}

		return null;
	}

	private Document getRoleDocument() {

		try {
			System.out.println("inside get role doc");
			View rolesView = currentdb.getView("Roles");
			Document roleDoc = rolesView.getDocumentByKey("roleHierarchy");
			if (roleDoc == null) {
				Document doc = currentdb.createDocument();
				doc.replaceItemValue("obid", "roleHierarchy");
				doc.replaceItemValue("JsonString", "");
				doc.replaceItemValue("Form", "roleHierarchy");
				doc.save();
				return doc;
			}
			return roleDoc;

		} catch (NotesException e) {

			e.printStackTrace();
		}
		return null;

	}

	public ArrayList<Role> getRoleList() {
		Document roleDoc = getRoleDocument();
		String roleJson = "";
		ObjectMapper mapper = new ObjectMapper();
		RoleHierarchy rH = null;
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}

		try {
			if (!roleJson.equals("")) {
				rH = mapper.readValue(roleJson, RoleHierarchy.class);
			} else {
				return null;
			}

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Role> roleList = rH.getRoleList();

		return roleList;

	}

	/*
	 * private DocumentCollection getModuleDocs(){
	 * 
	 * try { View moduleView = currentdb.getView("Modules"); DocumentCollection
	 * moduleDocs =
	 * 
	 * 
	 * return moduleDocs; } catch (NotesException e) { e.printStackTrace(); }
	 * 
	 * 
	 * return null; }
	 */
	private Document getModuleDoc(String moduleName) {
		try {
			View moduleView = currentdb.getView("Modules");
			Document moduleDoc = moduleView.getDocumentByKey(moduleName);
			return moduleDoc;
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getModuleJson(String moduleName) {
		Document moduleDoc = getModuleDoc(moduleName);
		try {
			return moduleDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Vector getEntityNames(String moduleName) {
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

		if (entityList != null) {
			Vector entityNames = new Vector();
			for (Entity e : entityList) {
				entityNames.add(e.getEntityName());
			}
			return entityNames;
		}

		return null;
	}

	/**
	 * This method is used to get the group names of the a given module, group
	 * names will be returned from the schema definition
	 * 
	 * @param moduleName
	 *@return Vector containing groupnames
	 */
	public Vector getGroupNames(String moduleName) {
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
		ArrayList<SchemaGroup> groupList = module.getGroups();

		if (groupList != null) {
			Vector groupNames = new Vector();
			for (SchemaGroup s : groupList) {
				groupNames.add(s.getGroupName());
			}
			return groupNames;
		}

		return null;
	}

	public ArrayList getGroupEntryNames(String moduleName, String groupName) {
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

		ArrayList<SchemaGroup> groups = module.getGroups();

		if (groups == null)
			return null;

		for (SchemaGroup s : groups) {
			if (s.getGroupName().equals(groupName)) {
				return ManageGroup.getAllEntryNames(s);
			}
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public ArrayList getEntities(String moduleName) {
		String moduleJson = getModuleJson(moduleName);
		ObjectMapper mapper = new ObjectMapper();
		Module module = null;
		try {
			System.out.println("json " + moduleJson);
			module = mapper.readValue(moduleJson, Module.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<Entity> entityList = module.getEntities();

		if (entityList != null) {
			/*
			 * Vector entityNames = new Vector(); for(Entity e:entityList){
			 * 
			 * entityNames.add(e.getEntityName()); } return entityNames;
			 */
			return entityList;
		}

		return null;
	}

	public Vector<String> getFeatures(String moduleName) {
		// To get the list of feature names for the selected module to show in
		// the UI
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
		ArrayList<Feature> featureList = module.getFeatures();

		if (featureList != null) {
			Vector<String> featureNames = new Vector<String>();
			for (Feature f : featureList) {
				featureNames.add(f.getFeatureName());
			}
			return featureNames;
		}

		return null;
	}

	public Vector<String> getFields(String moduleName, String entityName) {
		// To get the list of fields for the selected module and entity

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
		Entity entity = null;

		if (entityList != null) {
			Vector<String> entityNames = new Vector<String>();
			for (Entity e : entityList) {
				if (e.getEntityName().equals(entityName)) {
					entity = e;
				}
			}
		}
		ArrayList<Field> fieldList = entity.getFields();

		if (fieldList != null) {
			Vector<String> fieldNames = new Vector<String>();
			for (Field f : fieldList) {
				fieldNames.add(f.getFieldName());
			}
			return fieldNames;
		}

		return null;
	}
	
	public Vector<String> getEntityActions(String moduleName, String entityName){
		
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
		Entity entity = null;

		if (entityList != null) {
			Vector<String> entityNames = new Vector<String>();
			for (Entity e : entityList) {
				if (e.getEntityName().equals(entityName)) {
					entity = e;
				}
			}
		}
		
		ArrayList<EntityAction> actionList = entity.getActions();
		if(actionList!=null){
			Vector<String> actionNames = new Vector<String>();
			for(EntityAction ea:actionList){
				actionNames.add(ea.getActionName());
			}
			return actionNames;
		}
		return null;
	}

	public void removeFeature(String moduleName, String featureName) {
		try {

			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();
			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Feature> features = null;
			if (module.getFeatures() == null) {
				features = new ArrayList();
			} else {
				features = module.getFeatures();
			}

			// ArrayList<Feature> featureList = new ArrayList<Feature>();

			for (Feature f : features) {
				// Add features to the new entity
				if (f.getFeatureName().equals(featureName)) {
					features.remove(f);
					ManageGroup.removeEntFeat(module, "f:"+featureName, "ent");
					break;
				}
			}
			module.setFeatures(features);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public void removeEntity(String moduleName, String entityName) {
		try {

			View modulesView = currentdb.getView("Modules");

			// System.out.println("Document name"+modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}
			if (entities.size() == 0) {
				return;
			}
			for (Entity e : entities) {
				if (e.getEntityName().equals(entityName)) {
					entities.remove(e);
					ManageGroup.removeEntFeat(module, "e:"+e.getEntityName(),"ent");
					
					break;
				}
			}

			module.setEntities(entities);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	public void removeField(String moduleName, String entityName,
			String fieldName) {
		try {

			View modulesView = currentdb.getView("Modules");

			// System.out.println("Document name"+modulesView.getDocumentByKey(moduleName));
			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);

			ArrayList<Entity> entities = null;
			if (module.getEntities() == null) {
				entities = new ArrayList();
			} else {
				entities = module.getEntities();
			}
			Entity entity = null;
			for (Entity e : entities) {
				if (entities != null) {
					if (e.getEntityName().equals(entityName)) {
						entity = e;
						break;
					}
				}
			}

			ArrayList<Field> fieldsList = entity.getFields();

			// Add fields to the new entity
			if (fieldsList.size() == 0) {
				return;
			}
			for (Field f : fieldsList) {
				if (f.getFieldName().equals(fieldName)) {
					fieldsList.remove(f);
					break;
				}
			}

			entity.setFields(fieldsList);

			moduleDoc.replaceItemValue("JsonString", mapper
					.writeValueAsString(module));
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

	private Entity getEntityObj(Module module, String entityName) {
		// To get entity object of a given module and entity name, used in
		// adding group entries
		ArrayList<Entity> entities = null;

		entities = module.getEntities();

		Entity entity = null;
		for (Entity e : entities) {
			if (entities != null) {
				if (e.getEntityName().equals(entityName)) {
					return e;

				}
			}
		}
		return null;
	}

	private Feature getFeatureObj(Module module, String featureName) {
		// To get feature object of a given module and entity name, used in
		// adding group entries
		ArrayList<Feature> features = null;
		features = module.getFeatures();
		if (features != null) {
			for (Feature f : features) {
				if (f.getFeatureName().equals(featureName)) {
					return f;
				}
			}

		}
		return null;
	}

	public Vector<String> getEntFeatList(String moduleName) {
		// To get the list of entFeat list
		try {
			View modulesView = currentdb.getView("Modules");

			Document moduleDoc = modulesView.getDocumentByKey(moduleName);

			String jsonInput = moduleDoc.getItemValueString("JsonString");

			ObjectMapper mapper = new ObjectMapper();

			Module module = mapper.readValue(jsonInput, Module.class);
			
			return ManageGroup.getEntFeatList(module);
		} catch (NotesException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeModule(String moduleName) {		
			try {
				View modulesView = currentdb.getView("Modules");
				Document moduleDoc = modulesView.getDocumentByKey(moduleName);
				moduleDoc.remove(true);
			} catch (NotesException e) {
				e.printStackTrace();
			}			
	}
}
