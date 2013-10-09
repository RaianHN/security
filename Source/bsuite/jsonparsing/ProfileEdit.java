package bsuite.jsonparsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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

	@SuppressWarnings("unchecked")
	private Vector<String> setGroupEntryCrud(ProfileJson profile,
			String moduleName, String groupName) {
		
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
			return null;
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
		if(fields!=null){
			for (Field f : fields) {
				fieldSecurity.add(f.getFieldName() + ":" + f.getVisible()
						+ f.getReadonly());
			}
		}
		
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("fieldPerm", fieldSecurity);
		if(fields==null){
			viewScope.put("fieldN", 0);
		}else{
			viewScope.put("fieldN", Integer.toString(entity.getFields().size()));
		}
		return fieldSecurity;

	}

	@SuppressWarnings("unchecked")
	public Vector<String> setFeatureViewScopeCrud(ProfileJson profile,
			String moduleName) {
		Vector<String> featureSecurity = new Vector<String>();
		Module module = null;
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}

		if (module.getFeatures() != null) {
			for (Feature f : module.getFeatures()) {

				if (f != null) {
					featureSecurity.add(f.getFeatureName() + ":"
							+ f.getVisible());
				}

			}
		}

		
		Map viewScope = SessionContext.getViewScope();
		viewScope.put("featurePerm", featureSecurity);
		
		return featureSecurity;

	}

	public void saveEntityPerm(String profileName, String vals) {
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
			for (Entity entity : module.getEntities()) {

				

				if (entityName.equals(entity.getEntityName())) {
					
					entity.setCreate(Character.toString(entityPerm.charAt(0)));
					entity.setRead(Character.toString(entityPerm.charAt(1)));
					entity.setUpdate(Character.toString(entityPerm.charAt(2)));
					entity.setDelete(Character.toString(entityPerm.charAt(3)));
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
		String moduleName = "";
		String groupName = "";
		String groupPerm = "";

		
		for (int x = 0; x < arr.length; x++) {
			arrs = arr[x].split(":");
			
			moduleName = arrs[0];
			groupName = arrs[1];
			groupPerm = arrs[2];
			
			for (Module mod : profile.getModules()) {
				if (mod.getModuleName().equals(moduleName)) {
					module = mod;
					break;
				}

			}
			for (GroupPermission group : module.getGroups()) {
				if (groupName.equals(group.getName())) {
					group.setVisible(Character.toString(groupPerm.charAt(0)));
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
	

	public void saveFieldPerm(String profileName, String moduleName,
			String entityName, String vals) {

		String[] arr = vals.split(",");
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
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

		String[] fieldSecurity;
		String fSec = "";
		for (int i = 1; i < arr.length; i++) {
			fSec = arr[i];
			fieldSecurity = fSec.split(":");
			for (Field field : entity.getFields()) {
				

				if (fieldSecurity[0].equals(field.getFieldName())) {
					field.setVisible(Character.toString(fieldSecurity[1]
							.charAt(0)));
					field.setReadonly(Character.toString(fieldSecurity[1]
							.charAt(1)));
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

	public void saveFieldPerm(String profileName, String vals) {
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

		String moduleName = "";
		String entityName = "";
		String fieldName = "";
		String fieldSec = "";
		for (int i =0; i < arr.length; i++) {
			arrs = arr[i].split(":");
			if(arrs.length>1){
				moduleName = arrs[0];
				entityName = arrs[1];
			}else{
				break;
			}

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


	public int getNumberOfMOdules(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		return getModuleNumber(getJsonProfileObj(jsonString));

	}

	public int getNumberOfFeatures(String profileName, String moduleName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}

		return getFeatureNumber(getJsonProfileObj(jsonString), moduleName);

	}

	private int getFeatureNumber(ProfileJson profile, String moduleName) {
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
			e.printStackTrace();
		}
		return getEntityNumber(getJsonProfileObj(jsonString), moduleName);

	}

	private int getEntityNumber(ProfileJson profile, String moduleName) {
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
			e.printStackTrace();
		}
		return getActionNumber(getJsonProfileObj(jsonString), moduleName,
				groupName);

	}

	private int getActionNumber(ProfileJson profile, String moduleName,
			String groupName) {
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

	private int getModuleNumber(ProfileJson profile) {
		ArrayList<Module> modules = profile.getModules();
		if (modules != null) {
			return modules.size();
		}

		return 0;
	}

	public Vector<String> getModuleNames(String profileName) {
		ProfileJson profile = getProfileObj(profileName);
		if (profile == null) {
			return null;
		}
		Vector<String> modules = new Vector<String>();
		for (Module mod : profile.getModules()) {
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
		return entities;
	}

	public Vector<String> getEntityNames(ProfileJson profile, String moduleName) {
		Module module = getModule(profile, moduleName);
		Vector<String> entities = new Vector<String>();
		for (Entity entity : module.getEntities()) {
			entities.add(entity.getEntityName());
		}
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
		String[] arr = null;
		String[] arrs = null;
		if (vals.contains(",")) {
			arr = vals.split(",");
		} else {
			arr = new String[1];
			arr[0] = vals;
		}

		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
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
			for (Entity entity : module.getEntities()) {
				

				if (entityName.equals(entity.getEntityName())) {

					entity.setAccessType(accessPerm);
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



	public String getModuleN(String profileName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
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
			e.printStackTrace();
		}

		ProfileJson profile = getJsonProfileObj(jsonString);
		
		ArrayList<Module> modules = profile.getModules();
		
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
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
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

		for (String rFtr : profFtrs) {
			removeFeature(profile, moduleName, rFtr);
		}

		return profile;
	}

	@SuppressWarnings("unchecked")
	public ProfileJson removeEntity1(ProfileJson profile, String profileName,
			String moduleName) {
		DefineModule dm = new DefineModule();
		Vector<String> ets = dm.getEntityNames(moduleName);
		Vector<String> profEts = getEntityNames(profileName, moduleName);
		profEts.removeAll(ets);

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
				removeFeature1(profile, profileName, moduleName);
				removeEntity1(profile, profileName, moduleName);
				entities = getEntityNames(profile, moduleName);

				if (entities.size() != 0) {
					for (String entityName : entities) {
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
	
	@SuppressWarnings("unchecked")
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
	
	
	@SuppressWarnings("unchecked")
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
