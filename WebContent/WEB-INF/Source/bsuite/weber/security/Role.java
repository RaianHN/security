package bsuite.weber.security;

import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.tools.JSFUtil;

public class Role extends BsuiteWorkFlow {
	
	String roleName;
	String searchString;
	Vector<String> hierarchyRoleList=new Vector<String>();
	Vector<String> dataSharedRoles=new Vector<String>();
	Vector<String> effectiveUsersList=new Vector<String>();
	Association as=new Association();
	
	
	public Role(){
		//initialize associated role for the current user
		
			roleName=as.getAssociatedRoleName(this.currentuser.getBsuiteuser());
		if(roleName!=null){
			searchString=createSearchString();
			System.out.println("Search String"+searchString);
		}
		
		
	}

	public String createSearchString(){		
		//viewScope.put("entityName", "Activity");
		Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");	
		String moduleName=(String)viewScope.get("moduleName");
		String entityName=(String)viewScope.get("entityName");
		this.hierarchyRoleList= getFinalRoleList(roleName);//to get the child roles of the given role
		this.dataSharedRoles=getRolesFromDataSharingRules(moduleName,entityName,roleName);//pass current entity the uesr is accessing and roleName
		this.effectiveUsersList=getFinalUserList(roleName);
		System.out.println("Hierarchical Roles String"+hierarchyRoleList);
		System.out.println("Data Shared String"+dataSharedRoles);
		System.out.println("Effective String"+effectiveUsersList);
		
		Vector<String> a = new Vector();	
		String username;	
		for(String x:effectiveUsersList){
			username=as.getFormattedName(x, "common");
			a.add(username);
		}
		
		String currentuser1= as.getFormattedName(this.currentuser.getBsuiteuser(), "common");
		a.add(currentuser1);
		
		String querystr="(FIELD CreatedBy=";			
		for(int i=0;i<a.size();i++){
			if(i==0){
				querystr=querystr+"\""+ a.get(i)  +"\" " ;

			}else{
				querystr=querystr+" OR "+"\""+ a.get(i)  +"\"" ;
			}							
		}							
		querystr=querystr+")";
		
		return querystr;
	}

	
	public Vector getFinalUserList(String RoleName){
		Vector finallist=new Vector();
		System.out.println("in getFinalUserList");
		Vector userslist=new Vector();				

		for (String x : hierarchyRoleList) {			
			userslist.addAll(getAssociatedUsers(x));
		}				
		
		//Users from DataSharingRules
		Vector dsusers=new Vector();
		for (String x :dataSharedRoles ) {	
			dsusers.addAll(getAssociatedUsers(x));
			
		}
		System.out.println("DataSharing Rules UserList "+dsusers);
		//To remove all the duplicated values
		HashSet userset=new HashSet();
		userset.addAll(userslist);
		userset.addAll(dsusers);
		finallist.addAll(userset);
		
		 return finallist;
	}
	
	public Vector getFinalRoleList(String roleName){
		hierarchyRoleList.removeAllElements();
		getRoleList(roleName);
		hierarchyRoleList.removeElementAt(0);
		return hierarchyRoleList;
	}
		
	public Vector getRolesFromDataSharingRules(String moduleName,String entityName,String roleName){
		System.out.println("inside DataSharing function");
		System.out.println("RoleName "+roleName);
		Vector roles = new Vector();
		
		try{
			Database security = session.getDatabase("", bsuitepath+"Security.nsf");
			View datasharingView = security.getView("DataSharingView");
			String key=moduleName+"+"+entityName+"+"+roleName;
			System.out.println("key "+key);
			DocumentCollection dc=datasharingView.getAllDocumentsByKey(key);
			//Vector<String> res=JSFUtil.DBLookupVector("Security","DataSharingView",rolename,1);
			System.out.println("Data Sharing roles size "+dc.getCount());
			Document doc = dc.getFirstDocument();
			String sourceRole="";
			for(int i=0;i<dc.getCount();i++){
				sourceRole = doc.getItemValueString("SourceRole");
				System.out.println("Source role name"+sourceRole);
				roles.add(sourceRole);				
				doc = dc.getNextDocument(doc);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		return roles;
	}
	
public void getRoleList(String roleName){
	
	System.out.println("in getRoleList"+roleName);	
	hierarchyRoleList.add(roleName);
	System.out.println("in getRoleList 2"+hierarchyRoleList.get(0));
	Vector children = getChildRoles(roleName);
	for(int i=0;i<children.size();i++){
		getRoleList(children.get(i).toString());
	}
			
}

public Vector getChildRoles(String roleName){
	Vector roles = new Vector();
	try {
		Database security = session.getDatabase("", bsuitepath+"Security.nsf");
		View rolesView = security.getView("RolesViewCat");
		DocumentCollection coll = rolesView.getAllDocumentsByKey(roleName);
		Document doc = coll.getFirstDocument();
		String rolename="";
		for(int i=0;i<coll.getCount();i++){
			rolename = doc.getItemValueString("role_name");
			System.out.println("role name"+rolename);
			roles.add(rolename);
			
			doc = coll.getNextDocument(doc);
		}
		return roles;
	} catch (NotesException e) {
		
		e.printStackTrace();
	}
	return roles;
}



public Vector getAssociatedUsers(String RoleName){
	try{
		System.out.println("in get associated users");
		//get the rolename unid
		Database security = session.getDatabase("", bsuitepath+"Security.nsf");
		View roleView=security.getView("RolesView");
		Document roledoc=roleView.getDocumentByKey(RoleName);
		String roleunid=roledoc.getUniversalID();
		
		//Get HAS_A relationame unid
		Database relationDb = session.getDatabase("", bsuitepath+"Relation.nsf");
		View relview=relationDb.getView("CategoryRelation");
		Document reldoc= relview.getDocumentByKey("HAS_ROLE");
		String relationid=reldoc.getUniversalID();
		
		//Do lookup to get the person unids
		String key=JSFUtil.getlookupkey(relationid,roleunid);
		Vector tmp=new Vector();			
		tmp.add(key);
		Vector<String> personunid=JSFUtil.DBLookupVector("relation","TargetRelation",tmp,4);
		Database namesdb = session.getDatabase("", "names.nsf");
	
		Vector users=new Vector();
		Document persondoc=null;
		String fname="";
		for (String x : personunid) {
		persondoc=namesdb.getDocumentByUNID(x);
		fname=persondoc.getItemValueString("FullName");
		users.add(fname);
		}
		System.out.println("users "+users);
		return users;
		
	}catch (Exception e) {
		
	}
	
	return null;
}

public String getRoleName() {
	return roleName;
}


public void setRoleName(String roleName) {
	this.roleName = roleName;
}


public String getSearchString() {
	return searchString;
}


public void setSearchString(String searchString) {
	this.searchString = searchString;
}


public Vector getHierarchyRoleList() {
	return hierarchyRoleList;
}


public void setHierarchyRoleList(Vector hierarchyRoleList) {
	this.hierarchyRoleList = hierarchyRoleList;
}


public Vector getDataSharedRoles() {
	return dataSharedRoles;
}


public void setDataSharedRoles(Vector dataSharedRoles) {
	this.dataSharedRoles = dataSharedRoles;
}


public Vector getEffectiveUsersList() {
	return effectiveUsersList;
}


public void setEffectiveUsersList(Vector effectiveUsersList) {
	this.effectiveUsersList = effectiveUsersList;
}


}
