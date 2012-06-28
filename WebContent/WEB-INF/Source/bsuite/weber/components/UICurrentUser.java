package bsuite.weber.components;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import bsuite.weber.backend.PageDataBean;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;
import bsuite.weber.tools.StringUtil;

import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewColumn;
import com.ibm.xsp.component.xp.XspViewColumnHeader;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.model.domino.DominoViewData;
import com.ibm.xsp.util.ManagedBeanUtil;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewColumn;

public  class UICurrentUser implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 9210156024665912046L;
	private static String m_documentId;
	private UIPanelEx userpanel;
	private UIPanelEx pagePanel;
	public static FacesContext context;
	private final String BEAN_NAME= "user";
	private String[] fields;
	private String newfield;
	@SuppressWarnings("unchecked")
	private Map requestscope;
	private Vector<ViewColumn> viewlist;
	private Database userdb;
	private String bsuitepath;
	@SuppressWarnings("unchecked")
	private static Map sessionscope;
	
	
	public UICurrentUser() {
		
	}
	public String[] getFields() {
		return fields;
	}
	public UIPanelEx getUserPanel()
	{
		userpanel = null;
		System.out.println("Starting user panel");
		if (userpanel == null) {
			System.out.println("Populate user panel");			
			this.createUser();			// Populate user panel

		}
		
		return userpanel;// return dynamicDataTableGroup;
	}
	@SuppressWarnings("unchecked")
	public void resetUIform(UIComponent com,String id){
		m_documentId=null;
		UIComponent parentpanel= com.getParent();
		com.getParent().getChildren().removeAll(com.getParent().getChildren());
		this.initfieldslist();
		this.createFormTable(parentpanel, fields, id);
		
	}
	public void resetUI(UIComponent com,FacesContext context,String page){
		CompUtil.create(com, context, page);
	}
	

	public void resetBean(){
		
		System.out.println("calling reset");
		Object user = ManagedBeanUtil.getBean(context,BEAN_NAME);
		try {
			Class<?> c= user.getClass();
			//System.out.println("class");
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
	public void  setUserPanel(){
		userpanel = null;
	}
	
	@SuppressWarnings("unchecked")
	public UIPanelEx getPage() {
		pagePanel = null;
		m_documentId=null;
		context=FacesContext.getCurrentInstance();
		requestscope = (Map) JSFUtil.getVariableValue("requestScope");	
		sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		FacesContext.getCurrentInstance().getApplication();
	
		//System.out.println("Starting Page construct");
		if (pagePanel == null) {
			//System.out.println("Populate user page");			
			this.createPage(null);			// Populate user panel
			System.out.println("End page ");
		}
		System.out.println("return panel page ");
		return pagePanel;// return dynamicDataTableGroup;
	}
	private void createPage(String pageid){
		//System.out.println("Populate user panel");		
		bsuitepath = BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
		try {
			userdb = ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+BEAN_NAME+".nsf");
		} catch (NotesException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pagePanel= new UIPanelEx();	System.out.println(" panel init");
		 //getting data sets  ---- Here the page can be generalised or specific to data set
		//For now the data view is created statically
	
		 //adding data sets		 
		 //creating Page construct		 
		 	//adding container 
		 UIDojoBorderContainer pgcontainer= CompUtil.createDjbordercontainer(pagePanel, "djBorderContainer1");
		 
		//	System.out.println("adding panels");	//adding panels
		 
		 //adding view to panel1
		 UIDojoBorderPane panel1=CompUtil.createDjborderpanel(pgcontainer, "panel1", "left", true);		 
		
		//	System.out.println("Loading view to panel1"); //Loading view to panel1
		 
		 @SuppressWarnings("unused")
		XspViewPanel viewpanel= createViewTable(panel1);
         
		// System.out.println("adding form container to panel2"); //adding form container to panel2
		 UIDojoBorderPane panel2 = CompUtil.createDjborderpanel(pgcontainer, "panel2", "center", true);
		// System.out.println("init list for table"); //init list for table
		
		 initfieldslist();
		 
		 System.out.println("create table");//create table
		 createFormTable(panel2, fields,"container");
	}
	private void initfieldslist(){
		 fields=null;
		 try {
			fields= getfields();
			if(requestscope.get("user")!=null){
				resetBean();
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private XspViewPanel createViewTable(UIDojoBorderPane panel1)  {
		XspViewPanel viewpanel=CompUtil.createViewpanel( panel1, "viewPanel1", "viewentry1");
		 DominoViewData data = CompUtil.createDominoViewData("Demo2\\user.nsf","userprofile","view1");
        data.setComponent(viewpanel);   
        viewpanel.setData(data);
       // viewpanel..setShowColumnHeader(true);
        
       // viewpanel.set
        //Adding cloumns
       
        
        
        viewlist=null;
        //init column list
        viewlist= getviewcolumns();
        
        int i=1;
        for(ViewColumn vc : viewlist){
        	try {
				if(!((vc.getTitle().equals("REF"))||(vc.getTitle().equals("VerRef")))){
					System.out.println(vc.getTitle()+" : "+vc.getItemName());
					XspViewColumn vcol;
					XspViewColumnHeader vhead;
				/*	if(i==1){
						link="link";
						chkbox=true;
						String expression10="#{javascript:var doc=viewentry.getDocument();\nsessionScope.put(\'userdocumentId\',doc.getUniversalID());\nvar c= getComponent(\'container\');\n\nuiuser.resetUIform(c, \"container\");}";		
						 ev10= CompUtil.createEventHandler("onclick", "partial", expression10, true,"container");
					}*/
					vcol=  CompUtil.createViewcolumn(viewpanel, vc.getItemName(), "viewColumn"+Integer.toString(i));
					
					 vhead= CompUtil.createViewcolumnheader(vcol, vc.getTitle(), "viewColumnHeader"+Integer.toString(i));
					vcol.setHeader(vhead);
					 Map facets=vcol.getFacets();
					
					facets.entrySet();
					//facets.put("header", vhead);
					 System.out.println(facets.values());
					//if(i==1){
						//vcol.getChildren().add(ev10);
					//}
				}
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
        }
        
		return viewpanel;
	}
	@SuppressWarnings("unchecked")
	private Vector<ViewColumn> getviewcolumns()  {
	
		View view= null;
		
		Vector<ViewColumn> listcolumn = null;
		
			//we need to reload the VIEW
		try {
			view =userdb.getView("userprofile");
			listcolumn=	view.getColumns();				        
				 
			  return listcolumn;
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
			
		
	}
	@SuppressWarnings("unchecked")
	private void createUser(){

		m_documentId=null;
		context=FacesContext.getCurrentInstance();
		requestscope = (Map) JSFUtil.getVariableValue("requestScope");
		System.out.println("requestscopevalue: "+requestscope.toString());
		sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		FacesContext.getCurrentInstance().getApplication();
		userpanel= new UIPanelEx();	
			
	
		System.out.println("abtout to get fields");
		fields = null;
		try {
			fields = getfields();
			if(requestscope.get("user")!=null){
				resetBean();
			}
		} catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		// int cnt=1;
		XspTable utable = new XspTable();

		utable.setId( "customDatatable" );

		int i=1;

		System.out.println(fields.length);
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

		//create action bar

		createActionBar(userpanel);
		userpanel.getChildren().add(utable); 
		
	}
	
	//For now creating form table is specific to need -- needs to be generalised
	
	@SuppressWarnings("unchecked")
	public void createFormTable(UIComponent parent,String[] flist,String id){
		XspTable utable = new XspTable();

		utable.setId( id );

		int i=1;

		System.out.println(fields.length);
		for(String v:flist){

			System.out.println(v);
			if(!((v.equals("Form"))||(v.equals("$UpdatedBy"))||(v.equals("$Revisions")))){
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
		createActionBar(parent);
		parent.getChildren().add(utable);
		
	}
	
	

	
	@SuppressWarnings("unchecked")
	private  void createActionBar(UIComponent com)
	{
		System.out.println("create ActionBar");
		UIPanelEx actionpanel = new UIPanelEx();
		//XspTable utable = new XspTable();
		actionpanel.setId( "actionTable" );
		UIPanelEx actionpanel2 = new UIPanelEx();		
		actionpanel2.setId( "actionTable2" );
		//Create Save Button
		String expression1="#{javascript:actions[\"save\"].execute(\"user\");}";
		
		XspCommandButton button1 =  CompUtil.createButton("Save","button1" );
		XspEventHandler ev1= CompUtil.createEventHandler("onclick", "complete", expression1, true,null);
		button1.getChildren().add(ev1);
		actionpanel.getChildren().add(button1);
		//Create Save and Close Button
		/*XspCommandButton button2= CompUtil.createButton("Save and Close","button2" );
		String expression2="#{javascript:actions[\"save\"].execute(\"user\");\ncontext.redirectToPage(\"UserView\");}";
		XspEventHandler ev2= CompUtil.createEventHandler("onclick", "complete", expression2, true,null);
		button2.getChildren().add(ev2);
		actionpanel.getChildren().add(button2);*/
		//Create New User Button
		XspCommandButton button2= CompUtil.createButton("New User","button2" );
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
		System.out.println("end Action bar creation");
	}
	
		public void getAddNewRow(UIComponent com, String nf){	
		newfield=nf;
			
				int i=0;
					
							XspTableRow row1 = CompUtil.createRow(com, "rowx"+i);
							for (int j=0 ; j<2; j++)
							{	
								XspTableCell cell = CompUtil.createCell(row1, "cellx"+i+j);
								switch(j)
								{
								case 0: CompUtil.createLabel(cell,newfield);
										break;
								case 1: CompUtil.createInput(cell, newfield,BEAN_NAME);
										break;
								}
							}	 		  
			 
					 		
			
		
		}
		
		public void removeRow(String sid ,String tid){
			try {
				UIComponent source=JSFUtil.findComponent(sid);
				UIComponent target=JSFUtil.findComponent(tid);
				source.getChildren().remove(target.getParent().getParent());
				PageDataBean data = (PageDataBean) ManagedBeanUtil.getBean(context,BEAN_NAME);
				tid=StringUtil.removeFirst(tid);
				
				data.setValue(tid, null);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	/*
	@SuppressWarnings("unchecked")
	private  void createLabel(UIComponent com,String value) 
	{
		XspOutputLabel lab = new  XspOutputLabel();
		lab.setValue(value);
		lab.setId("l"+value);
		lab.setFor("i"+value);		
		
		com.getChildren().add(lab);
	}
	
	private  void createInput(UIComponent com, Application app, String fieldname ) 
	{
		XspInputText inp = new XspInputText();
		inp.setId("i"+fieldname);
		//String ref = "#{"+user+"."+fieldname+"}";	
		
		ValueBinding vb1  = app.createValueBinding("#{user."+fieldname+"}");
		inp.setValueBinding( "value", vb1);
		// String tmp=(String) JSFUtil.getBindingValue(ref); //getValueBinding("#{"+user+"."+fieldname+"}");
		// ValueBinding vb1 = getValueBinding("#{"+user+"."+fieldname+"}");
		// if(vb1.equals(null)){System.out.println("vb is null");}
		// String tmp=(String)vb1.getValue(context);//)setValue(context, inp);
		// System.out.println(tmp);
		
		//vb1.setValue(context, fieldname);
			//inp.setValue(vb1.getValue(context));	
		 
		com.getChildren().add(inp);
	}
	
	
	public static XspTableRow createRow(UIComponent com, String id)
	{
		XspTableRow row = new XspTableRow();
		row.setId(id);
		com.getChildren().add(row);
		return row;
	}
	
	public static XspTableCell createCell(UIComponent com, String id)
	{
		XspTableCell cell = new XspTableCell();
		cell.setId(id);
		com.getChildren().add(cell);
		return cell;
	}
	
	 private UIComponent createButton1() {
         XspCommandButton result = new XspCommandButton();
         result.setValue("Save");
         result.setId("button1");
         XspEventHandler event1 =  (XspEventHandler) createEventHandler();
         result.getChildren().add(event1);
        
         return result;
     }

     private UIComponent createEventHandler() {
         XspEventHandler result = new XspEventHandler();
         String sourceId = "button1/xp:eventHandler[1]/xp:this.action[1]/text()";
         MethodBinding action = 
        	 app.createMethodBinding( "#{javascript:actions[\"save\"].execute(\"user\");\ncontext.redirectToPage(\"UserView\");}", null);
                
         result.setAction(action);
         result.setSubmit(true);
         result.setEvent("onclick");
         result.setRefreshMode("complete");
         return result;
     }*/
	
	
	
	public String getNewfield() {
			return newfield;
		}
		public void setNewfield(String newfield) {
			this.newfield = newfield;
		}
	/**
	 * The method loads the instance of the database document wrapped by
	 * this class
	 * 
	 * @return document or <code>null</code> if the document has not been saved before
	 * @throws NotesException 
	 * @throws NotesException
	 */
	@SuppressWarnings("unchecked")
	public String[] getfields() throws NotesException  {
		
		String[] a=null;
		String[] defaultform ={"Form","first_name","lmiddle_name","last_name",
				"contact","email","location"};
		Document doc=null;
		Vector listfield = null;
		System.out.println("inside gettimg fields");
		String documentId=getDocumentId();
		if ("".equals(documentId)){System.out.println("document id is null");return defaultform;}
		//if(documentId==""){System.out.println("document id is null");return defaultform;}
		//Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");	
	
		
			//we need to reload the document
			
		String bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			
			try {
				Database userdb = ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+BEAN_NAME+".nsf");
				doc=userdb.getDocumentByUNID(documentId);
				listfield = doc.getItems();
				
				 a= new String[listfield.size()]; 				 
				 
		          int i =0;
				  for(Object str : listfield){
					  a[i++] = str.toString();
					  System.out.println("Adding to arreay: "+str.toString());
				  }
				  a=StringUtil.mergeStringArrays( a,defaultform);
				 
				  return a;
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				listfield.removeAllElements();
				listfield=null;
				doc.recycle();
				doc=null;
				
			}
		
		
			//String[] a=(String[])fields.toArray(new String[fields.size()]);
			//cache it in the viewscope until it gets invalid
			//viewScope.put("curfields",a );
			//System.out.println("viewscopevalue: "+viewScope.toString());
		return a;
	}

	
	private static String getDocumentId() {
		
			
			System.out.println("sessionscopevalue: "+sessionscope.toString());
			System.out.println("reading doc id");
			Object temp= sessionscope.get("userdocumentId");
			String c2=temp.getClass().getName();
			//System.out.print(c2);
			String[] documentIdArr = new String[100];
			if(c2=="java.lang.String"){
				documentIdArr[0]=(String) temp;
				}else{
					documentIdArr= (String[]) temp;
				}
			
			if (documentIdArr==null || documentIdArr.length==0) {
				//we create a new document
				m_documentId="";
			}
			else {
				m_documentId=documentIdArr[0];
				System.out.println("documentId="+m_documentId);
			}
		
		return m_documentId;
	}
	
	

	
}
