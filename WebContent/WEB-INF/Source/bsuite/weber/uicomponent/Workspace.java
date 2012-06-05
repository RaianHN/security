package bsuite.weber.uicomponent;

import java.util.Vector;

import javax.faces.component.UIComponent;

import lotus.domino.NotesException;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;

import bsuite.weber.relationship.Association;
import bsuite.weber.tools.CompUtil;

public class Workspace {
	private  String BEAN_NAME;
	private UIComponent test;
	
	

	public UIComponent getTest() {
		test = new UIPanelEx();
		XspTable utable = new XspTable();
		Vector vec = new Vector();
		vec.add("field1");
		vec.add("field2");
		vec.add("field3");
		vec.add("field4");
		vec.add("field5");
		vec.add("field6");
		vec.add("field7");
		vec.add("field8");
		loadFields(vec,utable,0);
		return test;
	}

	public void setTest(UIComponent test) {
		this.test = test;
	}

	public void createView(String entityName){
		
	}
	//When entity schema is defined we need to select which fields should be added as columns in the view we create using that data we need to
	//add columns in the view we create for this entity

	public void createReadForm(String entityName){
		Association asstn = new Association();
		if(!asstn.getParentEntity(entityName).equals("")){
			//Create fields for associated entity and tie it to the bean
		}
		
	}
	public void createEditForm(String entityName){
		
	}
	public void createFeatureButtons(String entityName){
		
	}
	//For this entity what features are available that needs to be populated by this method

	private void loadFields(Vector fields,XspTable table,int startRow){
		//This function is used to populate the fields in the given table
		//fields:list of fields
		//table: table component where field label and field will be added
		//startRow: the starting row number of the table where the fields needs to be added
		for (int i = 0; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);
				
				switch (j) {
				case 0:
					CompUtil.createLabel(cell,fields.get(i).toString(),true);
					break;
				case 1:
				
					//CompUtil.createInput(cell, fields.get(i).toString(),BEAN_NAME);
					break;
				}
			}
		}
		test.getChildren().add(table);	
	}
	
}
