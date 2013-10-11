package bsuite.configure;

import java.util.ArrayList;

 /**Used to define the role hierarchy in the role hierarchy document
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class RoleHierarchy {
	private ArrayList<Role> roleList;

	public ArrayList<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(ArrayList<Role> roleList) {
		this.roleList = roleList;
	}

}

 class Role {
	private String RoleName;
	private String RoleParent;

	public String getRoleName() {
		return RoleName;
	}

	public void setRoleName(String roleName) {
		RoleName = roleName;
	}

	public String getRoleParent() {
		return RoleParent;
	}

	public void setRoleParent(String roleParent) {
		RoleParent = roleParent;
	}
}