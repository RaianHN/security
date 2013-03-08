package bsuite.configure;

import java.util.ArrayList;

public class Entity {
	private String entityName;
	
	private ArrayList<Field> fields;
	private ArrayList<EntityAction> actions;
	
	public ArrayList<Field> getFields() {
		return fields;
	}
	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}
	
	public ArrayList<EntityAction> getActions() {
		return actions;
	}
	public void setActions(ArrayList<EntityAction> actions) {
		this.actions = actions;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
