package bsuite.security;

import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import bsuite.relationship.Association;
import bsuite.utility.*;

import bsuite.utility.Utility;



/**
[Class description-- To get the user's associated rolename]
   
  @author TSangmo
  @created On Aug 8, 2012
 */
public class Role {

	private String roleName;
	private String searchString;
	private Vector<String> hierarchyRoleList = new Vector<String>();
	private Vector<String> dataSharedRoles = new Vector<String>();
	private Vector<String> effectiveUsersList = new Vector<String>();
	private Association as = new Association();
	private String currentuser;
	public Role() {
		try{
			currentuser = Utility.getCurrentSession().getEffectiveUserName();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		roleName=as.getAssociatedRoleName(currentuser);
	}

	
	/**
	 
	 [this is called from getmydocs() in workspace to create the search String to be applied on view]
	  
	  @return [It will return search string]
	 
	@return
	 */
	@SuppressWarnings({ "unchecked"})
	public String createSearchString() {
		Map viewScope = (Map) JSFUtil.getVariableValue("viewScope");
		String moduleName = (String) viewScope.get("moduleName");
		String entityName = (String) viewScope.get("entityName");
		System.out.println("roleNam "+roleName);
		this.hierarchyRoleList = getFinalRoleList(roleName);// to get the child
															// roles of the
															// given role
		this.dataSharedRoles = getRolesFromDataSharingRules(moduleName,
				entityName, roleName);// pass current entity the uesr is
										// accessing and roleName
		this.effectiveUsersList = getFinalUserList(roleName);
		Vector<String> a = new Vector();
		String username;
		for (String x : effectiveUsersList) {
			username = as.getFormattedName(x, "common");
			a.add(username);
		}

		String currentuser1 = as.getFormattedName(this.currentuser, "common");
		a.add(currentuser1);

		String querystr = "(FIELD CreatedBy=";
		for (int i = 0; i < a.size(); i++) {
			if (i == 0) {
				querystr = querystr + "\"" + a.get(i) + "\" ";

			} else {
				querystr = querystr + " OR " + "\"" + a.get(i) + "\"";
			}
		}
		querystr = querystr + ")";
		querystr = querystr + " OR FIELD shareWithUser contains " + "\""
				+ as.getFormattedName(this.currentuser, "abr") + "\""
				+ " OR FIELD shareWithRoles contains " + "\"" + this.roleName
				+ "\"";
		return querystr;
	}

	@SuppressWarnings({ "unchecked"})
	public Vector getFinalUserList(String RoleName) {
		Vector finallist = new Vector();
		Vector userslist = new Vector();

		for (String x : hierarchyRoleList) {
			userslist.addAll(as.getAssociatedUsers(x));
		}

		// Users from DataSharingRules
		Vector dsusers = new Vector();
		for (String x : dataSharedRoles) {
			dsusers.addAll(as.getAssociatedUsers(x));

		}

		// Check whether the shareDataWithPeers is set or not in the Role
		// Document
		Boolean sharedata = as.isShareDataWithPeers(RoleName);
		Vector peersname = new Vector();

		// To remove all the duplicated values
		HashSet userset = new HashSet();
		userset.addAll(userslist);
		userset.addAll(dsusers);

		// if sharedata is true, then add usersname to the final users list
		if (sharedata) {
			
			peersname.addAll(as.getAssociatedUsers(RoleName));
			userset.addAll(peersname);
		}

		finallist.addAll(userset);

		return finallist;
	}

	@SuppressWarnings("unchecked")
	public Vector getFinalRoleList(String roleName) {
		hierarchyRoleList.removeAllElements();
		getRoleList(roleName);
		hierarchyRoleList.removeElementAt(0);
		return hierarchyRoleList;
	}

	@SuppressWarnings("unchecked")
	public Vector getRolesFromDataSharingRules(String moduleName,
			String entityName, String roleName) {
		Vector roles = new Vector(); 

		try {
			
			Database security = Utility.getDatabase("Security.nsf");
			View datasharingView = security.getView("DataSharingView");
			String key = moduleName + "+" + entityName + "+" + roleName;
			DocumentCollection dc = datasharingView.getAllDocumentsByKey(key);
			Document doc = dc.getFirstDocument();
			String sourceRole = "";
			for (int i = 0; i < dc.getCount(); i++) {
				sourceRole = doc.getItemValueString("SourceRole");
				roles.add(sourceRole);
				doc = dc.getNextDocument(doc);
			}

		} catch (Exception e) {
		}

		return roles;
	}

	@SuppressWarnings("unchecked")
	public void getRoleList(String roleName) {

		hierarchyRoleList.add(roleName);
		Vector children = getChildRoles(roleName);
		for (int i = 0; i < children.size(); i++) {
			getRoleList(children.get(i).toString());
		}

	}

	@SuppressWarnings("unchecked")
	public Vector getChildRoles(String roleName) {
		Vector roles = new Vector();
		try {
			Database security = Utility.getDatabase("Security.nsf");
			View rolesView = security.getView("RolesViewCat");
			DocumentCollection coll = rolesView.getAllDocumentsByKey(roleName);
			Document doc = coll.getFirstDocument();
			String rolename = "";
			for (int i = 0; i < coll.getCount(); i++) {
				rolename = doc.getItemValueString("role_name");
				roles.add(rolename);

				doc = coll.getNextDocument(doc);
			}
			return roles;
		} catch (NotesException e) {

			e.printStackTrace();
		}
		return roles;
	}

	public String getRoleName() {
		
		return roleName;
	}

	public void setRoleName(String roleName) {

		this.roleName = roleName;
	}

	public String getSearchString() {
		searchString = createSearchString();
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@SuppressWarnings("unchecked")
	public Vector getHierarchyRoleList() {
		return hierarchyRoleList;
	}

	@SuppressWarnings("unchecked")
	public void setHierarchyRoleList(Vector hierarchyRoleList) {
		this.hierarchyRoleList = hierarchyRoleList;
	}

	@SuppressWarnings("unchecked")
	public Vector getDataSharedRoles() {
		return dataSharedRoles;
	}

	@SuppressWarnings("unchecked")
	public void setDataSharedRoles(Vector dataSharedRoles) {
		this.dataSharedRoles = dataSharedRoles;
	}

	@SuppressWarnings("unchecked")
	public Vector getEffectiveUsersList() {
		return effectiveUsersList;
	}

	@SuppressWarnings("unchecked")
	public void setEffectiveUsersList(Vector effectiveUsersList) {
		this.effectiveUsersList = effectiveUsersList;
	}

}
