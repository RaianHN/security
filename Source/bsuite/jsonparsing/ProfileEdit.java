package bsuite.jsonparsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.swt.widgets.Group;

import bsuite.configure.DefineModule;
import bsuite.configure.Deploy;
import bsuite.jsonparsing.Entity;
import bsuite.jsonparsing.Feature;
import bsuite.jsonparsing.Field;
import bsuite.jsonparsing.Module;
import bsuite.jsonparsing.ProfileJson;
import bsuite.security.Profile;
import bsuite.utility.JSFUtil;
import bsuite.utility.SessionContext;
import bsuite.utility.Utility;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

public class ProfileEdit {

	public ProfileEdit() {
	}
	public void Getmetho(){
		//
	}
	public void getModulePermission(String profileName) throws NotesException {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		setModuleViewScope(getJsonProfileObj(jsonString));
		profDoc.recycle();
	}
	
	public String getModulePermission(String profileName, String moduleName) throws NotesException {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		
		profDoc.recycle();
		return setModuleViewScope(getJsonProfileObj(jsonString), moduleName);
	}

	private String setModuleViewScope(ProfileJson profile,
			String moduleName) {
	
		Module module = null;
		String result = "";
		if(profile.getModules()==null){
			return "";
		}
		for (Module mod : profile.getModules()) {
			if(mod!=null){
				if(mod.getModuleName().equals(moduleName)){
					module = mod;
					break;
				}
			}
		}
		result = module.getModuleName()+":"+module.getTabvis();
		//Map viewScope = SessionContext.getViewScope();
		
		//viewScope.put("moduleP", result);
		
		return result;
	}
	public Vector<String> getEntityPermission(String profileName,
			String moduleName) {

		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setEntityViewScopeCrud(getJsonProfileObj(jsonString), moduleName);

	}
	public Vector<String> getEntityActionPermission(String profileName,
			String moduleName,String entityName){
		//This method is used to get the entity permission and its action permission, used in new security ui to get a particular entities permission
		//along with its action permission
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setEntityActionPerm(getJsonProfileObj(jsonString), moduleName, entityName);
		
	}

	public Vector<String> getFieldPermission(String profileName,
			String moduleName, String entityName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setFieldViewScopeCrud(getJsonProfileObj(jsonString), moduleName,
				entityName);

	}
	public Vector<String> getGroupEntryPermission(String profileName,String moduleName, String groupName){
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setGroupEntryCrud(getJsonProfileObj(jsonString),moduleName,groupName);
		
	}

	private Vector<String> setGroupEntryCrud(ProfileJson profile,
			String moduleName, String groupName) {
		
		Vector<String> groupSecurity = new Vector<String>();
		Module module = null;
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}

		GroupPermission group=null;
		if(module.getGroups()!=null){
			for(GroupPermission gp : module.getGroups()){
				if(gp.getName().equals(groupName)){
					group = gp;
					break;
				}
			}
		}
		Vector<String> groupEntryPerm = new Vector<String>();
		
		if(group!=null){
			if(group.getEntries()==null)return groupEntryPerm;
			
			for(GroupEntry ge:group.getEntries()){
				if(ge!=null){
					groupEntryPerm.add(ge.getName()+":"+ge.getVisible());

				}
			}
		}
		

		Map viewScope = SessionContext.getViewScope();
		viewScope.put("groupEntryPerm", groupEntryPerm);
		return groupEntryPerm;
		
		
		
	}
	public void getFeaturePermission(String profileName, String moduleName,
			String entityName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		@SuppressWarnings("unused")
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}


	}

	public Vector<String> getFeaturePermission(String profileName,
			String moduleName) {
	
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setFeatureViewScopeCrud(getJsonProfileObj(jsonString),
				moduleName);

	}

	public Document getProfileDoc(Database db, String profileName) {

		View profileView = null;
		Document profDoc = null;
		try {
			profileView = db.getView("ProfileView");
			profDoc = profileView.getDocumentByKey(profileName);

		} catch (NotesException e) {
			e.printStackTrace();
		}
		return profDoc;
	}

	public Database getSecurityDatabase() {
		Database securitydb = null;

		securitydb = Utility.getDatabase("Security.nsf");

		return securitydb;
	}



	@SuppressWarnings("unchecked")
	private void setModuleViewScope(ProfileJson profile) {
		Vector<String> moduleSecurity = new Vector<String>();

		for (Module mod : profile.getModules()) {
			moduleSecurity.add(mod.getModuleName() + ":" + mod.getTabvis());
		}
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("modulePermission", moduleSecurity);
		viewScope.put("numberOfModules", moduleSecurity.size());
	}

	public ProfileJson getJsonProfileObj(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		ProfileJson profile = null;
		try {
			profile = mapper.readValue(jsonString, ProfileJson.class);
		} catch (JsonParseException e) {

			e.printStackTrace();
		} catch (JsonMappingException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return profile;
	}

	public void saveModulePerm(String profileName, String vals) {

		String[] arr = vals.split(",");

		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);


		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);

		String[] moduleSecurity;

		String mSec = "";
		for (int i = 0; i < arr.length; i++) {

			mSec = arr[i];
			moduleSecurity = mSec.split(":");
			for (Module mod : profile.getModules()) {

				if (moduleSecurity[0].equals(mod.getModuleName())) {

					mod.setTabvis(moduleSecurity[1]);
					break;
				}
			}
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}

	public void saveFeaturePerm(String profileName, String vals) {
		String[] arr = vals.split(",");
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);

		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);

		String[] moduleSecurity;

		String mSec = "";
		for (int i = 0; i < arr.length; i++) {

			mSec = arr[i];
			moduleSecurity = mSec.split(":");

			for (Module mod : profile.getModules()) {

				if (moduleSecurity[0].equals(mod.getModuleName())) {

					for (Feature feature : mod.getFeatures()) {

						if (moduleSecurity[1].equals(feature.getFeatureName())) {

							feature.setVisible(moduleSecurity[2]);
							break;
						}
					}
				}
			}

			ObjectMapper mapper = new ObjectMapper();
			String jsonString2 = "";
			try {
				jsonString2 = mapper.writeValueAsString(profile);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				profDoc.replaceItemValue("JsonString", jsonString2);
				profDoc.save();
			} catch (NotesException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> setEntityViewScopeCrud(ProfileJson profile,
			String moduleName) {
		Vector<String> entitySecurity = new Vector<String>();
		Module module = null;

		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		if (module.getEntities() == null) {
			return null;
		}
		for (Entity e : module.getEntities()) {
			entitySecurity.add(e.getEntityName() + ":" + e.getCreate()
					+ e.getRead() + e.getUpdate() + e.getDelete()
					+ e.getAccessType());
		}


		Map viewScope = SessionContext.getViewScope();
		viewScope.put("entityPerm", entitySecurity);
		return entitySecurity;

	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> setEntityActionPerm(ProfileJson profile,
			String moduleName, String entityName) {
		Vector<String> entitySecurity = new Vector<String>();
		Module module = null;
		Map viewScope = SessionContext.getViewScope();
		
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		if (module.getEntities() == null) {
			return null;
		}
		
		for (Entity e : module.getEntities()) {
			if(e.getEntityName().equals(entityName)){
				//entitySecurity.add(e.getEntityName() + ":" + e.getCreate()
				//		+ e.getRead() + e.getUpdate() + e.getDelete()
				//		+ e.getAccessType());
				viewScope.put("entityPermission",e.getEntityName() + ":" + e.getCreate()
								+ e.getRead() + e.getUpdate() + e.getDelete()
								+ e.getAccessType());
				if(e.getActions()!=null){
					viewScope.put("entityAN", Integer.toString(e.getActions().size()));
					for(EntityAction ea:e.getActions()){
						if(ea!=null){
							entitySecurity.add(ea.getActionName()+ ":" +ea.getVisible());
						}
						
					}
				}
				
				break;
			}
			
		}

		

		
		viewScope.put("entityActionPerm", entitySecurity);
		
		
		return entitySecurity;

	}
	
	@SuppressWarnings("unchecked")
	public Vector<String> setFieldViewScopeCrud(ProfileJson profile,
			String moduleName, String entityName) {
		Vector<String> fieldSecurity = new Vector<String>();
		Module module = null;
		String fieldN=null;
		// System.out.println("inside entity perm " + profile.getProfName() +
		// " "
		// + moduleName);
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		Entity entity = null;
		for (Entity e : module.getEntities()) {
			if (e.getEntityName().equals(entityName)) {
				entity = e;
				break;
			}
		}

		for (Field f : entity.getFields()) {
			fieldSecurity.add(f.getFieldName() + ":" + f.getVisible()
					+ f.getReadonly());
		}
		// System.out.println("vect entity perm" + fieldSecurity);
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("fieldPerm", fieldSecurity);
		viewScope.put("fieldN", Integer.toString(entity.getFields().size()));
		return fieldSecurity;

	}

	@SuppressWarnings("unchecked")
	public Vector<String> setFeatureViewScopeCrud(ProfileJson profile,
			String moduleName) {
		Vector<String> featureSecurity = new Vector<String>();
		Module module = null;
		System.out.println("inside entity perm " + profile.getProfName() + " "
				+ moduleName);
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		System.out.println("inside entity perm2 " + profile.getProfName() + " "
				+ moduleName);
		if (module.getFeatures() != null) {
			for (Feature f : module.getFeatures()) {
				System.out.println("inside entity perm3 "
						+ profile.getProfName() + " " + moduleName);
				if (f != null) {
					featureSecurity.add(f.getFeatureName() + ":"
							+ f.getVisible());
				}

			}
		}

		System.out.println("vect entity perm" + featureSecurity);
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("featurePerm", featureSecurity);
		System.out.println("features" + featureSecurity);
		return featureSecurity;

	}

	public void saveEntityPerm(String profileName, String vals) {
		// System.out.println("inside save" + vals);
		// //System.out.println("entity name" + moduleName);
		String[] arr = vals.split(",");
		String[] arrs = null;
		// System.out.println("inside save 1--" + arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		// System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		// System.out.println("inside save 4");
		String moduleName = "";
		String entityName = "";
		String entityPerm = "";

		for (int x = 0; x < arr.length; x++) {
			arrs = arr[x].split(":");

			moduleName = arrs[0];
			entityName = arrs[1];
			entityPerm = arrs[2];

			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			// System.out.println("inside save 5");
			String[] entitySecurity;
			String eSec = "";

			for (Entity entity : module.getEntities()) {
				// System.out.println("inside save 53");
				// System.out.println("entity name" + entityName
				// + " getfname" + entity.getEntityName());

				if (entityName.equals(entity.getEntityName())) {
					// System.out
					// .println("inside save 54" + entityPerm);
					entity.setCreate(Character.toString(entityPerm.charAt(0)));
					entity.setRead(Character.toString(entityPerm.charAt(1)));
					entity.setUpdate(Character.toString(entityPerm.charAt(2)));
					entity.setDelete(Character.toString(entityPerm.charAt(3)));
					break;
				}
			}
		}
		// System.out.println("inside save 6");

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 8");

	}
	
	public void saveEntityActionPerm(String profileName, String vals){
		String[] arr = vals.split(",");
		String[] arrs = null;
		String[] arrV = null;
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		Entity ent = null;
		String moduleName = "";
		String entityName = "";
		String entityPerm = "";
		String actionName = "";
		String actionVisib = "";

		arrV = arr[0].split(":");
		moduleName = arrV[0];
		entityName = arrV[1];
		entityPerm = arrV[2];
		
		
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		
		
		for (Entity entity : module.getEntities()) {
	
			if (entityName.equals(entity.getEntityName())) {
				ent = entity;
				break;
			}
		}
		
		
		for (int x = 0; x < arr.length; x++) {
			arrs = arr[x].split(":");

			
			actionName = arrs[3];
			actionVisib = arrs[4];

			
		
			String[] entitySecurity;
			String eSec = "";

			if(ent!=null){
				if(ent.getActions()!=null){
					for(EntityAction ea:ent.getActions()){
						if(ea!=null){
							if(ea.getActionName().equals(actionName)){
								ea.setVisible(actionVisib);
								break;
							}
						}
					}
				}
			}
				
			
		}
		ent.setRead(entityPerm);
		
		

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *This method is called to save the profile and its permissions for the
	 * selected profile
	 * 
	 * 
	 *@param profileName
	 *@param vals
	 */
	public void saveGroupPerm(String profileName, String vals) {

		System.out.print("1");
		String[] arr = vals.split(",");
		String[] arrs = null;
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		System.out.print("2");
		Module module = null;
		String moduleName = "";
		String groupName = "";
		String groupPerm = "";

		System.out.print("3");
		
		for (int x = 0; x < arr.length; x++) {
			System.out.print("4");
			arrs = arr[x].split(":");
			
			moduleName = arrs[0];
			groupName = arrs[1];
			groupPerm = arrs[2];
			System.out.print("4");
			
			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			String[] groupSecurity;
			String gSec = "";
			
			System.out.print("5");
			for (GroupPermission group : module.getGroups()) {
				if (groupName.equals(group.getName())) {
					group.setVisible(Character.toString(groupPerm.charAt(0)));
					break;
				}
			}

		}
		System.out.print("6");
		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			e.printStackTrace();
		}

	}
	

	public void saveFieldPerm(String profileName, String moduleName,
			String entityName, String vals) {
		// System.out.println("inside save" + vals);
		// System.out.println("entity name" + moduleName);
		String[] arr = vals.split(",");
		// System.out.println("inside save 1--" + arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		// System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		// System.out.println("inside save 4");
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		Entity entity = null;
		for (Entity e : module.getEntities()) {
			if (e.getEntityName().equals(entityName)) {
				entity = e;
				break;
			}

		}

		// System.out.println("inside save 5");
		String[] fieldSecurity;
		String fSec = "";
		for (int i = 1; i < arr.length; i++) {
			fSec = arr[i];
			// System.out.println("inside save 51");
			// System.out.println(fSec);
			fieldSecurity = fSec.split(":");
			// System.out.println("after split" + fieldSecurity[0]);
			// System.out.println("inside save 52");
			for (Field field : entity.getFields()) {
				// System.out.println("inside save 53");
				// System.out.println("field name" + fieldSecurity[0]
				// + " getfname" + field.getFieldName());

				if (fieldSecurity[0].equals(field.getFieldName())) {
					// System.out.println("inside save 54" + fieldSecurity[1]);
					field.setVisible(Character.toString(fieldSecurity[1]
							.charAt(0)));
					field.setReadonly(Character.toString(fieldSecurity[1]
							.charAt(1)));
					break;
				}
			}
		}
		// System.out.println("inside save 6");

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 8");

	}

	
	
	public void saveFieldPerm(String profileName, String vals) {
		// System.out.println("inside field permission" + vals);
		String[] arr = vals.split(",");
		String[] arrs = null;
		// System.out.println("inside save 1--" + arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		// System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		// System.out.println("inside save 4");

		// System.out.println("inside save 5");
		String[] fieldSecurity;
		String fSec = "";
		String moduleName = "";
		String entityName = "";
		String fieldName = "";
		String fieldSec = "";
		for (int i =0; i < arr.length; i++) {
			arrs = arr[i].split(":");
			moduleName = arrs[0];
			entityName = arrs[1];

			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			Entity entity = null;
			for (Entity e : module.getEntities()) {
				if (e.getEntityName().equals(entityName)) {
					entity = e;
					break;
				}

			}

			fieldName = arrs[2];
			fieldSec = arrs[3];

			for (Field field : entity.getFields()) {

				if (fieldName.equals(field.getFieldName())) {

					field.setVisible(Character.toString(fieldSec.charAt(0)));
					field.setReadonly(Character.toString(fieldSec.charAt(1)));
					break;
				}
			}
		}
		// System.out.println("inside save 6");

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 8");

	}

	public void saveGroupActionPerm(String profileName, String vals) {
		String[] arr = vals.split(",");
		String[] arrs = null;
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;

		String[] groupActionSecurity;
		String gSec = "";
		String moduleName = "";
		String groupName = "";
		String groupActionName = "";
		String actionSec = "";
		
		Feature feat = null;
		ArrayList<Feature> features = null;
		for (int i = 0; i < arr.length; i++) {
			arrs = arr[i].split(":");
			moduleName = arrs[0];
			groupName = arrs[1];

			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			GroupPermission group = null;
			features = module.getFeatures();
			for (GroupPermission g : module.getGroups()) {
				if (g.getName().equals(groupName)) {
					group = g;
					break;
				}

			}

			groupActionName = arrs[2];
			actionSec = arrs[3];

			for (GroupEntry ge : group.getEntries()) {

				if (groupActionName.equals(ge.getName())) {

					ge.setVisible(Character.toString(actionSec.charAt(0)));
					if(ge.getType().equals("f")){
						feat = saveGroupFeature(features,ge.getName());
						feat.setVisible(Character.toString(actionSec.charAt(0)));
					}
					
				}
			}
		}
		// System.out.println("inside save 6");

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 8");

	}
	private Feature saveGroupFeature(ArrayList<Feature> features, String featureName){
		
		if(features==null ||featureName==null){
			 return null;
		}
		for(Feature feature:features){
			if(feature!=null){
				if(feature.getFeatureName().equals(featureName)){
					return feature;
				}
			}
		}
		return null;
	}
	/*
	 * public void saveFeaturePerm(String profileName, String moduleName,String
	 * entityName, String vals){ //System.out.println("inside save"+vals);
	 * //System.out.println("entity name"+moduleName); String[] arr =
	 * vals.split(","); //System.out.println("inside save 1--"+arr[1]); Document
	 * profDoc = getProfileDoc(getSecurityDatabase(),profileName); String
	 * jsonString=""; //System.out.println("inside save 2"); try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * //System.out.println("inside save 3"); ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null;
	 * //System.out.println("inside save 4"); for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(moduleName)){
	 * module = mod; break; }
	 * 
	 * } Entity entity = null; for(Entity e:module.getEntities()){
	 * if(e.getEntityName().equals(entityName)){ entity = e; break; }
	 * 
	 * }
	 * 
	 * //System.out.println("inside save 5"); String[] featureSecurity; String
	 * fSec=""; for(int i=1;i<arr.length;i++){ fSec = arr[i];
	 * //System.out.println("inside save 51"); //System.out.println(fSec);
	 * featureSecurity = fSec.split(":");
	 * //System.out.println("after split"+featureSecurity[0]);
	 * //System.out.println("inside save 52"); for(Feature
	 * feature:entity.getFeatures()){ //System.out.println("inside save 53");
	 * //System.out.println("field name"+featureSecurity[0]+" getfname"+feature.
	 * getFeatureName());
	 * 
	 * if(featureSecurity[0].equals(feature.getFeatureName())){
	 * //System.out.println("inside save 54"+featureSecurity[1]);
	 * feature.setVisible(Character.toString(featureSecurity[1].charAt(0)));
	 * 
	 * } } } //System.out.println("inside save 6");
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } //System.out.println("inside save 7");
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } //System.out.println("inside save 8");
	 * 
	 * 
	 * }
	 * 
	 * private ProfileJson getProfileObj(String profileName){ Document profDoc =
	 * getProfileDoc(getSecurityDatabase(),profileName); String jsonString="";
	 * try { jsonString = profDoc.getItemValueString("JsonString"); } catch
	 * (NotesException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return getJsonProfileObj(jsonString); }
	 * 
	 * 
	 * 
	 * 
	 * public Vector<String> getModuleNames(String profileName){ ProfileJson
	 * profile = getProfileObj(profileName); Vector<String> modules = new
	 * Vector<String>(); for(Module mod:profile.getModules()){
	 * modules.add(mod.getModuleName()); } return modules; }
	 * 
	 * public Vector<String> getEntityNames(String profileName,String
	 * moduleName){ ProfileJson profile = getProfileObj(profileName); Module
	 * module = getModule(profile,moduleName); Vector<String> entities = new
	 * Vector<String>(); for(Entity entity:module.getEntities()){
	 * entities.add(entity.getEntityName()); } return entities; }
	 * 
	 * 
	 * public Vector<String> getFieldNames(String profileName,String
	 * moduleName,String entityName){ ProfileJson profile =
	 * getProfileObj(profileName); Module module =
	 * getModule(profile,moduleName); Entity entity =
	 * getEntity(profile,module,entityName); Vector<String> fields = new
	 * Vector<String>(); for(Field field:entity.getFields()){
	 * fields.add(field.getFieldName()); } return fields; }
	 * 
	 * public Vector<String> getFeatureNames(String profileName,String
	 * moduleName){ ProfileJson profile = getProfileObj(profileName); Module
	 * module = getModule(profile,moduleName);
	 * 
	 * Vector<String> features = new Vector<String>(); for(Feature
	 * feature:module.getFeatures()){ features.add(feature.getFeatureName()); }
	 * return features; }
	 * 
	 * 
	 * private Module getModule(ProfileJson profile,String moduleName){
	 * for(Module module:profile.getModules()){
	 * if(module.getModuleName().equals(moduleName)){ return module; } } return
	 * null; }
	 * 
	 * private Entity getEntity(ProfileJson profile,Module module,String
	 * entityName){ for(Entity entity:module.getEntities()){
	 * if(entity.getEntityName().equals(entityName)){ return entity; } } return
	 * null; } /* public void getEntityCrud(String profileName, String
	 * entityName){ Document profDoc =
	 * getProfileDoc(getSecurityDatabase(),profileName); String jsonString="";
	 * try { jsonString = profDoc.getItemValueString("JsonString"); } catch
	 * (NotesException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * setEntityViewScopeCrud(getJsonProfileObj(jsonString), entityName); }
	 * 
	 * 
	 * 
	 * public void getFeildPermission(String profileName, String entityName){
	 * Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
	 * String jsonString=""; try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * setFieldViewScopeCrud(getJsonProfileObj(jsonString), entityName); }
	 * 
	 * public void getFeaturePermission(String profileName, String entityName){
	 * Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
	 * String jsonString=""; try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * setFeatureViewScopeCrud(getJsonProfileObj(jsonString), entityName);
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * public void setEntityViewScopeCrud(ProfileJson profile,String moduleName,
	 * String entityName){ Vector<String> entitySecurity = new Vector<String>();
	 * 
	 * for(Module mod:profile.getModules()){
	 * if(mod.getModuleName().equals(moduleName)){ entitySecurity = mod; break;
	 * }
	 * 
	 * } for(FieldPerm field:module.getFieldsperm()){
	 * fldSecurity.add(field.getFname
	 * ()+":"+field.getVisible()+field.getReadonly()); }
	 * viewScope.put("fieldPerm", fldSecurity);
	 * 
	 * 
	 * if(module.getTabvis().equals("1")){ viewScope.put("v", true); }else{
	 * viewScope.put("v", false); }
	 * 
	 * if(module.getCreate().equals("1")){ viewScope.put("c", true); }else{
	 * viewScope.put("c", false); }
	 * 
	 * if(module.getRead().equals("1")){ viewScope.put("r", true); }else{
	 * viewScope.put("r", false); }
	 * 
	 * if(module.getUpdate().equals("1")){ viewScope.put("u", true); }else{
	 * viewScope.put("u", false); }
	 * 
	 * if(module.getDelete().equals("1")){ viewScope.put("d", true); }else{
	 * viewScope.put("d", false); }
	 * 
	 * 
	 * }
	 * 
	 * public void setFieldViewScopeCrud(ProfileJson profile, String
	 * entityName){ Vector fldSecurity = new Vector();
	 * 
	 * Module module = null; for(Module mod:profile.getModules()){
	 * if(mod.getModuleName().equals(entityName)){ module = mod; break; }
	 * 
	 * }
	 * 
	 * for(FieldPerm field:module.getFieldsperm()){
	 * fldSecurity.add(field.getFname
	 * ()+":"+field.getVisible()+field.getReadonly()); }
	 * viewScope.put("fieldPerm", fldSecurity);
	 * 
	 * }
	 * 
	 * public void setFeatureViewScopeCrud(ProfileJson profile, String
	 * entityName){ Vector ftrSecurity = new Vector(); Module module = null;
	 * for(Module mod:profile.getModules()){
	 * if(mod.getModuleName().equals(entityName)){ module = mod; break; }
	 * 
	 * }
	 * 
	 * for(FeaturePerm ftrPerm:module.getFeaturesperm()){
	 * ftrSecurity.add(ftrPerm.getFeaturename()+":"+ftrPerm.getVisible());
	 * 
	 * } viewScope.put("featurePerm", ftrSecurity);
	 * 
	 * }
	 * 
	 * public void saveEntityCrud(String profileName, String entityName,String
	 * v, String c, String r, String u, String d ){ Document profDoc =
	 * getProfileDoc(getSecurityDatabase(),profileName); String jsonString="";
	 * try { jsonString = profDoc.getItemValueString("JsonString"); } catch
	 * (NotesException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null; for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(entityName)) {
	 * module = mod; break; }
	 * 
	 * } module.setTabvis(v); module.setCreate(c); module.setRead(r);
	 * module.setUpdate(u); module.setDelete(d);
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); }
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); }
	 * 
	 * } public void saveFieldCrud(String profileName, String entityName, String
	 * vals){ //System.out.println("inside save"+vals);
	 * //System.out.println("entity name"+entityName); String[] arr =
	 * vals.split(","); //System.out.println("inside save 1--"+arr[1]); Document
	 * profDoc = getProfileDoc(getSecurityDatabase(),profileName); String
	 * jsonString=""; //System.out.println("inside save 2"); try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * //System.out.println("inside save 3"); ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null;
	 * //System.out.println("inside save 4"); for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(entityName)){
	 * module = mod; break; }
	 * 
	 * } //System.out.println("inside save 5"); String[] fldSecurity; String
	 * fSec=""; for(int i=1;i<arr.length;i++){ fSec = arr[i];
	 * //System.out.println("inside save 51"); //System.out.println(fSec);
	 * fldSecurity = fSec.split(":");
	 * //System.out.println("after split"+fldSecurity[0]);
	 * //System.out.println("inside save 52"); for(FieldPerm
	 * fldPrm:module.getFieldsperm()){ //System.out.println("inside save 53");
	 * System
	 * .out.println("field name"+fldSecurity[1]+" getfname"+fldPrm.getFname());
	 * 
	 * if(fldSecurity[1].equals(fldPrm.getFname())){
	 * //System.out.println("inside save 54"+fldSecurity[2]);
	 * fldPrm.setVisible(Character.toString(fldSecurity[2].charAt(0)));
	 * //System.out.println("inside save 541 visble"+fldSecurity[2].charAt(0));
	 * fldPrm.setReadonly(Character.toString(fldSecurity[2].charAt(1)));
	 * //System.out.println("inside save 541 ronly"+fldSecurity[2].charAt(1));
	 * //System.out.println("inside save 55"); } } }
	 * //System.out.println("inside save 6");
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } //System.out.println("inside save 7");
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } //System.out.println("inside save 8");
	 * 
	 * 
	 * } public void saveFeatureCrud(String profileName, String entityName,
	 * String vals){
	 * 
	 * String[] arr = vals.split(","); Document profDoc =
	 * getProfileDoc(getSecurityDatabase(),profileName); String jsonString="";
	 * try { jsonString = profDoc.getItemValueString("JsonString"); } catch
	 * (NotesException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null; for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(entityName)){
	 * module = mod; break; }
	 * 
	 * } String[] ftrSecurity;
	 * 
	 * 
	 * String fSec=""; for(int i=1;i<arr.length;i++){ fSec = arr[i]; ftrSecurity
	 * = fSec.split(":"); //System.out.println("ftrsecurity "+ftrSecurity);
	 * for(FeaturePerm ftrPrm:module.getFeaturesperm()){
	 * //System.out.println("comparing "+ftrSecurity[1]+ " "
	 * +ftrPrm.getFeaturename());
	 * if(ftrSecurity[1].equals(ftrPrm.getFeaturename())){
	 * //System.out.println("ftr permission"+ftrSecurity[2]+"  ");
	 * ftrPrm.setVisible(ftrSecurity[2]); } } }
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); }
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } }
	 */

	public int getNumberOfMOdules(String profileName) {
		// System.out.println("inside getNumber of modules" + profileName);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside getNumber of modules2" + profileName);
		return getModuleNumber(getJsonProfileObj(jsonString));

	}

	public int getNumberOfFeatures(String profileName, String moduleName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return getFeatureNumber(getJsonProfileObj(jsonString), moduleName);

	}

	private int getFeatureNumber(ProfileJson profile, String moduleName) {
		Vector<String> featureSecurity = new Vector<String>();
		Module module = null;
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}

		ArrayList<Feature> features = module.getFeatures();
		if (features != null) {
			return features.size();
		}
		return 0;

	}

	public int getNumberOfEntities(String profileName, String moduleName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getEntityNumber(getJsonProfileObj(jsonString), moduleName);

	}

	private int getEntityNumber(ProfileJson profile, String moduleName) {
		Vector<String> entitySecurity = new Vector<String>();
		Module module = null;

		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		ArrayList<Entity> entities = module.getEntities();
		if (entities != null) {
			return entities.size();
		}

		return 0;
	}
	
	public String getNumberOfGroups(String profileName, String moduleName){
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getGroupNumber(getJsonProfileObj(jsonString), moduleName);

	}

	private String getGroupNumber(ProfileJson profile, String moduleName) {
		Module module = null;

		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		ArrayList<GroupPermission> groups = module.getGroups();
		if (groups != null) {
			
			return Integer.toString(groups.size());
			
		}

		return "0";
		
	}
	
	private ArrayList<String> getGroupNames(Module module){
		ArrayList<String> groupNames = new ArrayList<String>();
		
		if(module==null){
			return null;
		}
		ArrayList<GroupPermission> groups = module.getGroups();
		if (groups != null) {
			for(GroupPermission gp:groups){
				if(gp!=null){
					groupNames.add(gp.getName());
				}
			}
			
			
		}
		return groupNames;
	}
	public int getNumberOfFields(String profileName, String moduleName,
			String entityName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getFieldNumber(getJsonProfileObj(jsonString), moduleName,
				entityName);

	}
	
	public int getNumberOfActions(String profileName, String moduleName,
			String groupName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getActionNumber(getJsonProfileObj(jsonString), moduleName,
				groupName);

	}

	private int getActionNumber(ProfileJson profile, String moduleName,
			String groupName) {
		Vector<String> actionSecurity = new Vector<String>();
		Module module = null;

		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		GroupPermission group = null;
		for (GroupPermission g : module.getGroups()) {
			if (g.getName().equals(groupName)) {
				group = g;
				break;
			}
		}
		ArrayList<GroupEntry> actions = group.getEntries();

		if (actions!= null) {
			return actions.size();
		}
		
		
		
		
		return 0;
	}
	private int getFieldNumber(ProfileJson profile, String moduleName,
			String entityName) {
		Vector<String> fieldSecurity = new Vector<String>();
		Module module = null;

		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		Entity entity = null;
		for (Entity e : module.getEntities()) {
			if (e.getEntityName().equals(entityName)) {
				entity = e;
				break;
			}
		}
		ArrayList<Field> fields = entity.getFields();

		if (fields != null) {
			return fields.size();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	private int getModuleNumber(ProfileJson profile) {
		// System.out.println("getNumber of modules");
		ArrayList<Module> modules = profile.getModules();
		if (modules != null) {
			return modules.size();
		}
		// System.out.println("getNumber of modules2");

		return 0;
	}

	public Vector<String> getModuleNames(String profileName) {
		// System.out.println("1");
		ProfileJson profile = getProfileObj(profileName);
		// System.out.println("2");
		if (profile == null) {
			return null;
		}
		Vector<String> modules = new Vector<String>();
		for (Module mod : profile.getModules()) {
			// System.out.println("3");
			// System.out.println(mod.getModuleName());
			modules.add(mod.getModuleName());
		}
		return modules;
	}

	private ProfileJson getProfileObj(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		if (profDoc == null) {
			return null;
		}
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getJsonProfileObj(jsonString);
	}

	public Vector<String> getEntityNames(String profileName, String moduleName) {
		ProfileJson profile = getProfileObj(profileName);
		Module module = getModule(profile, moduleName);
		Vector<String> entities = new Vector<String>();
		for (Entity entity : module.getEntities()) {
			entities.add(entity.getEntityName());
		}
		// System.out.println("Entities: "+entities);
		return entities;
	}

	public Vector<String> getEntityNames(ProfileJson profile, String moduleName) {
		Module module = getModule(profile, moduleName);
		Vector<String> entities = new Vector<String>();
		for (Entity entity : module.getEntities()) {
			entities.add(entity.getEntityName());
		}
		// System.out.println("Entities: "+entities);
		return entities;
	}

	private Module getModule(ProfileJson profile, String moduleName) {
		for (Module module : profile.getModules()) {
			if (module.getModuleName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}

	public void saveAccessTypePerm(String profileName, String vals) {
		// System.out.println("inside save" + vals);
		// //System.out.println("entity name" + moduleName);
		String[] arr = null;
		String[] arrs = null;
		if (vals.contains(",")) {
			// System.out.println("with comma");
			arr = vals.split(",");
		} else {
			// System.out.println("withiout comma");
			// System.out.println("inside save" + vals);
			arr = new String[1];
			arr[0] = vals;
			// System.out.println("inside save arr[0]" + arr[0]);
		}

		// System.out.println("inside save 1--" + arr[0]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		// System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		// System.out.println("inside save 4");
		String moduleName = "";
		String entityName = "";
		String accessPerm = "";

		for (int x = 0; x < arr.length; x++) {
			arrs = arr[x].split(":");

			moduleName = arrs[0];
			entityName = arrs[1];
			accessPerm = arrs[2];

			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			// System.out.println("inside save 5");
			String[] entitySecurity;
			String eSec = "";

			for (Entity entity : module.getEntities()) {
				// System.out.println("inside save 53");
				// System.out.println("entity name" + entityName
				// + " getfname" + entity.getEntityName());

				if (entityName.equals(entity.getEntityName())) {
					// System.out.println("inside save 54" + accessPerm);

					entity.setAccessType(accessPerm);
					break;
				}
			}
		}
		// System.out.println("inside save 6");

		ObjectMapper mapper = new ObjectMapper();
		String jsonString2 = "";
		try {
			jsonString2 = mapper.writeValueAsString(profile);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println("inside save 8");

	}

	// moduleN Modules:3
	// FeatureN Documents:2, In out:2, Employees:2
	// EntitiesN Document:1, In out:0, Employees:2

	public String getModuleN(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Module:" + getModuleNumber(getJsonProfileObj(jsonString));

	}

	public String getFeatureN(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		String result = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);
		System.out.println("in getfeaturen ");
		ArrayList<Module> modules = profile.getModules();
		System.out.println("in getfeaturen " + modules);
		for (Module module : modules) {
			if (modules != null) {
				if (result.equals("")) {
					if (module.getFeatures() == null) {
						result = module.getModuleName() + ":" + 0;
					} else {
						result = module.getModuleName() + ":"
								+ module.getFeatures().size();
					}

				} else {
					if (module.getFeatures() == null) {
						result = result + "," + module.getModuleName() + ":"
								+ 0;
					} else {
						result = result + "," + module.getModuleName() + ":"
								+ module.getFeatures().size();
					}

					// result =
					// result+","+module.getModuleName()+":"+module.getFeatures().size();
				}

			}
		}

		return result;
	}

	public String getEntityN(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		String result = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);

		ArrayList<Module> modules = profile.getModules();
		for (Module module : modules) {
			if (modules != null) {
				if (result.equals("")) {
					if (module.getEntities() != null) {
						result = module.getModuleName() + ":"
								+ module.getEntities().size();
					} else {
						result = module.getModuleName() + ":" + 0;

					}

				} else {
					if (module.getEntities() != null) {
						result = result + "," + module.getModuleName() + ":"
								+ module.getEntities().size();
					} else {
						result = result + "," + module.getModuleName() + ":"
								+ 0;
					}
				}
			}
		}

		return result;

	}

	// FieldsN Documents+Document:2,In
	// Out+"":0,Employees+Employee:3,Employees+Activity:2
	public String getFieldN(String profileName) {

		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		String result = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);
		ArrayList<Module> modules = profile.getModules();
		for (Module module : modules) {
			if (module.getEntities() != null) {
				for (Entity entity : module.getEntities()) {
					if (result.equals("")) {
						result = module.getModuleName() + "+"
								+ entity.getEntityName() + ":"
								+ entity.getFields().size();
					} else {
						result = result + "," + module.getModuleName() + "+"
								+ entity.getEntityName() + ":"
								+ entity.getFields().size();
					}
				}
			}

		}
		return result;

	}

	public String getActionN(String profileName,String moduleName){
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		String result = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		ArrayList<Module> modules = profile.getModules();
		
		Module module = getModule(profile, moduleName);
		
		if(module.getGroups()!=null){
			for(GroupPermission gp :module.getGroups()){
				if(gp!=null){
					if(gp.getEntries()!=null){
						if(result.equals("")){
							result = module.getModuleName()+"+"+gp.getName()+":"+gp.getEntries().size();
						}else{
							result = result+","+module.getModuleName()+"+"+gp.getName()+":"+gp.getEntries().size();
						}	
					}
					
				}
				
			}
		}
		
		return result;
	}
	
	public String getFieldN(String profileName, String moduleName, String entityName){
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		String result = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		ArrayList<Module> modules = profile.getModules();
		
		Module module = getModule(profile, moduleName);
		
		if (module.getEntities() == null) {
			return null;
		}
		
		for (Entity e : module.getEntities()) {
			if(e!=null){
				if(e.getEntityName().equals(entityName)){
					if(e.getFields()!=null){
						Integer.toString(e.getFields().size());
					}					
					break;
				}
			}
			
			
		}
		return "0";
	}
	
	public Vector<String> getFeatureNames(String profileName, String moduleName) {
		// returns the list of features from admin profile, this will be used
		// when updating schema
		ProfileJson profile = getProfileObj(profileName);
		Module module = getModule(profile, moduleName);
		Vector<String> features = new Vector<String>();
		for (Feature feature : module.getFeatures()) {
			features.add(feature.getFeatureName());
		}
		// System.out.println("Features: "+features);
		return features;
	}

	public Vector<String> getFieldNames(String profileName, String moduleName,
			String entityName) {
		// returns the list of fields in the amdin profile, this will be used
		// when updating schema
		ProfileJson profile = getProfileObj(profileName);
		Module module = getModule(profile, moduleName);
		Vector<String> fields = new Vector<String>();

		if (module.getEntities() == null) {
			// System.out.println("Entities is null");
			return fields;
		}
		Entity ent = null;
		for (Entity entity : module.getEntities()) {
			if (entity.getEntityName().equals(entityName))
				ent = entity;
		}
		if (ent.getFields() == null) {
			return fields;
		}
		for (Field f : ent.getFields()) {
			fields.add(f.getFieldName());
		}
		// System.out.println("Fields: "+fields);
		return fields;
	}

	public ProfileJson removeFeature(ProfileJson profile, String moduleName,
			String featureName) {
		Module module = getModule(profile, moduleName);
		if (module.getFeatures() == null) {
			return profile;
		}
		for (Feature f : module.getFeatures()) {
			if (f.getFeatureName().equals(featureName)) {
				module.getFeatures().remove(f);
				// System.out.println("Removed feature: "+f.getFeatureName());
			}
		}

		return profile;
	}

	public ProfileJson removeEntity(ProfileJson profile, String moduleName,
			String entityName) {
		Module module = getModule(profile, moduleName);
		if (module.getEntities() == null) {
			return profile;
		}
		for (Entity e : module.getEntities()) {
			if (e.getEntityName().equals(entityName)) {
				module.getEntities().remove(e);
				// System.out.println("Removed entity: "+e.getEntityName());
			}
		}
		return profile;
	}

	public ProfileJson removeField(ProfileJson profile, String moduleName,
			String entityName, String fieldName) {

		Module module = null;
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		Entity entity = null;
		for (Entity e : module.getEntities()) {
			if (e.getEntityName().equals(entityName)) {
				entity = e;
				break;
			}
		}
		ArrayList<Field> fields = entity.getFields();

		if (fields == null || fields.size() == 0) {
			return profile;
		}
		for (Field f : fields) {
			if (f.getFieldName().equals(fieldName)) {
				fields.remove(f);
				break;
			}
		}

		return profile;
	}

	public ProfileJson removeFeature1(ProfileJson profile, String profileName,
			String moduleName) {
		DefineModule dm = new DefineModule();
		Vector<String> ftrs = dm.getFeatures(moduleName);
		Vector<String> profFtrs = getFeatureNames(profileName, moduleName);
		profFtrs.removeAll(ftrs);
		// System.out.println("Removable Features"+profFtrs);

		for (String rFtr : profFtrs) {
			removeFeature(profile, moduleName, rFtr);
		}

		return profile;
	}

	public ProfileJson removeEntity1(ProfileJson profile, String profileName,
			String moduleName) {
		DefineModule dm = new DefineModule();
		Vector<String> ets = dm.getEntityNames(moduleName);
		Vector<String> profEts = getEntityNames(profileName, moduleName);
		profEts.removeAll(ets);
		// System.out.println("Removable Features"+profEts);

		for (String rEnt : profEts) {
			removeEntity(profile, moduleName, rEnt);
		}

		return profile;
	}

	public ProfileJson removeField1(ProfileJson profile, String profileName,
			String moduleName, String entityName) {
		DefineModule dm = new DefineModule();
		Vector<String> fields = dm.getFields(moduleName, entityName);
		Vector<String> proFields = getFieldNames(profileName, moduleName,
				entityName);
		proFields.removeAll(fields);
		// System.out.println("Removable fields"+proFields);
		if (proFields.size() == 0) {
			return profile;
		}
		for (String fld : proFields) {
			removeField(profile, moduleName, entityName, fld);
		}

		return null;
	}

	public void removeUpdate() {
		Deploy dp = new Deploy();
		ArrayList<Document> pDocs = dp.getProfileDocs();
		ProfileJson profile = null;
		String profileName = "";
		String moduleName = "";
		Vector<String> entities = null;
		ObjectMapper mapper = new ObjectMapper();
		for (Document doc : pDocs) {
			try {
				profileName = doc.getItemValueString("prof_name");
			} catch (NotesException e) {
				e.printStackTrace();
			}
			profile = getProfileObj(profileName);
			for (Module module : profile.getModules()) {

				moduleName = module.getModuleName();
				// System.out.println("This module=="+moduleName);
				removeFeature1(profile, profileName, moduleName);
				removeEntity1(profile, profileName, moduleName);
				entities = getEntityNames(profile, moduleName);

				if (entities.size() != 0) {
					for (String entityName : entities) {
						// System.out.println("This entity=="+entityName);
						removeField1(profile, profileName, moduleName,
								entityName);
					}
				}

			}

			try {
				doc.replaceItemValue("JsonString", mapper
						.writeValueAsString(profile));
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (NotesException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				doc.save();
			} catch (NotesException e) {
				e.printStackTrace();
			}

		}

	}

	public boolean checkEntityDelete(String moduleName, String entityName) {
		Profile profile = (Profile) JSFUtil
				.getBindingValue("#{security.profile}");

		String eaccess = profile.getEntityAccessType(moduleName, entityName);
		// If it is Public read/Write then do nothing
		if (!eaccess.equals("2")) {
			if (profile.isEntityDelete(moduleName, entityName)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	
	/**
	 * This method is used to get the group names of the a given module, group
	 * names will be returned from the profile
	 * 
	 * @param moduleName
	 *@return Vector containing groupnames
	 */
	public Vector<String> getGroupPermission(String profileName, String moduleName) {

		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return setGroupViewScopeCrud(getJsonProfileObj(jsonString),moduleName);
	}
	
	/**
	 *This method will be used in getting the feature permissions which is not categorized into gruops or menu items, i.e single features which is seen in the view
	 *
	 *@param profileName
	 *@param moduleName
	 *@return
	 */
	public Vector<String> getSingleFeaturePerm(String profileName, String moduleName){
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
	
		
		Module module = null;
		
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		
		ArrayList<String> groupNames = getGroupNames(module);
		ArrayList<String> groupFeatureNames = getFeatureNames(groupNames,module);
		ArrayList<String> otherFeatureNames=getOtherFeatures(groupFeatureNames,module);
		return getFeaturePerm(module.getFeatures(),otherFeatureNames);
		
	}
	
	private Vector<String> getFeaturePerm(ArrayList<Feature> features, ArrayList<String> otherFeatureNames){
		Vector<String> featurePerm = new Vector<String>();
		
		if(features==null || otherFeatureNames==null){
			return null;
		}
		for(String featureN:otherFeatureNames){
			if(featureN!=null){
				for(Feature f:features){
					if(f!=null){
						if(f.getFeatureName().equals(featureN)){
							featurePerm.add(f.getFeatureName() + ":"
									+ f.getVisible());
						}
						
					}
				}
			}
			
		}
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("singleFPerm", featurePerm);
		viewScope.put("singleFN", featurePerm.size());
		return featurePerm;
		
		
	}
	private ArrayList<String> getOtherFeatures(ArrayList<String> groupFeatureNames,Module module){
		if(module==null){
			return null;
		}
		ArrayList<String> allFeatureNames = getAllFtNames(module);
		if(groupFeatureNames!=null){
			allFeatureNames.removeAll(groupFeatureNames);
		}
		 
		 return allFeatureNames;
		
		
	}
	private ArrayList<String> getAllFtNames(Module module){
		ArrayList<Feature> features = module.getFeatures();
		ArrayList<String> featureNames = new ArrayList<String>();
		if(features==null){
			return null;
		}
		for(Feature feat:features){
			if(feat!=null){
				featureNames.add(feat.getFeatureName());
			}
		}
		return featureNames;
		
		
		
	}
	
	private ArrayList<String> getFeatureNames(ArrayList<String> groupNames, Module module){
		ArrayList<String> featureNames = new ArrayList<String>();
		ArrayList<String> featuresInGroup = null;
		if(groupNames==null){
			return null;
		}
		ArrayList<GroupPermission> groups = module.getGroups();
		if(groups==null){
			return null;
		}
		
		for(String g:groupNames){
			if(g!=null){
				featuresInGroup = getFeaturesInGroup(g,groups);
				if(featuresInGroup!=null){
					featureNames.addAll(getFeaturesInGroup(g,groups));
				}
				
			}
		}
		return featureNames;
		
	}
	private ArrayList<String> getFeaturesInGroup(String groupName,ArrayList<GroupPermission> groups ){
		ArrayList<String> features = new ArrayList<String>();
		GroupPermission group = null;
		if(groupName!=null && groups==null){
			return null;
		}
		
		
			for(GroupPermission gp:groups){
				if(gp!=null){
					if(gp.getName().equals(groupName)){
						group = gp;
					}
				}
			}
		
			if(group.getEntries()==null){
				return null;
			}
			
			for(GroupEntry ge:group.getEntries()){
				if(ge!=null){
					features.add(ge.getName());
				}
			}
			
			return features;
			
		
		
	}
	
	
	private Vector<String> setGroupViewScopeCrud(ProfileJson profile,String moduleName) {
		Vector<String> groupSecurity = new Vector<String>();
		Module module = null;
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}

		if(module.getGroups()!=null){
			for(GroupPermission gp : module.getGroups()){
				if(gp!=null && gp.getEntries()!=null){
					groupSecurity.add(gp.getName()+":"+gp.getVisible());
				}
			}
		}
		
		

		Map viewScope = SessionContext.getViewScope();
		viewScope.put("groupPerm", groupSecurity);
		return groupSecurity;
	}
	
	private ProfileJson removeModule(ProfileJson profile, String moduleName) {
		Module module = getModule(profile, moduleName);
		if(module != null){
			profile.getModules().remove(module);
		}
		
		return profile;
	}

	
	public boolean removeModule(String moduleName) {
		Deploy dp = new Deploy();
		ArrayList<Document> pDocs = dp.getProfileDocs();
		ProfileJson profile = null;
		String profileName = "";
		ObjectMapper mapper = new ObjectMapper();
		for (Document doc : pDocs) {
			try {
				profileName = doc.getItemValueString("prof_name");
			} catch (NotesException e) {
				e.printStackTrace();
			}
			profile = getProfileObj(profileName);
			removeModule(profile,moduleName);

			try {
				doc.replaceItemValue("JsonString", mapper
						.writeValueAsString(profile));
			 
			} catch (Exception e) {
				
				e.printStackTrace();
				return false;
			}
			try {
				doc.save();
			} catch (NotesException e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}
	
	
}
