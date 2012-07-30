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

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntryCollection;

import com.ibm.xsp.component.UIIncludeComposite;
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
import com.ibm.xsp.extlib.component.outline.UIOutlineDropDownButton;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;
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
	private UIComponent rentity;
	private ViewEntryCollection getmydocs;
	private UIComponent features;

	
	

	

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
		UIDojoTabContainer tabcont=(UIDojoTabContainer)CreateTabTable.createTT(tabs,true);
		tabcont.setId("MainTabContainer");
		
		
		//System.out.println("Get Selected tab "+ ((UIDojoTabContainer)JSFUtil.findComponent("MainTabContainer")).getSelectedTab());
	/*	//tabcont.setDefaultTabContent(sessionScope.defaulttab);
		  String sourceId = "djTabContainer1/@defaultTabContent";
          String defaultTabContentExpr = "#{javascript:sessionScope.whichtab;}";
          ValueBinding defaultTabContent = evaluator.createValueBinding(result, defaultTabContentExpr, sourceId,String.class);
          result.setValueBinding("defaultTabContent", defaultTabContent);
		

 		 String ref = "#{sessionScope.whichtab}";
 		System.out.println("String ref "+ref);		
 		ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref);
 		tabcont.setValueBinding( "value", vb1);*/
		
		ArrayList <String>mnames=(ArrayList<String>) JSFUtil.getBindingValue("#{security.modules}");
		if(mnames==null){
			System.out.println("if mnmes is null");
			return tabs;
		}
		//HashMap<String, ArrayList<String>> entity=(HashMap<String, ArrayList<String>>) JSFUtil.getBindingValue("#{security.modulesEntities}");
		//Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
		//System.out.println("Module Names "+mnames);
		//System.out.println("creatable entities"+entity.get("Employees").get(0));
		
		for(String x:mnames){
			System.out.println("Module Names "+x);
			
			//ArrayList<String> creatableentites=entity.get(x);
			//ArrayList<String> readableEntities = profile.getReadableEntitiesNames(x);
			//ArrayList<String> deletableEntities = profile.getDeletableEntitiesNames(x);
			//ArrayList<String> allEntities = profile.getAllEntitiesNames(x);
			
			if(x.contains(" ")){
				x = x.replace(" ", "_");
			}
			
			UIDojoTabPane pane2=(UIDojoTabPane)CreateTabTable.createTabPane(tabcont,x);
			//String tabid=pane2.getId();
			//String onShowExp="console.log(\"hiiiiii\");\nvar thisid =\"#{javascript:this;}\";\nconsole.log(\"%o\",thisid);\nXSP.executeOnServer(\'view:_id1:getTabPane\', \"\", \"\", \""+tabid+"\");";
			 String onShow = "console.log(\"hiiiiii\");\nXSP.executeOnServer(\'view:_id1:getTabPane\', \"\", \"\",  \""+x+"\");";
			//ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(onShowExp);
			//pane2.setValueBinding( "onShow", vb1);
			
			
			// String onShowExpr = "console.log(\"hiiiiii\");\nvar thisid =\"#{javascript:this.getId();}\";\nconsole.log(\"%o\",thisid);\nXSP.executeOnServer(\'view:_id1:eventEntityPerm\', \"\", \"\", \"\");";
	         //   ValueBinding onShow = evaluator.createValueBinding(result, onShowExpr, sourceId,String.class);
	         //   result.setValueBinding("onShow", onShow);
	         //   return result;
			pane2.setOnShow(onShow);

			
			
			
		
		//	UIComponent entityTabPanel = new UIPanelEx();
			
			createActionBar(pane2,x);
			
			
			/*UIDojoTabContainer entityTabs=(UIDojoTabContainer)CreateTabTable.createTT(entityTabPanel,false);
			int c=0;//if the entity is creatable, editable, deletable, used when creating action bar
			int r=0;
			int d=0;
			System.out.println("all entity names"+allEntities);
			if(allEntities!=null){
				for(String entityName:allEntities){
					System.out.println("tab----3");
					UIDojoTabPane paneE=(UIDojoTabPane)CreateTabTable.createTabPane(entityTabs,entityName);
					System.out.println("tab----4");
					pane2.getChildren().add(entityTabPanel);
					System.out.println("tab----5");
					if(creatableentites!=null){
						if(creatableentites.contains(entityName)){
							c=1;
						}else{
							c=0;
						}
					}
					
					if(readableEntities!=null){
						if(readableEntities.contains(entityName)){
							r=1;
						}else{
							r=0;
						}
					}
					
					if(deletableEntities!=null){
						if(deletableEntities.contains(entityName)){
							d=1;
						}else{
							d=0;
						}	
					}
					System.out.println("tab----6crd "+c+r+d);
					createEntityActionBar(paneE,x,entityName,c, r,d);
					
					
				}
				System.out.println("tab----6");
				
			}
			
		*/
		
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
		loadEditFields(profile.getEditableFieldsNames(moduleName, entityName), profile.getVisibleFieldsNames(moduleName, entityName),utable,0);
		centity.getChildren().add(utable);
		
		
		if(profile.isEntityUpdate(moduleName, entityName)){
			viewScope.put("editEntity",true);
		}else{
			viewScope.put("editEntity",false);
		}
		
		if(profile.isEntityDelete(moduleName, entityName)){
			viewScope.put("deleteEntity",true);
		}else{
			viewScope.put("deleteEntity",false);
		}
		
	}
	public void createFeatureButtons(UIComponent actionpanel,String moduleName,XspTable acttable,XspTableRow actrow1){
		//For this entity what features are available that needs to be populated by this method
		System.out.println("ModuleName "+moduleName);	
		Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
		ArrayList<String> features = profile.getVisibleFeaturesNames(moduleName);
		System.out.println("After getmodules Features 123 "+features);
		for(String x:features){
			XspTableCell cell1=CompUtil.createCell(actrow1, "cell"+x);
			String expression2="alert(\"Hello: \""+x+")";
			XspCommandButton button2 =  CompUtil.createButton(x,"button2"+x );
			XspEventHandler ev2= CompUtil.createEventHandler("onclick", "complete", expression2, false,null);
			button2.getChildren().add(ev2);
			cell1.getChildren().add(button2);
			
			//actionpanel.getChildren().add(acttable);
		}
		
		
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
	
	private void loadEditFields(ArrayList<String>editableFields ,ArrayList<String> fields,XspTable table,int startRow){
		//This function is used to populate the fields in the given table
		//fields:list of fields
		//table: table component where field label and field will be added
		//startRow: the starting row number of the table where the fields needs to be added
		for (int i = 0; i < fields.size(); i++) {
			XspTableRow row1 = CompUtil.createRow(table, "row" + i);
			for (int j = 0; j < 2; j++) {
				XspTableCell cell = CompUtil.createCell(row1, "cell" + i + j);
				String fieldName = fields.get(i).toString();
				int e=0;
				if(editableFields.contains(fieldName)){
					e=2;//Editable field
				}else{
					e=1;//Readonly  field
				}
				switch (j) {
				case 0:
					CompUtil.createLabel(cell,fields.get(i).toString(),true);
					break;
				case 1:
				
					CompUtil.createInput(cell, fieldName,BEAN_NAME,e);
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
		actionpanel.setId( "actionTable"+moduleName );
		UIPanelEx entitypanel = new UIPanelEx();		
		entitypanel.setId( "MainEntityPanel"+moduleName );
		
		//Create 1 table with 1 row and 2 column
		XspTable viewtable = new XspTable();
		//viewtable.setStyle("border-color:rgb(0,0,255);border-style:solid");
		XspTableRow viewrow = CompUtil.createRow(viewtable, "row" + "0");
		XspTableCell viewcell1 = CompUtil.createCell(viewrow, "EntityPanel"+moduleName);
		XspTableCell viewcell2 = CompUtil.createCell(viewrow, "readPanel"+moduleName);
		entitypanel.getChildren().add(viewtable);
		//For dropdown button		
        
 		HashMap<String, ArrayList<String>> entity=(HashMap<String, ArrayList<String>>) JSFUtil.getBindingValue("#{security.modulesEntities}");
 		Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
 		if(moduleName.contains("_")){
			moduleName = moduleName.replace("_", " ");
		}
 		
 		//Read&Write Entities of the current module
 		ArrayList<String> rwentities=profile.getPublicRWEntities(moduleName); 		
 		
 		ArrayList<String> creatableentites=entity.get(moduleName);
 		
 		if(rwentities!=null){
 		for(String a : rwentities){
 			if(!creatableentites.contains(a)){
 				creatableentites.add(a);
 			}
 		}
 		}
 		if(moduleName.contains(" ")){
			moduleName = moduleName.replace(" ", "_");
		}
 		//to get readable entities for the given moduleName
 		ArrayList<String> readentities=profile.getPublicReadEntities(moduleName);
 		
 		ArrayList<String> readableEntities = profile.getReadableEntitiesNames(moduleName);
 		
 		if(rwentities!=null){
 	 		for(String a : rwentities){
 	 			if(!readableEntities.contains(a)){
 	 				readableEntities.add(a);
 	 			}
 	 		}
 	 	}
 		if(readentities!=null){
 			for(String a : readentities){
 	 			if(!readableEntities.contains(a)){
 	 				readableEntities.add(a);
 	 			}
 	 		}
 		}
 		
 		
         System.out.println("Creatable entities "+creatableentites);
         System.out.println("Readable entities "+readableEntities);
         
         XspTable acttable = new XspTable();
         //acttable.setStyle("border-color:rgb(255,0,0);border-style:solid");
 		acttable.setId("actTable");
 		XspTableRow actrow1 = CompUtil.createRow(acttable, "row" + "0"); 
         if(creatableentites!=null){
        	
        	  UIOutlineDropDownButton result = new UIOutlineDropDownButton();
    		  result.setId("dropDownButton1");
    		  ComplexContainerTreeNode treeNodes = new ComplexContainerTreeNode();
			  treeNodes.setComponent(result);
			  treeNodes.setLabel("Create");
    		  for(String ent : creatableentites){
    			  ComplexLeafTreeNode children = new ComplexLeafTreeNode();
    			    children.setComponent(result);
    			    children.setLabel(ent);
    			    
    			    children.setSubmitValue(moduleName+"+"+ent);
    			     System.out.println("Inside createDropDown12");    
    			     treeNodes.addChild(children); 
    		  }
              
    		    String onItemClickExpr = "#{javascript:var choice=context.getSubmittedValue()\nvar array=choice.split(\"+\");\nprintln(\"Selected Choice \",choice);\nviewScope.moduleName=array[0];\nviewScope.entityName=array[1];\nsessionScope.documentId=\"\";\nloadCreateEntity(viewScope.moduleName,viewScope.entityName);\n\n\n\n\n}";
    		    
    	      	System.out.println("Inside createDropDown1234");
    	      	String refreshId="EntityPanel"+moduleName;
    	      	XspEventHandler ev2= CompUtil.createEventHandler("onItemClick", "partial",onItemClickExpr, true,refreshId);
    	      	result.getChildren().add(ev2);
              
    	      	result.addNode(treeNodes); 
    	      	
    	         			
    			XspTableCell cell = CompUtil.createCell(actrow1, "cell"+"00");
    			
    			cell.getChildren().add(result);
    	      	
                actionpanel.getChildren().add(acttable);
     
         }
 
      
         if(readableEntities!=null){        	

       	  UIOutlineDropDownButton result1 = new UIOutlineDropDownButton();
   		  result1.setId("dropDownButton2");
   		  ComplexContainerTreeNode treeNodes1 = new ComplexContainerTreeNode();
			  treeNodes1.setComponent(result1);
			  treeNodes1.setLabel("View");
   		  for(String readent : readableEntities){
   			  ComplexLeafTreeNode children1 = new ComplexLeafTreeNode();
   			    children1.setComponent(result1);
   			    children1.setLabel(readent);   			    
   			    children1.setSubmitValue(moduleName+"+"+readent);   			      
   			     treeNodes1.addChild(children1); 
   		  }
             
   		    String onItemClickExpr = "#{javascript:var choice=context.getSubmittedValue()\nvar array=choice.split(\"+\");\nprintln(\"Selected Read Choice \",choice);\nviewScope.moduleName=array[0];\nviewScope.entityName=array[1];\nloadViewEntity(viewScope.moduleName,viewScope.entityName);\n\n\n\n\n}";
   		       	      	
   	      	String refreshId="EntityPanel"+moduleName;
   	      	XspEventHandler ev3= CompUtil.createEventHandler("onItemClick", "partial",onItemClickExpr, true,refreshId);
   	      	result1.getChildren().add(ev3);
             
   	      	result1.addNode(treeNodes1); 
   	    
   	   		
			XspTableCell cell1 = CompUtil.createCell(actrow1, "cell"+"01");

			cell1.getChildren().add(result1);
          //  actionpanel.getChildren().add(cell1);
        	 
         }
				
		//For Features buttons
         System.out.println("Calling createFeatures");
         createFeatureButtons(actionpanel,moduleName,acttable,actrow1);	
		
		
		
		
		
/*		
		//Create Button
		
	String expression3="#{javascript:sessionScope.documentId=\"\";viewScope.moduleName=\""+moduleName+"\";loadCreateEntity(\""+moduleName+"\");}";
		
		XspCommandButton button3 =  CompUtil.createButton("Create","button3" );
		XspEventHandler ev3= CompUtil.createEventHandler("onclick", "complete", expression3, true,null);
		button3.setStyle("border:1px solid #A0A0A0");
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
		String expression4="#{javascript:viewScope.moduleName=\""+moduleName+"\";loadViewEntity(\""+moduleName+"\");}";
		
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
		com.getChildren().add( entitypanel );
		
		//com.getChildren().add( actionpanel );
	}
	
	private  void createEntityActionBar(UIComponent comp,String moduleName, String entityName, int c, int r, int d)
	{
		//c, r, d for entity is creatable, editable, deletable based on this we will load buttons
		System.out.println("create ActionBar");
		UIPanelEx actionpanel = new UIPanelEx();
		//XspTable utable = new XspTable();
		actionpanel.setId( "actionTable"+moduleName+entityName );
		UIPanelEx actionpanel2 = new UIPanelEx();		
		actionpanel2.setId( "cEntityPanel"+entityName );
		
		//Create Button
		if(c==1){
		String expression3="#{javascript:sessionScope.documentId=\"\";viewScope.moduleName=\""+moduleName+"\";viewScope.entityName=\""+entityName+"\";loadCreateEntity(\""+moduleName+"\",\""+entityName+"\");}";	
		XspCommandButton button3 =  CompUtil.createButton("Create","button3" );
		XspEventHandler ev3= CompUtil.createEventHandler("onclick", "complete", expression3, true,null);
		button3.getChildren().add(ev3);
		actionpanel.getChildren().add(button3);
		}
		if(d==1){
		//Delete Button
		String expression4="#{javascript:}";	
		XspCommandButton button4 =  CompUtil.createButton("Delete","button4" );
		XspEventHandler ev4= CompUtil.createEventHandler("onclick", "complete", expression4, true,null);
		button4.getChildren().add(ev4);
		actionpanel.getChildren().add(button4);
		}
		
		System.out.println("view--1");
	if(entityName.equals("Employee")){
			//"+actionpanel.getId()+"  com.weberon.DynamicCC.loadCC(context, actionpanel, "/testcontrol.xsp", "cctestControl"+entityName);
			String expression = "#{javascript:loadTestControl(\""+actionpanel.getId()+"\",\"/cc_EntityView.xsp\",\"moduledivx"+moduleName+entityName+"\",\""+moduleName+"\",\""+entityName+"\")}";
			XspEventHandler ev4= CompUtil.createEventHandler("onClientLoad", "norefresh", expression, true,null);
			actionpanel.getChildren().add(ev4);
		}
		System.out.println("view--2");
		
	
		comp.getChildren().add( actionpanel );
	
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
	

	public UIComponent getRentity() {
		rentity = new UIPanelEx();
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		String pid = (String) viewscope.get("personunid");		
		String moduleName = (String)viewscope.get("moduleName");
		String entityName = (String)viewscope.get("entityName");
		if(moduleName!=null & entityName!=null){
			createReadForm(moduleName,entityName);
		}
		return rentity;
	}

	public void setRentity(UIComponent rentity) {
		this.rentity = rentity;
	}
	
	public void createReadForm(String moduleName,String entityName){
		XspTable utable = new XspTable();
		utable.setId("tableRead");
		Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
		loadViewFields(profile.getVisibleFieldsNames(moduleName, entityName),utable,0);
		rentity.getChildren().add(utable);
		
		if(profile.isEntityUpdate(moduleName, entityName)){
			viewScope.put("editEntity",true);
		}else{
			viewScope.put("editEntity",false);
		}
		
		if(profile.isEntityDelete(moduleName, entityName)){
			viewScope.put("deleteEntity",true);
		}else{
			viewScope.put("deleteEntity",false);
		}
		
	}
	
	private void loadViewFields(ArrayList<String> fields,XspTable table,int startRow){
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
				
					CompUtil.createInput(cell, fields.get(i).toString(),BEAN_NAME,1);
					break;
				}
			}
		}
		//test.getChildren().add(table);	
	}
	
	


	public ViewEntryCollection getGetmydocs() {
		System.out.println("Inside getMyDocs");
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		System.out.println("Inside getMyDocs 1");
		String moduleName=(String)viewscope.get("moduleName");
		String entityName=(String)viewscope.get("entityName");	
		System.out.println("Inside getMyDocs moduleName entityName "+moduleName+entityName);
		viewscope.put("fields", "Form,CreatedBy");
		Profile profile = (Profile) JSFUtil.getBindingValue("#{security.profile}");
		//ArrayList fieldNames=profile.getVisibleFieldsNames(moduleName, entityName);
	//	viewscope.put("fields",fieldNames);

	
		System.out.println("Inside getMyDocs 23"); 
		System.out.println("Inside getMyDocs ModuleName "+moduleName);
		
		if(moduleName.contains("_")){
			moduleName=moduleName.replace("_"," ");
		}
		
		//get the module name and treat it as database name
		String dbname=moduleName.toLowerCase().replace(" ", "");
		dbname=dbname+".nsf";	
		//ViewName can get from entityName
		String viewName=entityName;	
		System.out.println("Inside getMyDocs 256"); 
		try {
			Database db = session.getDatabase("", bsuitepath+dbname);
			System.out.println("db Name "+db.getFileName());
			View allView = db.getView(viewName);			
			System.out.println("View Name "+allView.getName());
			
			//check the AccessType for the given EntityName
			Profile profile1 = (Profile) JSFUtil.getBindingValue("#{security.profile}");
			String eaccess=profile1.getEntityAccessType(moduleName, entityName);
			System.out.println("Entity AccessType "+eaccess);
			if(eaccess.equals("1")){//private
				//to get the query string from the security.searchString		
				String querystr= (String)JSFUtil.getBindingValue("#{role.searchString}");			
				System.out.println("Query String "+querystr);
				allView.FTSearch(querystr);
			}	
						
			System.out.println("Doc Count "+allView.getAllEntries().getCount());
			return allView.getAllEntries();		
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public void setGetmydocs(ViewEntryCollection getmydocs) {
		this.getmydocs = getmydocs;
	}
	
	public UIComponent getFeatures() {
		
		return features;
	}

	public void setFeatures(UIComponent features) {
		this.features = features;
	}
	private void loadTab(String moduleName,String entityName){
		
	}
}
