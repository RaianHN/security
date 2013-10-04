package bsuite.security;

import java.util.HashSet;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import bsuite.relationship.Association;
import bsuite.utility.Utility;



/**
 * To get the associated role name of the current user
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
	
	/**
	 * Role constructor to initialize the current user's associate role name
	 */
	public Role() {
		try{
			currentuser = Utility.getCurrentSession().getEffectiveUserName();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		roleName=as.getAssociatedRoleName(currentuser);
	}

	
	/**
	 Returns the search string, which is called to be included in teh view selection formula
	  @return the view search formula a string
	 */
	@SuppressWarnings({ "unchecked"})
	public String createSearchString(String moduleName, String entityName) {
	
		
		//To get the child roles of a given role
		this.hierarchyRoleList = getFinalRoleList(roleName);
		
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

	
	/**Returns the list of user names by processing roleHierarchy, dataSharedRoles, dataSharedWith peers
	 *@param RoleName role name of the current user
	 *@return list of final users
	 */
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

	/**To get the roles in teh hierarchy
	 *@param roleName current user's role name
	 *@return list of all roles in the hierarchy
	 */
	@SuppressWarnings("unchecked")
	public Vector getFinalRoleList(String roleName) {
		hierarchyRoleList.removeAllElements();
		getRoleList(roleName);
		hierarchyRoleList.removeElementAt(0);
		return hierarchyRoleList;
	}

	/**Returns the roles which has shared data with the given role
	 *@param moduleName module name
	 *@param entityName entity name
	 *@param roleName current role name
	 *@return roles which has shared data with the given role
	 */
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

	/**Recursive function to dig and return all the roles under the hierarchy
	 *@param roleName current role name
	 */
	@SuppressWarnings("unchecked")
	public void getRoleList(String roleName) {

		hierarchyRoleList.add(roleName);
		Vector children = getChildRoles(roleName);
		for (int i = 0; i < children.size(); i++) {
			getRoleList(children.get(i).toString());
		}

	}

	/**Returns all immediate children for the current role
	 *@param roleName current role name
	 *@return list of roles
	 */
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

	
	/**getter for current role
	 *@return current roleName
	 */
	public String getRoleName() {
		
		return roleName;
	}

	/**setter for current role
	 *@param roleName
	 */
	public void setRoleName(String roleName) {

		this.roleName = roleName;
	}

	/**returns the search string for the given module and entity based on the hierarchy and datasharing rules
	 *@param moduleName modulename
	 *@param entityName entityname
	 *@return string 
	 */
	public String getSearchString(String moduleName, String entityName) {
		searchString = createSearchString(moduleName,entityName);
		return searchString;
	}

	/**setter for the search string
	 *@param searchString
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	/**getter for hierarchy role list
	 *@return
	 */
	@SuppressWarnings("unchecked")
	public Vector getHierarchyRoleList() {
		return hierarchyRoleList;
	}

	/**setter for hierarchy role list
	 *@param hierarchyRoleList
	 */
	@SuppressWarnings("unchecked")
	public void setHierarchyRoleList(Vector hierarchyRoleList) {
		this.hierarchyRoleList = hierarchyRoleList;
	}

	/** getter for datashared roles
	 *@return
	 */
	@SuppressWarnings("unchecked")
	public Vector getDataSharedRoles() {
		return dataSharedRoles;
	}

	/**setter for datashared roles
	 *@param dataSharedRoles
	 */
	@SuppressWarnings("unchecked")
	public void setDataSharedRoles(Vector dataSharedRoles) {
		this.dataSharedRoles = dataSharedRoles;
	}

	/**returns the final user list based on datasharing and hierarchy
	 *@return vector of usernames
	 */
	@SuppressWarnings("unchecked")
	public Vector getEffectiveUsersList() {
		return effectiveUsersList;
	}

	
	/**setter for setting effective user's list
	 *@param effectiveUsersList
	 */
	@SuppressWarnings("unchecked")
	public void setEffectiveUsersList(Vector effectiveUsersList) {
		this.effectiveUsersList = effectiveUsersList;
	}

}
