package bsuite.weber.jsonparsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import bsuite.weber.model.BsuiteWorkFlow;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

public class ProfileEdit extends BsuiteWorkFlow {

	public void getModulePermission(String profileName) throws NotesException {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setModuleViewScope(getJsonProfileObj(jsonString));
		profDoc.recycle();
	}

	public Vector<String> getEntityPermission(String profileName,
			String moduleName) {
		System.out.println("getting feature perm for" + profileName + " "
				+ moduleName);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return setEntityViewScopeCrud(getJsonProfileObj(jsonString), moduleName);

	}

	public Vector<String> getFieldPermission(String profileName,
			String moduleName, String entityName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return setFieldViewScopeCrud(getJsonProfileObj(jsonString), moduleName,
				entityName);

	}

	public void getFeaturePermission(String profileName, String moduleName,
			String entityName) {
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		@SuppressWarnings("unused")
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// setFeatureViewScopeCrud(getJsonProfileObj(jsonString),
		// moduleName,entityName);

	}

	public Vector<String> getFeaturePermission(String profileName,
			String moduleName) {
		System.out.println("getting feature perm for" + profileName + " "
				+ moduleName);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
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
		try {
			securitydb = session.getDatabase("", bsuitepath + "Security.nsf");
		} catch (NotesException e) {

			e.printStackTrace();
		}
		return securitydb;
	}

	@SuppressWarnings("unchecked")
	private void setModuleViewScope(ProfileJson profile) {
		Vector<String> moduleSecurity = new Vector<String>();

		for (Module mod : profile.getModules()) {
			moduleSecurity.add(mod.getModuleName() + ":" + mod.getTabvis());
		}
		viewScope.put("modulePermission", moduleSecurity);
		viewScope.put("numberOfModules", moduleSecurity.size());
		System.out.println("profile " + moduleSecurity);
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
		System.out.println("moduleperm1");
		System.out.println("moduleperm11" + vals);
		String[] arr = vals.split(",");
		System.out.println("moduleperm20");
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		System.out.println("moduleperm2");

		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("moduleperm3");

		ProfileJson profile = getJsonProfileObj(jsonString);
		System.out.println("moduleperm4");

		String[] moduleSecurity;

		String mSec = "";
		for (int i = 1; i < arr.length; i++) {
			System.out.println("moduleperm5");

			mSec = arr[i];
			moduleSecurity = mSec.split(":");
			System.out.println("ftrsecurity " + moduleSecurity);
			for (Module mod : profile.getModules()) {
				System.out.println("comparing " + moduleSecurity[0] + " "
						+ mod.getModuleName());
				if (moduleSecurity[0].equals(mod.getModuleName())) {
					System.out.println("module permission" + moduleSecurity[1]
							+ "  ");
					mod.setTabvis(moduleSecurity[1]);
				}
			}
		}
		System.out.println("moduleperm6");

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
		System.out.println("moduleperm7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public Vector<String> setEntityViewScopeCrud(ProfileJson profile,
			String moduleName) {
		Vector<String> entitySecurity = new Vector<String>();
		Module module = null;
		System.out.println("inside entity perm " + profile.getProfName() + " "
				+ moduleName);
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		System.out.println("inside entity perm1");
		if (module.getEntities() == null) {
			return null;
		}
		System.out.println("inside entity perm2");
		for (Entity e : module.getEntities()) {
			entitySecurity.add(e.getEntityName() + ":" + e.getCreate()
					+ e.getRead() + e.getUpdate() + e.getDelete()
					+ e.getAccessType());
		}
		System.out.println("inside entity perm3");
		System.out.println("vect entity perm" + entitySecurity);
		viewScope.put("entityPerm", entitySecurity);
		return entitySecurity;

	}

	@SuppressWarnings("unchecked")
	public Vector<String> setFieldViewScopeCrud(ProfileJson profile,
			String moduleName, String entityName) {
		Vector<String> fieldSecurity = new Vector<String>();
		Module module = null;
		System.out.println("inside entity perm " + profile.getProfName() + " "
				+ moduleName);
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
		System.out.println("vect entity perm" + fieldSecurity);
		viewScope.put("fieldPerm", fieldSecurity);
		return fieldSecurity;

	}

	@SuppressWarnings("unchecked")
	public Vector<String> setFeatureViewScopeCrud(ProfileJson profile,
			String moduleName) {
		Vector<String> featureSecurity = new Vector<String>();
		Module module = null;
		// System.out.println("inside entity perm "+profile.getProfName()+" "+moduleName);
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}

		for (Feature f : module.getFeatures()) {
			featureSecurity.add(f.getFeatureName() + ":" + f.getVisible());
		}
		System.out.println("vect entity perm" + featureSecurity);
		viewScope.put("featurePerm", featureSecurity);
		System.out.println("features" + featureSecurity);
		return featureSecurity;

	}

	public void saveEntityPerm(String profileName, String moduleName,
			String vals) {
		System.out.println("inside save" + vals);
		System.out.println("entity name" + moduleName);
		String[] arr = vals.split(",");
		System.out.println("inside save 1--" + arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		System.out.println("inside save 4");
		for (Module mod : profile.getModules()) {
			if (mod.getModuleName().equals(moduleName)) {
				module = mod;
				break;
			}

		}
		System.out.println("inside save 5");
		String[] entitySecurity;
		String eSec = "";
		for (int i = 1; i < arr.length; i++) {
			eSec = arr[i];
			System.out.println("inside save 51");
			System.out.println(eSec);
			entitySecurity = eSec.split(":");
			System.out.println("after split" + entitySecurity[0]);
			System.out.println("inside save 52");
			for (Entity entity : module.getEntities()) {
				System.out.println("inside save 53");
				System.out.println("field name" + entitySecurity[0]
						+ " getfname" + entity.getEntityName());

				if (entitySecurity[0].equals(entity.getEntityName())) {
					System.out.println("inside save 54" + entitySecurity[1]);
					entity.setCreate(Character.toString(entitySecurity[1]
							.charAt(0)));
					entity.setRead(Character.toString(entitySecurity[1]
							.charAt(1)));
					entity.setUpdate(Character.toString(entitySecurity[1]
							.charAt(2)));
					entity.setDelete(Character.toString(entitySecurity[1]
							.charAt(3)));
				}
			}
		}
		System.out.println("inside save 6");

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
		System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside save 8");

	}

	public void saveFieldPerm(String profileName, String moduleName,
			String entityName, String vals) {
		System.out.println("inside save" + vals);
		System.out.println("entity name" + moduleName);
		String[] arr = vals.split(",");
		System.out.println("inside save 1--" + arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		System.out.println("inside save 2");
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside save 3");
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		System.out.println("inside save 4");
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

		System.out.println("inside save 5");
		String[] fieldSecurity;
		String fSec = "";
		for (int i = 1; i < arr.length; i++) {
			fSec = arr[i];
			System.out.println("inside save 51");
			System.out.println(fSec);
			fieldSecurity = fSec.split(":");
			System.out.println("after split" + fieldSecurity[0]);
			System.out.println("inside save 52");
			for (Field field : entity.getFields()) {
				System.out.println("inside save 53");
				System.out.println("field name" + fieldSecurity[0]
						+ " getfname" + field.getFieldName());

				if (fieldSecurity[0].equals(field.getFieldName())) {
					System.out.println("inside save 54" + fieldSecurity[1]);
					field.setVisible(Character.toString(fieldSecurity[1]
							.charAt(0)));
					field.setReadonly(Character.toString(fieldSecurity[1]
							.charAt(1)));
				}
			}
		}
		System.out.println("inside save 6");

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
		System.out.println("inside save 7");

		try {
			profDoc.replaceItemValue("JsonString", jsonString2);
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside save 8");

	}

	/*
	 * public void saveFeaturePerm(String profileName, String moduleName,String
	 * entityName, String vals){ System.out.println("inside save"+vals);
	 * System.out.println("entity name"+moduleName); String[] arr =
	 * vals.split(","); System.out.println("inside save 1--"+arr[1]); Document
	 * profDoc = getProfileDoc(getSecurityDatabase(),profileName); String
	 * jsonString=""; System.out.println("inside save 2"); try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * System.out.println("inside save 3"); ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null;
	 * System.out.println("inside save 4"); for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(moduleName)){
	 * module = mod; break; }
	 * 
	 * } Entity entity = null; for(Entity e:module.getEntities()){
	 * if(e.getEntityName().equals(entityName)){ entity = e; break; }
	 * 
	 * }
	 * 
	 * System.out.println("inside save 5"); String[] featureSecurity; String
	 * fSec=""; for(int i=1;i<arr.length;i++){ fSec = arr[i];
	 * System.out.println("inside save 51"); System.out.println(fSec);
	 * featureSecurity = fSec.split(":");
	 * System.out.println("after split"+featureSecurity[0]);
	 * System.out.println("inside save 52"); for(Feature
	 * feature:entity.getFeatures()){ System.out.println("inside save 53");
	 * System.out.println("field name"+featureSecurity[0]+" getfname"+feature.
	 * getFeatureName());
	 * 
	 * if(featureSecurity[0].equals(feature.getFeatureName())){
	 * System.out.println("inside save 54"+featureSecurity[1]);
	 * feature.setVisible(Character.toString(featureSecurity[1].charAt(0)));
	 * 
	 * } } } System.out.println("inside save 6");
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } System.out.println("inside save 7");
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } System.out.println("inside save 8");
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
	 * vals){ System.out.println("inside save"+vals);
	 * System.out.println("entity name"+entityName); String[] arr =
	 * vals.split(","); System.out.println("inside save 1--"+arr[1]); Document
	 * profDoc = getProfileDoc(getSecurityDatabase(),profileName); String
	 * jsonString=""; System.out.println("inside save 2"); try { jsonString =
	 * profDoc.getItemValueString("JsonString"); } catch (NotesException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * System.out.println("inside save 3"); ProfileJson profile =
	 * getJsonProfileObj(jsonString); Module module = null;
	 * System.out.println("inside save 4"); for(Module
	 * mod:profile.getModules()){ if(mod.getModuleName().equals(entityName)){
	 * module = mod; break; }
	 * 
	 * } System.out.println("inside save 5"); String[] fldSecurity; String
	 * fSec=""; for(int i=1;i<arr.length;i++){ fSec = arr[i];
	 * System.out.println("inside save 51"); System.out.println(fSec);
	 * fldSecurity = fSec.split(":");
	 * System.out.println("after split"+fldSecurity[0]);
	 * System.out.println("inside save 52"); for(FieldPerm
	 * fldPrm:module.getFieldsperm()){ System.out.println("inside save 53");
	 * System
	 * .out.println("field name"+fldSecurity[1]+" getfname"+fldPrm.getFname());
	 * 
	 * if(fldSecurity[1].equals(fldPrm.getFname())){
	 * System.out.println("inside save 54"+fldSecurity[2]);
	 * fldPrm.setVisible(Character.toString(fldSecurity[2].charAt(0)));
	 * System.out.println("inside save 541 visble"+fldSecurity[2].charAt(0));
	 * fldPrm.setReadonly(Character.toString(fldSecurity[2].charAt(1)));
	 * System.out.println("inside save 541 ronly"+fldSecurity[2].charAt(1));
	 * System.out.println("inside save 55"); } } }
	 * System.out.println("inside save 6");
	 * 
	 * ObjectMapper mapper = new ObjectMapper(); String jsonString2 = ""; try {
	 * jsonString2 = mapper.writeValueAsString(profile); } catch
	 * (JsonGenerationException e) { e.printStackTrace(); } catch
	 * (JsonMappingException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } System.out.println("inside save 7");
	 * 
	 * try { profDoc.replaceItemValue("JsonString",jsonString2 );
	 * profDoc.save(); } catch (NotesException e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); } System.out.println("inside save 8");
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
	 * = fSec.split(":"); System.out.println("ftrsecurity "+ftrSecurity);
	 * for(FeaturePerm ftrPrm:module.getFeaturesperm()){
	 * System.out.println("comparing "+ftrSecurity[1]+ " "
	 * +ftrPrm.getFeaturename());
	 * if(ftrSecurity[1].equals(ftrPrm.getFeaturename())){
	 * System.out.println("ftr permission"+ftrSecurity[2]+"  ");
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
		System.out.println("inside getNumber of modules"+profileName);
		Document profDoc = getProfileDoc(getSecurityDatabase(), profileName);
		String jsonString = "";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside getNumber of modules2"+profileName);
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
		System.out.println("getNumber of modules");
		ArrayList<Module> modules = profile.getModules();
		if (modules != null) {
			return modules.size();
		}
		System.out.println("getNumber of modules2");

		return 0;
	}
}
