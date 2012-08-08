package bsuite.weber.relationship;

import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Name;
import lotus.domino.View;
import bsuite.weber.tools.BsuiteMain;
import bsuite.weber.tools.JSFUtil;

/**
[As name suggest, this class is use to get the association document, to get the asoociated profile, RoleName]
   
  @author TSangmo
  @created On Aug 8, 2012
 */
public class Association extends BsuiteMain {
	
	private Database namesdb;
	private Database securityDb;
	private Database reldb;
	private View roleView ; 
	private View categoryRelationView;
	
	
	
	public Association() {
		try{
			namesdb = session.getDatabase("", "names.nsf");
			securityDb = session.getDatabase("", bsuitepath	+ "Security.nsf");
			reldb = session.getDatabase("", bsuitepath + "relation.nsf");
			
			roleView=securityDb.getView("RolesView");
			categoryRelationView = reldb.getView("CategoryRelation");
		}catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	/**
	 
	 [To get the user associated profile name]
	  
	  @return [user's profile Name]
	 
	@param currentuser
	@return
	 */
	@SuppressWarnings({ "unchecked"})
	public  Document getAssociatedProfile(String currentuser) {
		try {
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
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			
			if(targetunid==null){
				return null;
			}
			Document profiledoc = securityDb.getDocumentByUNID(targetunid);
			return profiledoc;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 
	 [To get the user associated rolename]
	  
	  @return [RoleName]
	 
	@param currentuser
	@return
	 */
	@SuppressWarnings("unchecked")
	public  String getAssociatedRoleName(String currentuser){
		try {
		
			String relname = "HAS_ROLE";			
			Document reldoc = categoryRelationView.getDocumentByKey(relname);
			String reldocunid = reldoc.getUniversalID();

			// Person unid
			// Changing the canonical form to abr form so that it will lookup in
			// the $VIMPeople view
			String abbrname = getFormattedName(currentuser, "abr");
			Document persondoc = getPerson(abbrname);
			String persondocunid = persondoc.getUniversalID();
			String lookupkey = JSFUtil.getlookupkey(reldocunid, persondocunid);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			String targetunid = JSFUtil.DBLookupString("relation",
					"SourceRelation", tmp, 4);
			
			if(targetunid==null){
				return null;
			}
			Document roledoc = securityDb.getDocumentByUNID(targetunid);
			return roledoc.getItemValueString("role_name");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	
	
	public Document getPerson(String username) {
		try {			
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
			e.printStackTrace();
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
	
	@SuppressWarnings({ "unchecked"})
	public String getParentEntity(String entityName){	
		String entityUnid = getEntityUnid(entityName);
		String relDocUnid = getRelationDocUnid("IS_A");
		String lookupkey = JSFUtil.getlookupkey(relDocUnid, entityUnid);
		Vector tmp = new Vector();
		tmp.add(lookupkey);
		return "";
	}
	
	private String getRelationDocUnid(String relName){
		
		try{		
			Document reldoc = categoryRelationView.getDocumentByKey(relName);
			return reldoc.getUniversalID();	
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "";
	}
	
	
	
	/**
	 
	 [This function is use to get the profileName while opening the Employee document. to get default value]
	  
	  @return [ProfileName of the employee]
	 
	@param employeeId
	@return
	 */
	@SuppressWarnings("unchecked")
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
			//Database securitydb = session.getDatabase("", bsuitepath
			//		+ "Security.nsf");
			
			Document profiledoc = securityDb.getDocumentByUNID(profileunid);
			
			return profiledoc.getItemValueString("prof_name");
		}catch (Exception e) {
		}
		
		return "";
		
	}
	

	
	/**
	 
	 [This function is use to get the role Name while opening the Employee document. to get default value.]
	  
	  @return [It will return RoleName]
	 
	@param employeeId
	@return
	 */
	@SuppressWarnings("unchecked")
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
			Document roledoc = securityDb.getDocumentByUNID(roleunid);
			
			return roledoc.getItemValueString("role_name");
		}catch (Exception e) {
		}
		
		return "";
		
	}

	@SuppressWarnings("unchecked")
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
		Document empdoc=db.getDocumentByUNID(employeeId);
		String fullname=empdoc.getItemValueString("FullName");
			return fullname;
		}catch (Exception e) {
		}
		return "";
	}
	
	
	
	public void editEmployee(String employeeId,String oldprofile,String newprofile,String oldrole,String newrole){
			
		updateAssociatedProfile(employeeId,oldprofile,newprofile);
		updateAssociatedRole(employeeId,oldrole,newrole);
	}
	public void editEmployee(String employeeId,String newrole,String newprofile){
		
		updateAssociatedProfile(employeeId,newprofile);
		updateAssociatedRole(employeeId,newrole);
	}
	
	/**
	 
	 [It will update the profile name in the Person-Profile association document when Admin change the profile name for the user]
	  
	  @return [nothing]
	 
	@param employeeId
	@param oldprofile
	@param newprofile
	 */
	@SuppressWarnings("unchecked")
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
				
				View view=securityDb.getView("ProfileView");
				Document newprofiledoc=view.getDocumentByKey(newprofile);
				String newprofileunid=newprofiledoc.getUniversalID();
				//get has_a relation
				String relationId = getRelationDocUnid("HAS_A");
				String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
				Vector tmp1 = new Vector();
				tmp1.add(lookupkey1);
					
				//get the relationship doc which shows the Role association
				View relview=reldb.getView("SourceRelation");
				Document profileassodoc=relview.getDocumentByKey(lookupkey1);
				profileassodoc.replaceItemValue("targetid",newprofileunid);
				profileassodoc.replaceItemValue("trg_data", newprofile);
				profileassodoc.save(true,false);
				
			}catch (Exception e) {
			}
			
		}		
		
		
			
	}
	/**
	 
	 [It will update the profile name in the Person-Profile association document when Admin change the profile name for the user]
	  
	  @return [nothing]
	 
	@param employeeId
	@param newprofile
	 */
	@SuppressWarnings("unchecked")
	private void updateAssociatedProfile(String employeeId, String newprofile){
		
			String relDocUnid = getRelationDocUnid("IS_A");
			String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
			Vector tmp = new Vector();
			tmp.add(lookupkey);
			//to get personunid and get associated role doc
			String personunid = JSFUtil.DBLookupString("relation",
					"TargetRelation", tmp, 4);
			//get the unid of the new profile name
			try{
			
				View view=securityDb.getView("ProfileView");
				Document newprofiledoc=view.getDocumentByKey(newprofile);
				String newprofileunid=newprofiledoc.getUniversalID();
				//get has_a relation
				String relationId = getRelationDocUnid("HAS_A");
				String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
				Vector tmp1 = new Vector();
				tmp1.add(lookupkey1);
				
				//get the relationship doc which shows the Role association
				View relview=reldb.getView("SourceRelation");
				Document profileassodoc=relview.getDocumentByKey(lookupkey1);
				profileassodoc.replaceItemValue("targetid",newprofileunid);
				profileassodoc.replaceItemValue("trg_data", newprofile);
				profileassodoc.save(true,false);
				
			}catch (Exception e) {
	
			}
		}
	
/**
 
 [It will update the RoleName in the Person-Role association document when Admin change the Rolename for the user]
  
  @return [nothing]
 
@param employeeId
@param oldrole
@param newrole
 */
@SuppressWarnings("unchecked")
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
			
			Document newroledoc=roleView.getDocumentByKey(newrole);
			String newroleunid=newroledoc.getUniversalID();
			//get has_role relation
			String relationId = getRelationDocUnid("HAS_ROLE");
			String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
			Vector tmp1 = new Vector();
			tmp1.add(lookupkey1);
			
			//get the relationship doc which shows the Role association
			View relview=reldb.getView("SourceRelation");
			Document roleassodoc=relview.getDocumentByKey(lookupkey1);
			roleassodoc.replaceItemValue("targetid",newroleunid);
			roleassodoc.replaceItemValue("trg_data", newrole);
			roleassodoc.save(true,false);
			
		}catch (Exception e) {
		}
		
		
	}	
	
}

/**
 
 [It will update the RoleName in the Person-Role association document when Admin change the Rolename for the user]
  
  @return [nothing]
 
@param employeeId
@param newrole
 */
@SuppressWarnings("unchecked")
private void updateAssociatedRole(String employeeId, String newrole){
	
	
		String relDocUnid = getRelationDocUnid("IS_A");
		String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
		Vector tmp = new Vector();
		tmp.add(lookupkey);
		//to get personunid and get associated role doc
		String personunid = JSFUtil.DBLookupString("relation",
				"TargetRelation", tmp, 4);
		//get the unid of the new role name
		
		try{
		
			Document newroledoc=roleView.getDocumentByKey(newrole);
			String newroleunid=newroledoc.getUniversalID();
			//get has_role relation
			String relationId = getRelationDocUnid("HAS_ROLE");
			String lookupkey1 = JSFUtil.getlookupkey(relationId,personunid);
			Vector tmp1 = new Vector();
			tmp1.add(lookupkey1);
			
			//get the relationship doc which shows the Role association
			View relview=reldb.getView("SourceRelation");
			Document roleassodoc=relview.getDocumentByKey(lookupkey1);
			roleassodoc.replaceItemValue("targetid",newroleunid);
			roleassodoc.replaceItemValue("trg_data", newrole);
			roleassodoc.save(true,false);
			
		}catch (Exception e) {
		}		
	
	
}


public void deleteAssoDoc(String employeeId){
	deleteEmpPersonAsso(employeeId);
	deleteProfileDoc(employeeId);
	deleteRoleDoc(employeeId);
	
}

/**
 
 [Delete the Person-Role association document when the Employee is deleted from the view]
  
  @return [nothing]
 
@param employeeId
 */
@SuppressWarnings("unchecked")
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
			//get the relationship doc which shows the Role association
			View relview=reldb.getView("SourceRelation");
			Document roleassodoc=relview.getDocumentByKey(lookupkey1);
			roleassodoc.remove(true);
		}catch (Exception e) {
		}		
	
}



/**
 
 [Delete the Person-Profile association document when the Employee is deleted from the view]
  
  @return [nothing]
 
@param employeeId
 */
@SuppressWarnings("unchecked")
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
					//get the relationship doc which shows the Role association
			View relview=reldb.getView("SourceRelation");
			Document profileassodoc=relview.getDocumentByKey(lookupkey1);
			profileassodoc.remove(true);
		}catch (Exception e) {
		}
		
	
}
	
/**
 
 [To delete the Employee and Person association document when the Employee is deleted by the Admin from the Employee view]
  
  @return [Description of return value]
 
@param employeeId
 */
@SuppressWarnings("unchecked")
private void deleteEmpPersonAsso(String employeeId){

	String relDocUnid = getRelationDocUnid("IS_A");
	String lookupkey = JSFUtil.getlookupkey(relDocUnid,employeeId);
	Vector tmp = new Vector();
	tmp.add(lookupkey);
	//to get personunid and get associated role doc
	try{
			//get the relationship doc which shows the Role association
		View relview=reldb.getView("TargetRelation");
		Document emppersonassodoc=relview.getDocumentByKey(lookupkey);
		emppersonassodoc.remove(true);
	}catch (Exception e) {
	}
	
}


/**
 
 [To get the all the users which is associated with the given RoleName]
  
  @return [Vector containing the names of users]
 
@param RoleName
@return
 */
@SuppressWarnings("unchecked")
public Vector getAssociatedUsers(String RoleName) {
	try {
		// get the rolename unid
		Document roledoc = roleView.getDocumentByKey(RoleName);
		String roleunid = roledoc.getUniversalID();

		// Get HAS_A relationame unid	
		Document reldoc = categoryRelationView.getDocumentByKey("HAS_ROLE");
		String relationid = reldoc.getUniversalID();

		// Do lookup to get the person unids
		String key = JSFUtil.getlookupkey(relationid, roleunid);
		Vector tmp = new Vector();
		tmp.add(key);
		Vector<String> personunid = JSFUtil.DBLookupVector("relation",
				"TargetRelation", tmp, 4);
		
		Vector users = new Vector();
		Document persondoc = null;
		String fname = "";
		for (String x : personunid) {
			persondoc = namesdb.getDocumentByUNID(x);
			fname = persondoc.getItemValueString("FullName");
			users.add(fname);
		}
		return users;

	} catch (Exception e) {

	}

	return null;
}


/**
 
 [To check the Role document field "sharewithpeers" field value]
  
  @return [true if the field value is "1" else it will return "0"]
 
@param RoleName
@return
 */
public boolean isShareDataWithPeers(String RoleName) {
	try {	
		Document roledoc = roleView.getDocumentByKey(RoleName);
		String share = roledoc.getItemValueString("sharewithpeers");
		if (share.equals("1")) {
			return true;
		}
	} catch (Exception e) {
	}
	return false;
}

}
