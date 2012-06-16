package bsuite.weber.uicomponent;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import lotus.domino.NotesException;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.UISelectItemsEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.util.ManagedBeanUtil;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.security.Profile;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;

public class Workspace extends BsuiteWorkFlow implements Serializable{
	private  String BEAN_NAME="employee";
	private UIComponent tabs;
	private UIComponent centity;
	
	

	
	
	public UIComponent getCentity() {
		centity = new UIPanelEx();
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		String pid = (String) viewscope.get("personunid");		
		String moduleName = (String)viewscope.get("moduleName");
		String entityName = (String)viewscope.get("entityName");
		if(moduleName!=null & entityName!=null){
			createEditForm(moduleName,entityName);
		}
		return centity;
	}

	public void setCentity(UIComponent centity) {
		this.centity = centity;
	}

	public UIComponent getTabs() {
		System.out.println("Inside getTest()");
		tabs = new UIPanelEx();
		UIDojoTabContainer tabcont=(UIDojoTabContainer)CreateTabTable.createTT(tabs);
		ArrayList <String>mnames=(ArrayList<String>) JSFUtil.getBindingValue("#{security.modules}");
		if(mnames==null){
			System.out.println("if mnmes is null");
			return tabs;
		}
		HashMap<String, ArrayList<String>> entity=(HashMap<String, ArrayList<String>>) JSFUtil.getBindingValue("#{security.modulesEntities}");
		//System.out.println("Module Names "+mnames);
		//System.out.println("creatable entities"+entity.get("Employees").get(0));
		
		for(String x:mnames){
			System.out.println("Module Names "+x);
			
			if(x.contains(" ")){
				x = x.replace(" ", "_");
			}
			
			UIDojoTabPane pane2=(UIDojoTabPane)CreateTabTable.createTabPane(tabcont,x);
			
			
			ArrayList<String> creatableentites=entity.get(x);
			if(creatableentites!=null){
				createActionBar(pane2,x);
				
			}
			
			/*if(x.equals("Employees")){
				//added by Tenzing just for testing
				System.out.println("Inside when mdoule is Employee");
				ArrayList fields=this.getFields();
				System.out.println("Fields Name "+fields);
				XspTable utable = new XspTable(); 
				//UIPanelEx panel=(UIPanelEx)loadReadFields(fields,utable);
				UIComponent comp=getTest1();
				System.out.println("Afdter the laodReadFields");
				pane2.getChildren().add(comp);
				createActionBar(pane2);
				XspViewPanel panel=createViewTable(pane2);
				
			}*/
		}
		return tabs;
	}

	public void setTabs(UIComponent tabs) {
		this.tabs = tabs;
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
		//Load the fields here
	}
	public void createEditForm(String moduleName,String entityName){
		XspTable utable = new XspTable();
		utable.setId("tableCreate");
		Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
		loadReadFields(profile.getVisibleFieldsNames(moduleName, entityName),utable,0);
		centity.getChildren().add(utable);
	}
	public void createFeatureButtons(String entityName){
		//For this entity what features are available that needs to be populated by this method
	}
	

	private void loadReadFields(ArrayList<String> fields,XspTable table,int startRow){
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
				
					CompUtil.createInput(cell, fields.get(i).toString(),BEAN_NAME);
					break;
				}
			}
		}
		//test.getChildren().add(table);	
	}
	
	
	private  void createActionBar(UIComponent com, String moduleName)
	{
		System.out.println("create ActionBar");
		UIPanelEx actionpanel = new UIPanelEx();
		//XspTable utable = new XspTable();
		actionpanel.setId( "actionTable" );
		UIPanelEx actionpanel2 = new UIPanelEx();		
		actionpanel2.setId( "cEntityPanel" );
		
		//Create Button
		
	String expression3="#{javascript:sessionScope.documentId=\"\";viewScope.moduleName=\""+moduleName+"\";loadCreateEntity();}";
		
		XspCommandButton button3 =  CompUtil.createButton("Create","button3" );
		XspEventHandler ev3= CompUtil.createEventHandler("onclick", "complete", expression3, true,null);
		button3.getChildren().add(ev3);
		actionpanel.getChildren().add(button3);
		
		
		//Creatable entities combobox
		 XspSelectOneMenu comboBox = new XspSelectOneMenu();
		 comboBox.setId("comboBox1");	
		 
		 String ref = "#{viewScope.entityName}";
		System.out.println("String ref "+ref);		
		ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref);
		comboBox.setValueBinding( "value", vb1);
		 
		  
			
		 UISelectItemsEx result = new UISelectItemsEx();
	     //String sourceId = "comboBox1/xp:selectItems[1]/xp:this.value[1]/text()";
		 Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
	     ArrayList<String> valueExpr = profile.getCreatableEntitiesNames(moduleName);
	     Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
	     viewscope.put("valueExpr"+moduleName, valueExpr);
	     String expressionE="#{javascript:viewScope.valueExpr"+moduleName+"}";
	    // ValueBinding value = evaluator.createValueBinding(result, valueExpr, sourceId,Object.class);
	     //result.setValueBinding("value", value);
	 	ValueBinding vb2  = FacesContext.getCurrentInstance().getApplication().createValueBinding(expressionE);
		result.setValueBinding( "value", vb2);
		comboBox.getChildren().add(result);
		actionpanel.getChildren().add(comboBox);
		
		
		//View Button
		String expression4="#{javascript:viewScope.moduleName=\""+moduleName+"\";loadViewEntity();}";
		
		XspCommandButton button4 =  CompUtil.createButton("Read","button4" );
		XspEventHandler ev4= CompUtil.createEventHandler("onclick", "complete", expression4, true,null);
		button4.getChildren().add(ev4);
		actionpanel.getChildren().add(button4);
		
		
		
		//Readable entities combobox
		 XspSelectOneMenu comboBox2 = new XspSelectOneMenu();
		 comboBox2.setId("comboBox2");	
		 
		 String ref2 = "#{viewScope.entityRName}";
		System.out.println("String ref "+ref2);		
		ValueBinding vb3  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref2);
		comboBox2.setValueBinding( "value", vb3);
		 
		  
			
		 UISelectItemsEx result1 = new UISelectItemsEx();
	    
		
	     ArrayList<String> valueExpr1 = profile.getReadableEntitiesNames(moduleName);
	     viewscope.put("rEntity"+moduleName, valueExpr1);
	     String expressionRE="#{javascript:viewScope.rEntity"+moduleName+"}";
	
	 	ValueBinding vb4 = FacesContext.getCurrentInstance().getApplication().createValueBinding(expressionRE);
		result1.setValueBinding( "value", vb4);
		comboBox2.getChildren().add(result1);
		actionpanel.getChildren().add(comboBox2);
		
		/*
		//Create Save Button
		String expression1="#{javascript:viewScope.put('entityName',\"Employee\");pageaction['save'].execute(\"employee\");wrkspc.resetBean();}";
		
		XspCommandButton button1 =  CompUtil.createButton("Save","button1" );
		XspEventHandler ev1= CompUtil.createEventHandler("onclick", "complete", expression1, true,null);
		button1.getChildren().add(ev1);
		actionpanel.getChildren().add(button1);
		
		
		
		//Create Delete button
		String expression2="#{javascript:var serv=@Subset(@DbName(),1);var dbpath=@LeftBack(@Subset(@DbName(),-1),\"/\");var custdb=dbpath+\"\\\\\"+\"employees.nsf\";var dbname = new Array(serv,custdb);var db: NotesDatabase = session.getDatabase(serv, custdb);var docids=getComponent(\"viewPanel1\").getSelectedIds();for(id in docids){var doc:NotesDocument=db.getDocumentByID(id);var unid=doc.getUniversalID();sessionScope.put('documentId',unid);pageaction['delete'].execute(\"employee\")};}";
		XspCommandButton button2 =  CompUtil.createButton("Delete","button2" );
		XspEventHandler ev2= CompUtil.createEventHandler("onclick", "complete", expression2, true,null);
		button2.getChildren().add(ev2);
		actionpanel.getChildren().add(button2);
		
		*/
		
		
		
		
		//Add combobox to read entities
		
		
		
		com.getChildren().add( actionpanel );
		com.getChildren().add( actionpanel2 );
		
		//com.getChildren().add( actionpanel );
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
