package bsuite.configure;

import java.util.ArrayList;


public class Module {
	
	private String moduleName;
	private ArrayList<Entity> entities;
	private ArrayList<Feature> features;
	private ArrayList<SchemaGroup> groups;
	private ArrayList<Object> entFeat;//contains all the entities and features, used to mange group of actions
	
	public ArrayList<Object> getEntFeat() {
		return entFeat;
	}

	public void setEntFeat(ArrayList<Object> entFeat) {
		this.entFeat = entFeat;
	}

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

}
