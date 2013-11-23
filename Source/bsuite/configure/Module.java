package bsuite.configure;

import java.util.ArrayList;


 /**Module class used to define the module schema in the module document, contains getters and setters for
  * entities, features, groups
  *@author JPrakash
  *@created Oct 9, 2013
 */
public class Module {
	
	

	private String moduleName;
	private ArrayList<Entity> entities;
	private ArrayList<Feature> features;
	private ArrayList<SchemaGroup> groups;
	private ArrayList<String> entFeat;//contains all the entities and features, used to mange group of actions
	
	

	public ArrayList<SchemaGroup> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<SchemaGroup> groups) {
		this.groups = groups;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	


	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}
	
	public ArrayList<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	
	public ArrayList<String> getEntFeat() {
		return entFeat;
	}

	public void setEntFeat(ArrayList<String> entFeat) {
		this.entFeat = entFeat;
	}

}
