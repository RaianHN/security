package bsuite.weber.jsonparsing;

import java.io.IOException;
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

public class ProfileEdit extends BsuiteWorkFlow  {
	
	public void getModulePermission(String profileName){
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setModuleViewScope(getJsonProfileObj(jsonString));
		
	}
	


public Document getProfileDoc(Database db, String profileName){
		
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
	

public Database getSecurityDatabase(){
	Database securitydb = null;
	try {
		securitydb = session.getDatabase("", bsuitepath
				+ "Security.nsf");
	} catch (NotesException e) {
		
		e.printStackTrace();
	}
	return securitydb;
}
	

private void setModuleViewScope(ProfileJson profile) {
	Vector<String> moduleSecurity = new Vector<String>();
	
	for(Module mod:profile.getModules()){			
		moduleSecurity.add(mod.getModuleName()+":"+mod.getTabvis());
	}
	viewScope.put("modulePermission", moduleSecurity);
	
}


public ProfileJson getJsonProfileObj(String jsonString){
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
	/*
	public void getEntityCrud(String profileName, String entityName){
			Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
			String jsonString="";
			try {
				jsonString = profDoc.getItemValueString("JsonString");
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setEntityViewScopeCrud(getJsonProfileObj(jsonString), entityName);
	}
	
	
	
	public void getFeildPermission(String profileName, String entityName){
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setFieldViewScopeCrud(getJsonProfileObj(jsonString), entityName);
	}
	
	public void getFeaturePermission(String profileName, String entityName){
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setFeatureViewScopeCrud(getJsonProfileObj(jsonString), entityName);
		
	}
	
	
	
	
	
	
	public void setEntityViewScopeCrud(ProfileJson profile,String moduleName, String entityName){
		Vector<String> entitySecurity = new Vector<String>();
		
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(moduleName)){
				entitySecurity = mod;
				break;
			}
			
		}
		for(FieldPerm field:module.getFieldsperm()){			
			fldSecurity.add(field.getFname()+":"+field.getVisible()+field.getReadonly());
		}
		viewScope.put("fieldPerm", fldSecurity);
		
		
		if(module.getTabvis().equals("1")){
			viewScope.put("v", true);
		}else{
			viewScope.put("v", false);
		}
		
		if(module.getCreate().equals("1")){
			viewScope.put("c", true);
		}else{
			viewScope.put("c", false);
		}
		
		if(module.getRead().equals("1")){
			viewScope.put("r", true);
		}else{
			viewScope.put("r", false);
		}
		
		if(module.getUpdate().equals("1")){
			viewScope.put("u", true);
		}else{
			viewScope.put("u", false);
		}
		
		if(module.getDelete().equals("1")){
			viewScope.put("d", true);
		}else{
			viewScope.put("d", false);
		}
		
		
	}
		
	public void setFieldViewScopeCrud(ProfileJson profile, String entityName){
		Vector fldSecurity = new Vector();
		
		Module module = null;
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(entityName)){
				module = mod;
				break;
			}
			
		}
		
		for(FieldPerm field:module.getFieldsperm()){			
			fldSecurity.add(field.getFname()+":"+field.getVisible()+field.getReadonly());
		}
		viewScope.put("fieldPerm", fldSecurity);
		
	}
		
	public void setFeatureViewScopeCrud(ProfileJson profile, String entityName){
		Vector ftrSecurity = new Vector();
		Module module = null;
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(entityName)){
				module = mod;
				break;	
			}
			
		}
		
		for(FeaturePerm ftrPerm:module.getFeaturesperm()){		
			ftrSecurity.add(ftrPerm.getFeaturename()+":"+ftrPerm.getVisible());
			
		}
		viewScope.put("featurePerm", ftrSecurity);
		
	}
	
	public void saveEntityCrud(String profileName, String entityName,String v, String c, String r, String u, String d ){
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(entityName))
			{
				module = mod;
				break;
			}
			
		}
		module.setTabvis(v);
		module.setCreate(c);
		module.setRead(r);
		module.setUpdate(u);
		module.setDelete(d);
		
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
			profDoc.replaceItemValue("JsonString",jsonString2 );
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void saveFieldCrud(String profileName, String entityName, String vals){
		System.out.println("inside save"+vals);
		System.out.println("entity name"+entityName);
		String[] arr = vals.split(",");
		System.out.println("inside save 1--"+arr[1]);
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
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
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(entityName)){
				module = mod;
				break;
			}
			
		}
		System.out.println("inside save 5");
		String[] fldSecurity;
		String fSec="";
		for(int i=1;i<arr.length;i++){
			fSec = arr[i];
			System.out.println("inside save 51");
			System.out.println(fSec);
			fldSecurity = fSec.split(":");
			System.out.println("after split"+fldSecurity[0]);
			System.out.println("inside save 52");
			for(FieldPerm fldPrm:module.getFieldsperm()){
				System.out.println("inside save 53");
				System.out.println("field name"+fldSecurity[1]+" getfname"+fldPrm.getFname());
				
				if(fldSecurity[1].equals(fldPrm.getFname())){
					System.out.println("inside save 54"+fldSecurity[2]);
					fldPrm.setVisible(Character.toString(fldSecurity[2].charAt(0)));
					System.out.println("inside save 541 visble"+fldSecurity[2].charAt(0));
					fldPrm.setReadonly(Character.toString(fldSecurity[2].charAt(1)));
					System.out.println("inside save 541 ronly"+fldSecurity[2].charAt(1));
					System.out.println("inside save 55");
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
			profDoc.replaceItemValue("JsonString",jsonString2 );
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("inside save 8");
		
		
	}
	public void saveFeatureCrud(String profileName, String entityName, String vals){
		
		String[] arr = vals.split(",");
		Document profDoc = getProfileDoc(getSecurityDatabase(),profileName);
		String jsonString="";
		try {
			jsonString = profDoc.getItemValueString("JsonString");
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProfileJson profile = getJsonProfileObj(jsonString);
		Module module = null;
		for(Module mod:profile.getModules()){
			if(mod.getModuleName().equals(entityName)){
				module = mod;
				break;	
			}
			
		}
		String[] ftrSecurity; 
		
		
		String fSec="";
		for(int i=1;i<arr.length;i++){
			fSec = arr[i];
			ftrSecurity = fSec.split(":");
			System.out.println("ftrsecurity "+ftrSecurity);
			for(FeaturePerm ftrPrm:module.getFeaturesperm()){
				System.out.println("comparing "+ftrSecurity[1]+ " " +ftrPrm.getFeaturename());
				if(ftrSecurity[1].equals(ftrPrm.getFeaturename())){
					System.out.println("ftr permission"+ftrSecurity[2]+"  ");
					ftrPrm.setVisible(ftrSecurity[2]);				
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
			profDoc.replaceItemValue("JsonString",jsonString2 );
			profDoc.save();
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
