package bsuite.weber.uicomponent;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import lotus.domino.NotesException;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.model.domino.DominoViewData;
import com.ibm.xsp.util.ManagedBeanUtil;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;
import bsuite.weber.tools.StringUtil;

public class Workspace extends BsuiteWorkFlow implements Serializable {
	private String BEAN_NAME="employee";
	private  UIComponent test;
	private static String m_documentId;
	
private UIPanelEx test1;
private UIPanelEx testview;

public UIComponent getTest1(){
	Map requestscope = (Map) JSFUtil.getVariableValue("requestScope");	
	m_documentId=null;
	test1 = new UIPanelEx();
	ArrayList<String> fields=this.getFields();
	System.out.println("Fields Name "+fields);
	
	
	if(requestscope.get("employee")!=null){
		resetBean();
	}
	//resetBean();
		
	
	
	XspTable utable = new XspTable();

	utable.setId( "customDatatable" );

	int i=1;

	System.out.println("Field size "+fields.size());
	for(String v:fields){

		System.out.println(v);
		if(!((v.equals("Form"))||(v.equals("$UpdatedBy"))||(v.equals("$Revisions"))||(v.equals("display_field")))){
			v= StringUtil.removechar(v,"$");

			XspTableRow row = CompUtil.createRow(utable, "row"+i);
			for (int j=0 ; j<2; j++)
			{	
				XspTableCell cell = CompUtil.createCell(row, "cell"+i+j);
				switch(j)
				{
				case 0: CompUtil.createLabel(cell,v);
				break;
				case 1: CompUtil.createInput(cell, v,BEAN_NAME);
				break;
				}
			}	 			 

		}
		i++;
	}		 		

	
	
	
	//UIPanelEx panel=(UIPanelEx)loadReadFields(fields,utable);
	//System.out.println("Afdter the laodReadFields");
	test1.getChildren().add(utable);
	return test1;
}
	

	public UIComponent getTest() {
		System.out.println("Inside getTest()");
		test = new UIPanelEx();
		UIDojoTabContainer tabcont=(UIDojoTabContainer)CreateTabTable.createTT(test);
		Vector <String>mnames=this.getModulesName();
		System.out.println("Module Names "+mnames);
		
		
		for(String x:mnames){
			System.out.println("Module Names "+x);
			
			UIDojoTabPane pane2=(UIDojoTabPane)CreateTabTable.createTabPane(tabcont,x);
			
			/*if(x.equals("Employees")){
				//added by Tenzing just for testing
				System.out.println("Inside when mdoule is Employee");
				Vector fields=this.getFields();
				System.out.println("Fields Name "+fields);
				XspTable utable = new XspTable(); 
				UIPanelEx panel=(UIPanelEx)loadReadFields(fields,utable);
				System.out.println("Afdter the laodReadFields");
				pane2.getChildren().add(panel);
			}*/
		}
	

	/*
		 * XspTable utable = new XspTable(); Vector vec = new Vector();
		 * vec.add("field1"); vec.add("field2"); vec.add("field3");
		 * vec.add("field4"); vec.add("field5"); vec.add("field6");
		 * vec.add("field7"); vec.add("field8"); loadReadFields(vec,utable,0);
		 */
		return test;
	}

	public void setTest(UIComponent test) {
		this.test = test;
	}

	public void createView(String entityName) {

	}

	// When entity schema is defined we need to select which fields should be
	// added as columns in the view we create using that data we need to
	// add columns in the view we create for this entity

	public void createReadForm(String entityName) {
		Association asstn = new Association();
		if (!asstn.getParentEntity(entityName).equals("")) {
			// Create fields for associated entity and tie it to the bean
		}
		// Load the fields here
	}

	public void createEditForm(String entityName) {

	}

	public void createFeatureButtons(String entityName) {
		// For this entity what features are available that needs to be
		// populated by this method
	}

	private UIComponent loadReadFields(Vector fields, XspTable table, int startRow) {
	
		// This function is used to populate the fields in the given table
		// fields:list of fields
		// table: table component where field label and field will be added
		// startRow: the starting row number of the table where the fields needs
		// to be added
		
		
		for (int i = 1; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);

				switch (j) {
				case 0:
					CompUtil.createLabel(cell, fields.get(i).toString(), true);
					break;
				case 1:

					CompUtil.createInput(cell, fields.get(i).toString(),
							BEAN_NAME);
					break;
				}
			}
		}
		return table;
		
		// test.getChildren().add(table);
	}


	
	
	//for testing to get modules names
public Vector getModulesName(){
	/*
	Application application = context.getApplication();
	MethodBinding myMethodExpression = application.createMethodBinding(
			"#{security.getModules}", null);
	Object myMethodReturn = myMethodExpression.invoke(context, null);

	System.out.println("Modules Name " + myMethodReturn);
	viewScope.put("modulesName", myMethodReturn);
	Vector mname=(Vector)viewScope.get("modulesName");
	*/
	
	Vector modules=new Vector();
	modules.add("Employees");
	modules.add("Documents");
	//modules.add("InOut");
	
	return modules;
	
}

//To get fields name.. just for testing


public ArrayList getFields(){
	ArrayList<String> fnames=new ArrayList();
	fnames.add("Name");
	fnames.add("Salary");
	fnames.add("Emergency");
	return fnames;
	
}


private UIComponent loadReadFields(Vector fields, XspTable table) {
	//, int startRow
	// This function is used to populate the fields in the given table
	// fields:list of fields
	// table: table component where field label and field will be added
	// startRow: the starting row number of the table where the fields needs
	// to be added
System.out.println("inside loadReadFields");
	UIPanelEx panel=new UIPanelEx();
	for (int i = 0; i < fields.size(); i++) {
		XspTableRow row1 = CompUtil.createRow(table, "row" + i);
		for (int j = 0; j < 2; j++) {
			XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);

			switch (j) {
			case 0:
				CompUtil.createLabel(cell, fields.get(i).toString(), true);
				break;
			case 1:
				
				System.out.println("in case1 BeanName "+BEAN_NAME);
				CompUtil.createInput(cell, fields.get(i).toString(),
						BEAN_NAME);
				break;
			}
		}
	}
	panel.getChildren().add(table);
	return panel;
	
	// test.getChildren().add(table);
}

public void getTestview(){
	testview= new UIPanelEx();
	UIPanelEx vpanel=new UIPanelEx();
	XspViewPanel viewPanel=CompUtil.createViewpanel(vpanel,"viewPanel1", "viewEntry");
	 DominoViewData data = CompUtil.createDominoViewData("Demo2\\user.nsf","userprofile","view1");
     data.setComponent(viewPanel);   
     viewPanel.setData(data);
	
}
public void resetUI(UIComponent com,FacesContext context,String page){
	CompUtil.create(com, context, page);
}

public void resetBean(){
	
	System.out.println("calling reset");
	Object user = ManagedBeanUtil.getBean(context,BEAN_NAME);
	try {
		Class<?> c= user.getClass();
		System.out.println("class "+c);
		Method m = c.getMethod("reset",null);
		m.invoke(user, null);
		
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (NoSuchMethodException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	//FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(BEAN_NAME,user.getClass().newInstance()); 
	//if(!(user.equals(null))){System.out.println(user.getClass().toString());}
catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

}
