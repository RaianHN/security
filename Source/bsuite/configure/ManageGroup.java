package bsuite.configure;

import java.util.ArrayList;
import java.util.Vector;
  /**
   * This class will be used in defining the schema by creating group and adding features and entities under that, and a group of actions can be controlled in
   * admin profile interface
  *@author JPrakash
  *@created Feb 13, 2013
 */
public class ManageGroup {
	/**
	 * Used in adding feature or entities to groups in a particular module
	 * 1 Add obj to given group
	 * 2 Remove obj from entFeat list of this module
	 *@param module module object
	 *@param group group object
	 *@param str feature
	 *@return module object
	 */
	public static Module addObjToGrp(Module module, SchemaGroup group, String str) {
		//Used in adding feature or entities to groups in a particular module
		
		//1 Add obj to given group
		//2 Remove obj from entFeat list of this module
		
		ArrayList<String> GroupEntries = null;
		
		if(group!=null){
			GroupEntries = group.getGroupEntries();
		}else{
			return module; //If no group return;
		}
		
		if(GroupEntries==null){
			GroupEntries = new ArrayList<String>();
		}
		
		GroupEntries.add(str);
		group.setGroupEntries(GroupEntries);
		
		removeEntFeat(module, str,"grp");
		
		return module;
	}

	/**Remove entity or feature from group
	 * Add the obj to entFeat list
	 *@param module
	 *@param group
	 *@param str
	 *@return
	 */
	/**Remove entity or feature from group
	 * Add the obj to entFeat list
	 *@param module moduel object
	 *@param group group object
	 *@param str feature name
	 *@return module object
	 */
	public static Module removeObjFrmGrp(Module module, SchemaGroup group, String str) {
		//Remove entity or feature from group
		//Add the obj to entFeat list
		
		ArrayList<String> GroupEntries = null;
		
		if(group!=null){
			GroupEntries = group.getGroupEntries();
		}else{
			return module; //If no group return;
		}
		
		GroupEntries.remove(str);
		group.setGroupEntries(GroupEntries);
		
		addEntFeat(module, str);
		
		return module;
	}
	
	//Group Crud
	/**Used to create group and add features and entities under each group
	 *@param module module object
	 *@param groupName group name
	 *@return module object
	 */
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
	/**Used to rename the group 
	 *@param module module object
	 *@param group group 
	 *@param newName new name
	 *@return module object
	 */
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
	/**Used to delete th grou; from the module schema
	 *@param module module object
	 *@param groupName group name
	 *@return module object
	 */
	public static Module deleteGroup(Module module, String groupName){
		ArrayList<SchemaGroup> groups = module.getGroups();
		ArrayList<String> grpEntries=null;
		
		if(groups==null){
			return module;
		}
		
		for(SchemaGroup sg:groups){
			if(sg!=null){
				if(sg.getGroupName().equals(groupName)){
					groups.remove(sg);
					grpEntries = sg.getGroupEntries();
					break;
				}
				
			
			}
			
		}
		
		ArrayList<String> entFeat = module.getEntFeat();
		
		if(entFeat!=null){
			if(grpEntries!=null){
			entFeat.addAll(grpEntries);
			}
			module.setGroups(groups);
		}
		
		
		
		return module;
	}
	
	/**To add in entFeat list of the module when a new feature or entity is added
	 *@param module module object
	 *@param str feature name
	 *@return module object
	 */
	public static Module addEntFeat(Module module, String str){
		//to add in entFeat list of the module when a new feature or entity is added
		ArrayList<String> list = null;
		if(module.getEntFeat()==null){
			list = new ArrayList<String>();
		}else{
			list = module.getEntFeat();
		}
		list.add(str);
		module.setEntFeat(list);		
		return module;
	}
	/**to remove from entFeat list of the module when a new feature or entity is removed.
	 * If from group remove from entity list else if remove is called from removeEntity remove from entList and from group
	 *@param module
	 *@param str
	 *@param grp
	 *@return
	 */
	public static Module removeEntFeat(Module module, String str, String grp){
		//to remove from entFeat list of the module when a new feature or entity is removed.
		//If from group remove from entity list else if remove is called from removeEntity remove from entList and from group
		ArrayList<String> list = null;
		if(module.getEntFeat()==null){
			return module;
		}else{
			list = module.getEntFeat();
		}
		if(grp.equals("grp")){
			list.remove(str);
		}
		else{
			if(!list.remove(str)){
				removeEntFeatFromGrp(module,str);
			}
		}
		
		return module;
	}
	/**Returns the list of feature and entity names, if it is entity e: will be appended, if it is feature f: will be appended
	 *@param group group object
	 *@return entry names as list
	 */
	public static ArrayList<String> getAllEntryNames(SchemaGroup group){
		//Returns the list of feature and entity names, if it is entity e: will be appended, if it is feature f: will be appended
		ArrayList<String> list = group.getGroupEntries();
		
		
		if(list==null){
			return null;
		}
		
		
				
		return list;
		
	}
	/**Returns the list of all entity and features in entFeat list in module
	 *@param module module object
	 *@return list of features
	 */
	public static Vector<String> getEntFeatList(Module module){
		//Returns the list of all entity and features in entFeat list in module
		
		Vector<String>entryNames = new Vector<String>();
		ArrayList<String> list = module.getEntFeat();
		if(list==null){
			return null;
		}

		for(Object o:list){
			if(o instanceof Entity){
				entryNames.add("e:"+((Entity) o).getEntityName());
			}else if(o instanceof Feature){			
				entryNames.add("f:"+((Feature) o).getFeatureName());
			}
		}
				
		return entryNames;
	}
	/**Removes the entFeat from any groups if the entFeat is not found in entList when an entity or feature is removed from module
	 *@param module module object
	 *@param entFeat feature name
	 *@return moduleobject
	 */
	private static Module removeEntFeatFromGrp(Module module, String entFeat){
		//Removes the entFeat from any groups if the entFeat is not found in entList when an entity or feature is removed from module
		ArrayList<String> list = null;
		ArrayList<SchemaGroup> grps = module.getGroups();
		if(grps==null) return module;
		
		for(SchemaGroup sg:grps){
			if(sg!=null){
				list = sg.getGroupEntries();
				if(list!=null){
					if(list.remove(entFeat)){
						return module;
					}
					
				}
			}
		}
		
		return module;
	}
}
