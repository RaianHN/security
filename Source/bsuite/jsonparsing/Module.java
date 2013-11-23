package bsuite.jsonparsing;

import java.util.ArrayList;

 /**Used to maintain the permissions of a module and is stored in the profile document, contains getter and setters
  * for the module its entities, features and group
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class Module {

	private String moduleName;
	private String tabvis;
	private ArrayList<Entity> entities;
	private ArrayList<Feature> features;
	private ArrayList<GroupPermission> groups;
	
	
	
	public ArrayList<Entity> getEntities() {
		return entities;
	}
	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getTabvis() {
		return tabvis;
	}
	public void setTabvis(String tabvis) {
		this.tabvis = tabvis;
	}
	
	public ArrayList<Feature> getFeatures() {
		return features;
	}


	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	public ArrayList<GroupPermission> getGroups() {
		return groups;
	}
	public void setGroups(ArrayList<GroupPermission> groups) {
		this.groups = groups;
	}
	
	
	
	
}
