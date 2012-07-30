package bsuite.weber.relationship;

import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.View;
import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.tools.JSFUtil;

public class Association extends BsuiteWorkFlow {
	
	public  Document getAssociatedProfile(String currentuser) {
		try {
			System.out.println("in get assoprofile");
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_A";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			System.out.println("in get assoprofile1");
			Document persondoc = getPerson(abbrname);
			System.out.println("in get assoprofile2");
			String persondocunid = persondoc.getUniversalID();
			System.out.println("in get assoprofile3");
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			System.out.println("in get assoprofile4");
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			System.out.println("in get assoprofile5");
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			System.out.println("in get assoprofile6");
			if(targetunid==null){
				return null;
			}
			Document profiledoc = securitydb.getDocumentByUNID(targetunid);
			System.out.println("in get assoprofile2");
			return profiledoc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public  String getAssociatedRoleName(String currentuser){
		try {
			System.out.println("get ass rolename1 ");
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			String relname = "HAS_ROLE";
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();
			System.out.println("get ass rolename2 ");
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			if(targetunid==null){
				return null;
			}
			Document roledoc = securitydb.getDocumentByUNID(targetunid);
			System.out.println("get ass rolename3 ");
			return roledoc.getItemValueString("role_name");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public Document getPerson(String username) {
		try {

			Database namesdb = session.getDatabase("", "names.nsf");
			View peopleview = namesdb.getView("($VIMPeople)");
			Document userdoc = peopleview.getDocumentByKey(username);
			
			return userdoc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public String getFormattedName(String currentuser, String param) {
		try {			

			Name user = session.createName(currentuser);
			if (param.equals("abr")) {
				return user.getAbbreviated();
			}

			else if (param.equals("canonical")) {
				return user.getCanonical();
			} else if (param.equals("common")) {
				return user.getCommon();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	public String getEntityUnid(String entityName){
		
			try {
				Database entitiesdb = session.getDatabase("", bsuitepath+"bentity.nsf");				
				View allView = entitiesdb.getView("Entities");				
				Document entityDoc = allView.getDocumentByKey(entityName);
				return entityDoc.getUniversalID();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		
	}
	
	public String getParentEntity(String entityName){	
		String entityUnid = getEntityUnid(entityName);
		String relDocUnid = getRelationDocUnid("IS_A");
		String lookupkey = JSFUtil.getlookupkey(relDocUnid, entityUnid);
		Vector tmp = new Vector();
		tmp.add(lookupkey);
		String targetunid = JSFUtil.DBLookupString("relation","SourceRelation", tmp, 4);
		//Not complete
		
		return "";
	}
	
	private String getRelationDocUnid(String relName){
		
		try{
			Database reldb = session.getDatabase("", bsuitepath
					+ "relation.nsf");
			
			View relview = reldb.getView("CategoryRelation");
			Document reldoc = relview.getDocumentByKey(relName);
			return reldoc.getUniversalID();	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}

	
	//This function is use to get the profileName while opening the Employee document. to get default value
	public String getDefaultProfileName(String employeeId){
		try{
			String relDocUnid = getRelationDocUnid("IS_A");
			String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String personunid = JSFUtil.DBLookupString("relation",
					"TargetRelation", tmp, 4);
			String relationid=getRelationDocUnid("HAS_A");
			String lookupkey1 = JSFUtil.getlookupkey(relationid,personunid);
			Vector tmp1 = new Vector();
			tmp1.add(lookupkey1);
			String profileunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp1, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			
			Document profiledoc = securitydb.getDocumentByUNID(profileunid);
			
			return profiledoc.getItemValueString("prof_name");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return "";
		
	}
	

	//This function is use to get the profileName while opening the Employee document. to get default value
	public String getDefaultRoleName(String employeeId){
		try{
			String relDocUnid = getRelationDocUnid("IS_A");
			String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String personunid = JSFUtil.DBLookupString("relation",
					"TargetRelation", tmp, 4);
			String relationid=getRelationDocUnid("HAS_ROLE");
			String lookupkey1 = JSFUtil.getlookupkey(relationid,personunid);
			Vector tmp1 = new Vector();
			tmp1.add(lookupkey1);
			String roleunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp1, 4);
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			
			Document roledoc = securitydb.getDocumentByUNID(roleunid);
			
			return roledoc.getItemValueString("role_name");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return "";
		
	}

	public String getDefaultPersonName(String employeeId){
	
		try{
		
			Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
			String moduleName=(String)viewscope.get("moduleName");
			if(moduleName.contains("_")){
				moduleName=moduleName.replace("_"," ");
			}
			
			//get the module name and treat it as database name
			String dbname=moduleName.toLowerCase().replace(" ", "");
			dbname=dbname+".nsf";	
		
			Database db = session.getDatabase("", bsuitepath+dbname);
			System.out.println("db Name "+db.getFileName());
		Document empdoc=db.getDocumentByUNID(employeeId);
		String fullname=empdoc.getItemValueString("FullName");
		System.out.println("FullName "+fullname);
			return fullname;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
	
	
	
	public void editEmployee(String employeeId,String oldprofile,String newprofile,String oldrole,String newrole){
		/*Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");		
		String moduleName=(String)viewscope.get("moduleName");
		if(moduleName.contains("_")){
			moduleName=moduleName.replace("_"," ");
		}
		
		//get the module name and treat it as database name
		String dbname=moduleName.toLowerCase().replace(" ", "");
		dbname=dbname+".nsf";	
		try{
			Database db = session.getDatabase("", bsuitepath+dbname);
			Document edoc=db.getDocumentByUNID(employeeId);
		}catch (Exception e) {
			// TODO: handle exception
		}*/
		
		updateAssociatedProfile(employeeId,oldprofile,newprofile);
		updateAssociatedRole(employeeId,oldrole,newrole);
	}
	
	private void updateAssociatedProfile(String employeeId, String oldprofile,String newprofile){
		if(!oldprofile.equals(newprofile)){
			String relDocUnid = getRelationDocUnid("IS_A");
			String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			//to get personunid and get associated role doc
			String personunid = JSFUtil.DBLookupString("relation",
					"TargetRelation", tmp, 4);
			//get the unid of the new profile name
			try{
				Database securitydb = session.getDatabase("", bsuitepath
						+ "Security.nsf");
				View view=securitydb.getView("ProfileView");
				Document newprofiledoc=view.getDocumentByKey(newprofile);
				String newprofileunid=newprofiledoc.getUniversalID();
				//get has_a relation
				String relationId = getRelationDocUnid("HAS_A");
				String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
				Vector tmp1 = new Vector();
				tmp1.add(lookupkey1);
				Database relationdb = session.getDatabase("", bsuitepath
						+ "relation.nsf");	
				//get the relationship doc which shows the Role association
				View relview=relationdb.getView("SourceRelation");
				Document profileassodoc=relview.getDocumentByKey(lookupkey1);
				profileassodoc.replaceItemValue("targetid",newprofileunid);
				profileassodoc.replaceItemValue("trg_data", newprofile);
				profileassodoc.save(true,false);
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
		
		
		
			
	}
	
private void updateAssociatedRole(String employeeId, String oldrole,String newrole){
	
	if(!oldrole.equals(newrole)){
		String relDocUnid = getRelationDocUnid("IS_A");
		String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
		Vector tmp = new Vector();
		tmp.add(lookupkey);
		//to get personunid and get associated role doc
		String personunid = JSFUtil.DBLookupString("relation",
				"TargetRelation", tmp, 4);
		//get the unid of the new role name
		
		try{
			Database securitydb = session.getDatabase("", bsuitepath
					+ "Security.nsf");
			View view=securitydb.getView("RolesView");
			Document newroledoc=view.getDocumentByKey(newrole);
			String newroleunid=newroledoc.getUniversalID();
			//get has_role relation
			String relationId = getRelationDocUnid("HAS_ROLE");
			String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
			Vector tmp1 = new Vector();
			tmp1.add(lookupkey1);
			Database relationdb = session.getDatabase("", bsuitepath
					+ "relation.nsf");	
			//get the relationship doc which shows the Role association
			View relview=relationdb.getView("SourceRelation");
			Document roleassodoc=relview.getDocumentByKey(lookupkey1);
			roleassodoc.replaceItemValue("targetid",newroleunid);
			roleassodoc.replaceItemValue("trg_data", newrole);
			roleassodoc.save(true,false);
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		
	}	
	
}


public void deleteAssoDoc(String employeeId){
	deleteEmpPersonAsso(employeeId);
	deleteProfileDoc(employeeId);
	deleteRoleDoc(employeeId);
	
}

private void deleteRoleDoc(String employeeId){
	
	String relDocUnid = getRelationDocUnid("IS_A");
	String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
	Vector tmp = new Vector();
	tmp.add(lookupkey);
	//to get personunid and get associated role doc
	String personunid = JSFUtil.DBLookupString("relation",
			"TargetRelation", tmp, 4);
	
	
		//get has_a relation
		String relationId = getRelationDocUnid("HAS_ROLE");
		String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
		Vector tmp1 = new Vector();
		tmp1.add(lookupkey1);
		try{
			Database relationdb = session.getDatabase("", bsuitepath
					+ "relation.nsf");	
			//get the relationship doc which shows the Role association
			View relview=relationdb.getView("SourceRelation");
			Document roleassodoc=relview.getDocumentByKey(lookupkey1);
			roleassodoc.remove(true);
		}catch (Exception e) {
			// TODO: handle exception
		}		
	
}



private void deleteProfileDoc(String employeeId){
	
	
	String relDocUnid = getRelationDocUnid("IS_A");
	String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
	Vector tmp = new Vector();
	tmp.add(lookupkey);
	//to get personunid and get associated role doc
	String personunid = JSFUtil.DBLookupString("relation",
			"TargetRelation", tmp, 4);
	
	
		//get has_a relation
		String relationId = getRelationDocUnid("HAS_A");
		String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
		Vector tmp1 = new Vector();
		tmp1.add(lookupkey1);
		try{
			Database relationdb = session.getDatabase("", bsuitepath
					+ "relation.nsf");	
			//get the relationship doc which shows the Role association
			View relview=relationdb.getView("SourceRelation");
			Document profileassodoc=relview.getDocumentByKey(lookupkey1);
			profileassodoc.remove(true);
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	
}
	
private void deleteEmpPersonAsso(String employeeId){

	String relDocUnid = getRelationDocUnid("IS_A");
	String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
	Vector tmp = new Vector();
	tmp.add(lookupkey);
	//to get personunid and get associated role doc
	try{
		Database relationdb = session.getDatabase("", bsuitepath
				+ "relation.nsf");	
		//get the relationship doc which shows the Role association
		View relview=relationdb.getView("TargetRelation");
		Document emppersonassodoc=relview.getDocumentByKey(lookupkey);
		emppersonassodoc.remove(true);
	}catch (Exception e) {
		// TODO: handle exception
	}
	
}
}
