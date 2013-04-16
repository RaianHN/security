package bsuite.jsonparsing;

import java.util.ArrayList;

public class Entity{
	
	
	
	private String entityName;
	private String create;
	private String read;
	private String update;
	private String delete;
	private ArrayList<Field> fields;
	private ArrayList<EntityAction>actions;
	
	private String accessType;
	//Added for fields and features perms

	
	
	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getCreate() {
		return create;
	}

	public void setCreate(String create) {
		this.create = create;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getDelete() {
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	
		
	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
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







	

}