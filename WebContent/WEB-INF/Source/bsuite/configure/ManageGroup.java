package bsuite.configure;

import java.util.ArrayList;
  /**
   * This class will be used in defining the schema by creating group and adding features and entities under that, and a group of actions can be controlled in
   * admin profile interface
  *@author JPrakash
  *@created Feb 13, 2013
 */
public class ManageGroup {
	public static Module addObjToGrp(Module module, String groupName, Object obj) {
		//Used in adding feature or entities to groups in a particular module
		return module;
	}

	public static Module removeObjFrmGrp(Module module, String groupName, Object obj) {
		//Remove entity or feature from group
		return module;
	}
	
	//Group Crud
	public static Module createGroup(Module module, String groupName){
		//Used to create group and add features and entities under each group
		ArrayList<SchemaGroup> groups = null;
		if (module.getGroups() == null) {
			groups = new ArrayList<SchemaGroup>();//If no groups, create new list
		} else {
			groups = module.getGroups();
		}

			SchemaGroup grp = new SchemaGroup();//Create a group object and set its name
			grp.setGroupName(groupName);
			groups.add(grp);					
		module.setGroups(groups);
		return module;
	}
	public static Module renameGroup(Module module, SchemaGroup group, String newName){
		ArrayList<SchemaGroup> groups = module.getGroups();
		if(groups==null){
			return module;
		}
		int index = groups.indexOf(group);
		if(index<0){
			return module;
		}
		SchemaGroup grp = groups.get(index);
		if(grp==null){
			return module;
		}
		grp.setGroupName(newName);		
		return module;
	}
	public static boolean deleteGroup(Module module, String groupName){
		
		return true;
	}
	
	public static Module addEntFeat(Module module, Object obj){
		//to add in entFeat list of the module when a new feature or entity is added
		ArrayList<Object> list = null;
		if(module.getEntFeat()==null){
			list = new ArrayList<Object>();
		}else{
			list = module.getEntFeat();
		}
		list.add(obj);
		module.setEntFeat(list);		
		return module;
	}
	public static boolean removeEntFeat(){
		//to remove from entFeat list of the module when a new feature or entity is removed
	
		return true;
	}
	
}
