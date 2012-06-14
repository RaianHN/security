package bsuite.weber.configure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import lotus.domino.cso.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

import bsuite.weber.model.BsuiteWorkFlow;

public class DefineModule extends BsuiteWorkFlow {
	public void addModules(Vector<String> modules) {
		for (String moduleName : modules) {
			String moduleJson = createJsonString(moduleName);
			createModuleDocument(moduleJson, moduleName);
		}

	}

	/**
	 * @param moduleJson
	 *            : Json string created for this module This method creates a
	 *            document in the current database
	 */
	private void createModuleDocument(String moduleJson, String moduleName) {

		try {
			Document doc = currentdb.createDocument();
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

	public void addEntity(String moduleName, String entityName,
			Vector<String> fields, Vector<String> features) {

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
			ArrayList<Feature> featureList = new ArrayList<Feature>();

			for (String s : fields) {
				// Add fields to the new entity
				Field field = new Field();
				field.setFieldName(s);
				fieldsList.add(field);
			}
			for (String s : features) {
				// Add features to the new entity
				Feature feature = new Feature();
				feature.setFeatureName(s);
				featureList.add(feature);
			}
			entity.setEntityName(entityName);
			entity.setFields(fieldsList);
			entity.setFeatures(featureList);
			entities.add(entity);
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

	public Vector getModules() {
		System.out.println("inside get modules");
		View moduleView= null;
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
			while(doc!=null){				
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
		String roleJson="";
		ObjectMapper mapper = new ObjectMapper();
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		System.out.println("add r1");
		if(roleJson.equals("")){
			RoleHierarchy rH = new RoleHierarchy();
			ArrayList<Role> roleList = new ArrayList<Role>();
			Role role = new Role();
			role.setRoleName(roleName);
			role.setRoleParent(roleParent);
			roleList.add(role);
			rH.setRoleList(roleList);
			
			
			
			try {
				roleDoc.replaceItemValue("JsonString",mapper
						.writeValueAsString(rH) );
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (NotesException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("add r2");
			try {
				RoleHierarchy rH = mapper.readValue(roleJson, RoleHierarchy.class);
				ArrayList<Role> roleList = rH.getRoleList();
				Role role = new Role();
				role.setRoleName(roleName);
				role.setRoleParent(roleParent);
				roleList.add(role);
				rH.setRoleList(roleList);
				
				try {
					roleDoc.replaceItemValue("JsonString",mapper.writeValueAsString(rH));
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

	public Vector getRoles(){
		Document roleDoc = getRoleDocument();
		String roleJson="";
		ObjectMapper mapper = new ObjectMapper();
		RoleHierarchy rH=null;
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		try {
			if(!roleJson.equals("")){
				rH = mapper.readValue(roleJson, RoleHierarchy.class);
			}else{
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
		
		if(roleList!=null){
			Vector roleNames = new Vector();
			for(Role r:roleList){
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
			if(roleDoc==null){
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
	
	public ArrayList<Role> getRoleList(){
		Document roleDoc = getRoleDocument();
		String roleJson="";
		ObjectMapper mapper = new ObjectMapper();
		RoleHierarchy rH=null;
		try {
			roleJson = roleDoc.getItemValueString("JsonString");
		} catch (NotesException e1) {
			e1.printStackTrace();
		}
		
		try {
			if(!roleJson.equals("")){
				rH = mapper.readValue(roleJson, RoleHierarchy.class);
			}else{
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
	/*private DocumentCollection getModuleDocs(){
		
		try {
			View moduleView = currentdb.getView("Modules");
			DocumentCollection moduleDocs  = 
			
			
			return moduleDocs;
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}*/
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
	public ArrayList getEntities(String moduleName){
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
			/*Vector entityNames = new Vector();
			for(Entity e:entityList){
			
				entityNames.add(e.getEntityName());
			}
			return entityNames;*/
			return entityList;
		}


		return null;
	}

}
