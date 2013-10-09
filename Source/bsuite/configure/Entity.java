package bsuite.configure;

import java.util.ArrayList;

 /**This class is hols the schema of the entity in the module document, contains only getter and setter methods
  * for fields and actions in the entity
  *@author JPrakash
  *@created Oct 9, 2013
 */
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
