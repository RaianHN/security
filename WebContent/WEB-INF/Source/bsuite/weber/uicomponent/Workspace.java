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
import javax.faces.el.ValueBinding;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;
import lotus.domino.ViewEntryCollection;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.UISelectItemsEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspPager;
import com.ibm.xsp.component.xp.XspSelectOneMenu;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewColumn;
import com.ibm.xsp.component.xp.XspViewColumnHeader;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoViewData;
import com.ibm.xsp.util.ManagedBeanUtil;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.relationship.Association;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;
import bsuite.weber.tools.StringUtil;

public class Workspace extends BsuiteWorkFlow implements Serializable {
	private String BEAN_NAME="employee";
	private  UIComponent test;
	private static String m_documentId;
	private Vector<ViewColumn> viewlist;
	Database userdb;
private UIPanelEx test1;
private UIPanelEx testview;
private ViewEntryCollection getmydocs;


public ViewEntryCollection getGetmydocs() {
	System.out.println("Inside getMyDocs");
	Map viewscope = (Map) JSFUtil.getVariableValue("ViewScope");
	System.out.println("Inside getMyDocs 1");
	//String moduleName=(String)viewscope.get("moduleName");
	//String entityName=(String)viewscope.get("entityName");	
	//viewscope.put("Fields", "Name;Salary");
	
	//System.out.println("Viewscope value fields "+viewscope.get("Fields"));
	String moduleName="Employees";
	String entityName="Employee";
	System.out.println("Inside getMyDocs 23"); 
	System.out.println("Inside getMyDocs ModuleName "+moduleName);
	//get the module name and treat it as database name
	String dbname=moduleName.toLowerCase().replace(" ", "");
	dbname=dbname+".nsf";	
	//ViewName can get from entityName
	String viewName=entityName;	
	System.out.println("Inside getMyDocs 256"); 
	try {
		Database db = session.getDatabase("", bsuitepath+dbname);
		View allView = db.getView(viewName);								
		System.out.println("Inside getMyDocs 2357"); 
		//to get the query string from the security.searchString		
		//String querystr= (String)JSFUtil.getBindingValue("#{security.searchString}");			
		String querystr="FIELD Salary=89";
		allView.FTSearch(querystr);
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
			
			if(x.equals("Employees")){
				//added by Tenzing just for testing
				System.out.println("Inside when mdoule is Employee");
				ArrayList fields=this.getFields();
				System.out.println("Fields Name "+fields);
				XspTable utable = new XspTable(); 
				//UIPanelEx panel=(UIPanelEx)loadReadFields(fields,utable);
				UIComponent comp=getTest1();
				System.out.println("Afdter the laodReadFields");
				pane2.getChildren().add(comp);
				//createActionBar(pane2);
				XspViewPanel panel=createViewTable(pane2);
				
			}
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
	modules.add("InOut");
	
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

private  void createActionBar(UIComponent com)
{
	System.out.println("create ActionBar");
	UIPanelEx actionpanel = new UIPanelEx();
	//XspTable utable = new XspTable();
	actionpanel.setId( "actionTable" );
	UIPanelEx actionpanel2 = new UIPanelEx();		
	actionpanel2.setId( "actionTable2" );
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
	
	
	 XspSelectOneMenu comboBox = new XspSelectOneMenu();
	 comboBox.setId("comboBox1");	
	 
	 String ref = "#{viewScope.entityName}";
	System.out.println("String ref "+ref);		
	ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref);
	comboBox.setValueBinding( "value", vb1);
	 
	  
		
	 UISelectItemsEx result = new UISelectItemsEx();
     //String sourceId = "comboBox1/xp:selectItems[1]/xp:this.value[1]/text()";
     String valueExpr = "#{javascript:var a:java.util.ArrayList=new java.util.ArrayList();\na.add(\"1\");\na.add(\"2\");\nreturn a;}";
    // ValueBinding value = evaluator.createValueBinding(result, valueExpr, sourceId,Object.class);
     //result.setValueBinding("value", value);
 	ValueBinding vb2  = FacesContext.getCurrentInstance().getApplication().createValueBinding(valueExpr);
	result.setValueBinding( "value", vb2);
	comboBox.getChildren().add(result);
	actionpanel.getChildren().add(comboBox);
	com.getChildren().add( actionpanel );
	
	//Create Save and Close Button
	/*XspCommandButton button2= CompUtil.createButton("Save and Close","button2" );
	String expression2="#{javascript:actions[\"save\"].execute(\"user\");\ncontext.redirectToPage(\"UserView\");}";
	XspEventHandler ev2= CompUtil.createEventHandler("onclick", "complete", expression2, true,null);
	button2.getChildren().add(ev2);
	actionpanel.getChildren().add(button2);*/
	//Create New User Button
	/*XspCommandButton button2= CompUtil.createButton("New User","button2" );
	String expression2="#{javascript:sessionScope.put('userdocumentId',\"\");uiuser.resetBean();var c= getComponent(\'container\');uiuser.resetUI(c, facesContext,\"/ccUser.xsp\");}";
	XspEventHandler ev2= CompUtil.createEventHandler("onclick", "partial", expression2, true,"container");
	button2.getChildren().add(ev2);
	actionpanel.getChildren().add(button2);
	//Create Back Button
	XspCommandButton button3= CompUtil.createButton("Back","button3" );
	String expression3="#{javascript:context.redirectToPage(\"SushantActivity\");}";
	XspEventHandler ev3= CompUtil.createEventHandler("onclick", "complete", expression3, true,null);
	button3.getChildren().add(ev3);
	actionpanel.getChildren().add(button3);
	//create add email button
	XspCommandButton button4= CompUtil.createButton("Add Email1","button4" );
	String expression4="#{javascript:println(\"getting component\")\nvar email= getComponent(\"iemail1\");\nif(email==null){\nvar table=getComponent(\'customDatatable\');\nuiuser.getAddNewRow(table,\"email1\");}}";
	XspEventHandler ev4= CompUtil.createEventHandler("onclick", "partial", expression4, true,"container");
	button4.getChildren().add(ev4);
	actionpanel2.getChildren().add(button4);
	//create add Location button
	XspCommandButton button5= CompUtil.createButton("Add Location1","button5" );
	String expression5="#{javascript:var loc= getComponent(\"location1\");\nif(loc==null){\nvar table=getComponent(\'customDatatable\');\nuiuser.getAddNewRow(table,\"location1\");}}";
	XspEventHandler ev5= CompUtil.createEventHandler("onclick", "partial", expression5, true,"container");
	button5.getChildren().add(ev5);
	actionpanel2.getChildren().add(button5);
	//create add Contact button
	XspCommandButton button6= CompUtil.createButton("Add Contact1","button6" );
	String expression6="#{javascript:var com= getComponent(\"contact1\");\nif(com==null){\nvar table=getComponent(\'customDatatable\');\nuiuser.getAddNewRow(table,\"contact1\");}}";
	XspEventHandler ev6= CompUtil.createEventHandler("onclick", "partial", expression6, true,"container");
	button6.getChildren().add(ev6);
	actionpanel2.getChildren().add(button6);
	//Remove Email1
	XspCommandButton button7= CompUtil.createButton("Remove Email1","button7" );
	String expression7="#{javascript:uiuser.removeRow(\"customDatatable\",\"iemail1\")}";
	//String expression7="#{javascript:var com= getComponent(\'iemail1\');\nif(com!=null){\n\tprintln(\"email1 present\");\n\tvar table = getComponent(\'customDatatable\');\n\n\tif(table.getChildren().remove(com.getParent().getParent())){\n\tprintln(\"removed email1\")\n\t}\n\n}}";
	XspEventHandler ev7= CompUtil.createEventHandler("onclick", "partial", expression7, true,"container");
	button7.getChildren().add(ev7);
	actionpanel2.getChildren().add(button7);
	//Remove Location1
	XspCommandButton button8= CompUtil.createButton("Remove Location1","button8" );
	String expression8="#{javascript:uiuser.removeRow(\"customDatatable\",\"ilocation1\")}";		
	XspEventHandler ev8= CompUtil.createEventHandler("onclick", "partial", expression8, true,"container");
	button8.getChildren().add(ev8);
	actionpanel2.getChildren().add(button8);
	//Remove Contact1
	XspCommandButton button9= CompUtil.createButton("Remove Contact1","button9" );
	String expression9="#{javascript:uiuser.removeRow(\"customDatatable\",\"icontact1\")}";		
	XspEventHandler ev9= CompUtil.createEventHandler("onclick", "partial", expression9, true,"container");
	button9.getChildren().add(ev9);
	actionpanel2.getChildren().add(button9);
	
	com.getChildren().add( actionpanel );
	com.getChildren().add( actionpanel2 );
	System.out.println("end Action bar creation");*/
}

private XspViewPanel createViewTable(UIDojoTabPane panel1)  {
	String dbname="";
	try{
			Database currentdb = ExtLibUtil.getCurrentDatabase();
			String path = BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			 userdb = session.getDatabase("", path
						+ "employees.nsf");
				dbname=userdb.getFileName();
	System.out.println("Get the dbname "+dbname);
	System.out.println("Get the dbname path "+userdb.getFilePath());
	}catch (Exception e) {
		// TODO: handle exception
	}
	
	XspViewPanel viewpanel = new XspViewPanel();
    viewpanel.setVar("viewEntry");
    viewpanel.setRows(30);
    DominoViewData data = new DominoViewData();
    data.setComponent(viewpanel);
    data.setViewName("Employee");
    data.setDatabaseName("INB80/Development!!mvc2/employees.nsf");
    data.setVar("view1");
    viewpanel.setData(data);
    viewpanel.setId("viewPanel1");
   
    XspViewColumn column = new XspViewColumn();
    column.setColumnName("Name");
    viewpanel.getChildren().add(column);
    panel1.getChildren().add(viewpanel);
	//XspViewPanel viewpanel=createViewpanel( panel1, "viewPanel1", "viewentry1");
	 //DominoViewData data = createDominoViewData(userdb,"Employee","view1");
	 try {
		System.out.println("Is DB open "+userdb.isOpen());
	} catch (NotesException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	// data.setComponent(viewpanel);   
    //viewpanel.setData(data);        
   /* viewlist=null;
    //init column list
    viewlist= getviewcolumns();
    
    int i=1;
    for(ViewColumn vc : viewlist){
    	try {
    		System.out.println("Creating view COlumn");
		//	if(!((vc.getTitle().equals("REF"))||(vc.getTitle().equals("VerRef")))){
			//	System.out.println(vc.getTitle()+" : "+vc.getItemName());
			//	String link= "";
			//	Boolean chkbox=false;
			//	XspEventHandler ev10=null;
			//	XspViewColumn vcol;
			//	XspViewColumnHeader vhead;
			//	if(i==1){
			//		link="link";
			//		chkbox=true;
			//		String expression10="#{javascript:var doc=viewentry.getDocument();\nsessionScope.put(\'userdocumentId\',doc.getUniversalID());\nvar c= getComponent(\'container\');\n\nuiuser.resetUIform(c, \"container\");}";		
			//		 ev10= CompUtil.createEventHandler("onclick", "partial", expression10, true,"container");
			//	}
				
				 //  XspViewColumn result = new XspViewColumn();
		         //   result.setShowCheckbox(true);
		         ///   result.setColumnName("Name");
		         //   result.setDisplayAs("link");
		         //   setId(result, "viewColumn1");
				 XspViewColumn column = new XspViewColumn();
		         column.setColumnName(vc.getItemName());
		      
		            //setId(result, "viewColumn2");
		         
		          System.out.println("222");
		            
				//XspViewColumnHeader columnheader = new XspViewColumnHeader();
	          //  columnheader.setValue(vc.getItemName());
	           //header.setId("viewColumnHeader2");
	          //  column.setHeader(columnheader);
	            
	            
//Map facets=column.getFacets();
//facets.entrySet();
//facets.put("header",columnheader);
	            viewpanel.getChildren().add(column);
				//viewpanel.getChildren().add(columnheader);
				 System.out.println("333");
				
			//	vcol=CompUtil.createViewcolumn(viewpanel,vc.getItemName(), "viewColumn"+Integer.toString(i), link, chkbox);
				//vcol=  CompUtil.createViewcolumn(viewpanel, vc.getItemName(), "viewColumn"+Integer.toString(i));
				
			//	 vhead= CompUtil.createViewcolumnheader(vcol, vc.getTitle(), "viewColumnHeader"+Integer.toString(i));
			//	vcol.setHeader(vhead);
			//	System.out.println("After creating vew column and column headers");
			//	 Map facets=vcol.getFacets();
				
			//	facets.entrySet();
				//facets.put("header", vhead);
			//	 System.out.println("Facets value "+facets.values());
				//if(i==1){
					//vcol.getChildren().add(ev10);
				//}
		//	}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		i++;
    }
    panel1.getChildren().add(viewpanel);*/
	return viewpanel;
}
@SuppressWarnings("unchecked")
private Vector<ViewColumn> getviewcolumns()  {
System.out.println("inside getViewColumns");
	
	
	View view= null;
	
	Vector<ViewColumn> listcolumn = null;
	
		//we need to reload the VIEW
	try {
		//userdb=ExtLibUtil.getCurrentSession().getDatabase("",  bsuitepath+"employees.nsf");
		//userdb.open();
		view =userdb.getView("Employee");
		System.out.println("inside getViewColumns getting View Name"+view.getName() );
		listcolumn=	view.getColumns();				        
		System.out.println("inside getViewColumns First Column Name "+listcolumn.get(0));
		  return listcolumn;
	} catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return null;
		
	
}


public  XspViewPanel createViewpanel( UIComponent parent, String id, String var) {
	
	 
	XspViewPanel viewpanel = new XspViewPanel();
    viewpanel.setVar("viewEntry");
    viewpanel.setRows(30);
    DominoViewData data = new DominoViewData();
    data.setComponent(viewpanel);
    data.setViewName("Employee");
    data.setDatabaseName("INB80/Development!!mvc2/employees.nsf");
    data.setVar("view1");
    viewpanel.setData(data);
    viewpanel.setId("viewPanel1");
    parent.getChildren().add(viewpanel);
    return viewpanel;
}

public  DominoViewData createDominoViewData(Database userdb,String view, String var){
	 DominoViewData data = new DominoViewData();

	 try {
		data.setDatabaseName(userdb.getFileName());
		System.out.println("Is DB open in CreateDominoData"+userdb.isOpen());
	} catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 data.setViewName(view);         
    data.setVar(var);
   // String searchExpr = "#{javascript:requestScope.get(\"findme\");}";
	 //ValueBinding search  = FacesContext.getCurrentInstance().getApplication().createValueBinding(searchExpr);
    //data.setValueBinding("search", search); 
	 return data;
} 

public static XspPager createPager1(UIComponent parent) {
    XspPager result = new XspPager();
    result.setPartialRefresh(true);
    result.setLayout("Previous Group Next");
    result.setId( "pager1");
    return result;
}

//to get the document collections

public ViewEntryCollection getDocCollection(){
	Map viewscope = (Map) JSFUtil.getVariableValue("ViewScope");	
	String moduleName=(String)viewscope.get("moduleName");
	String entityName=(String)viewscope.get("entityName");
	
	//get the module name and treat it as database name
	String dbname=moduleName.toLowerCase().replace(" ", "");
	dbname=dbname+".nsf";
	
	//ViewName can get from entityName
	String viewName=entityName;
	
	try {
		Database db = session.getDatabase("", bsuitepath+dbname);
		View allView = db.getView(viewName);								
		
		//to get the query string from the security.searchString
		
		String querystr= (String)JSFUtil.getBindingValue("#{security.searchString}");
		
		
		allView.FTSearch(querystr);
		System.out.println("Doc Count "+allView.getAllEntries().getCount());
		return allView.getAllEntries();
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	return null;
}
	


}
