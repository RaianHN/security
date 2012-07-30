package bsuite.weber.jsonparsing;

import java.util.ArrayList;

public class Entity{
	
	
	
	private String entityName;
	private String create;
	private String read;
	private String update;
	private String delete;
	private ArrayList<Field> fields;
	
	private String accessType;
	//Added for fields and features perms
	//private ArrayList<FieldPerm> fieldPerm;
	//private ArrayList<FeaturePerm> featurePerm;
	
	
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



/*	public ArrayList<FieldPerm> getFieldPerm() {
		return fieldPerm;
	}

	public void setFieldPerm(ArrayList<FieldPerm> fieldPerm) {
		this.fieldPerm = fieldPerm;
	}

	public ArrayList<FeaturePerm> getFeaturePerm() {
		return featurePerm;
	}

	public void setFeaturePerm(ArrayList<FeaturePerm> featurePerm) {
		this.featurePerm = featurePerm;
	}*/



	

}