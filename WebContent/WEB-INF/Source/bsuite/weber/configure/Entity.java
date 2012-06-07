package bsuite.weber.configure;

import java.util.ArrayList;

public class Entity {
	private String entityName;
	
	private ArrayList<Field> fields;
	private ArrayList<Feature> features;
	
	public ArrayList<Field> getFields() {
		return fields;
	}
	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}
	public ArrayList<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
