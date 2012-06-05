package bsuite.weber.uicomponent;

import bsuite.weber.relationship.Association;

public class Workspace {
	public void createView(String entityName){
		
	}
	//When entity schema is defined we need to select which fields should be added as columns in the view we create using that data we need to
	//add columns in the view we create for this entity

	public void createReadForm(String entityName){
		if(!getParentEntity(entityName).equals("")){
			
		}
		
	}
	public void createEditForm(String entityName){
		
	}
	public void createFeatureButtons(String entityName){
		
	}
	//For this entity what features are available that needs to be populated by this method

	private String getParentEntity(String entityName){	
		Association association = new Association();
		String entityUnid = association.getEntityUnid(entityName);
		
		return "";
	}
}
