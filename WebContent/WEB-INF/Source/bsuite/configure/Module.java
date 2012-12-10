package bsuite.configure;

import java.util.ArrayList;


public class Module {
	
	private String moduleName;
	private ArrayList<Entity> entities;
	private ArrayList<Feature> features;
	
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
