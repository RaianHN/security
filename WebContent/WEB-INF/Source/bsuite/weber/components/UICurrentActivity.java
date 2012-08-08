package bsuite.weber.components;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.CompUtil;
import bsuite.weber.tools.JSFUtil;
import bsuite.weber.tools.StringUtil;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspTable;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import com.ibm.xsp.util.ManagedBeanUtil;

public class UICurrentActivity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8042866678261204991L;
	private static String m_documentId;
	private UIPanelEx actpanel;
	public static FacesContext context;
	private  String BEAN_NAME;
	private String[] fields;
	@SuppressWarnings("unchecked")
	private static Map requestscope;
	@SuppressWarnings("unchecked")
	private static Map sessionscope;
	
	
	
	
	public void resetUI(UIComponent com,FacesContext context,String page){
		CompUtil.create(com, context, page);
	}
	

public void resetBean() {
		
		System.out.println("calling reset");
		BEAN_NAME=(String) sessionscope.get("Beanid");
		Object active = ManagedBeanUtil.getBean(context,BEAN_NAME);
		try {
			Class<?> c= active.getClass();
			//System.out.println("class");
			Method m = c.getMethod("reset",null);
			m.invoke(active, null);
			
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
	private void initfieldslist(){
	 fields=null;
	 try {
		fields= getfields();
		if(requestscope.get("activity")!=null){
			resetBean();
		}
	} catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
	

	@SuppressWarnings("unchecked")
	public UIPanelEx getActivityPanel()
	{
		actpanel = null;
		m_documentId=null;
		context=FacesContext.getCurrentInstance();
		FacesContext.getCurrentInstance().getApplication();
		requestscope = (Map) JSFUtil.getVariableValue("requestScope");
		sessionscope = (Map) JSFUtil.getVariableValue("sessionScope");
		BEAN_NAME= (String) sessionscope.get("Beanid");
		System.out.println("Starting active panel");
		if (actpanel == null) {
			System.out.println("Populate active panel");			
			this.createActivity();			// Populate Activity panel

		}
		
		return actpanel;// return dynamicDataTableGroup;
	}


	@SuppressWarnings("unchecked")
	private void createActivity() {
		
		
		System.out.println("requestscopevalue: "+requestscope.toString());
		
		actpanel= new UIPanelEx();	
		this.initfieldslist();
		XspTable utable = new XspTable();

		utable.setId( "customDatatable" );

		int i=1;

		System.out.println(fields.length);
		for(String v:fields){

			System.out.println(v);
			if(!((v.equals("Form"))||(v.equals("form"))||(v.equals("$UpdatedBy"))||(v.equals("$Revisions"))||(v.equals("display_field")))){
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

		createActionBar(actpanel);
		actpanel.getChildren().add(utable); 

		
	}
	@SuppressWarnings("unchecked")
	private void createActionBar(UIPanelEx actpanel2) {
		try {
			System.out.println("create ActionBar");
			UIPanelEx actionpanel = new UIPanelEx();
			//XspTable utable = new XspTable();
			actionpanel.setId( "actionTable" );
			UIPanelEx actionpanel2 = new UIPanelEx();		
			actionpanel2.setId( "actionTable2" );
			//Create Save Button
			String expression1="#{javascript:actions[\"save\"].execute(\"activity\");}";
			
			XspCommandButton button1 =  CompUtil.createButton("Save","button1" );
			XspEventHandler ev1= CompUtil.createEventHandler("onclick", "complete", expression1, true,null);
			button1.getChildren().add(ev1);
			actionpanel.getChildren().add(button1);
			
			//Create New Activity Button
			XspCommandButton button2= CompUtil.createButton("New Activity","button2" );
			String expression2="#{javascript:sessionScope.put('actdocumentId',\"\");uiactivity.resetBean();var c= getComponent(\'container\');uiactivity.resetUI(c, facesContext,\"/ccActivity.xsp\");}";
			XspEventHandler ev2= CompUtil.createEventHandler("onclick", "partial", expression2, true,"container");
			button2.getChildren().add(ev2);
			actionpanel.getChildren().add(button2);
			actpanel2.getChildren().add(actionpanel);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		String[] defaultform ={"Form","subject"};
		Document doc=null;
		Vector listfield = null;
		System.out.println("inside gettimg fields");
		String documentId=getDocumentId();
		if ("".equals(documentId)){System.out.println("document id is null");return defaultform;}
		//if(documentId==""){System.out.println("document id is null");return defaultform;}
		
	
		
			//we need to reload the document
			
		String bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			
			try {
				Database actdb = ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+BEAN_NAME+".nsf");
				doc=actdb.getDocumentByUNID(documentId);
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
				
				e.printStackTrace();
			}finally{
				listfield.removeAllElements();
				listfield=null;
				doc.recycle();
				doc=null;
				
			}
		
		
		
		return a;
	}

	
	private static String getDocumentId() {
		
			//read query parameter 'documentId'
			//Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
			System.out.println("sessionscopevalue: "+sessionscope.toString());
			System.out.println("reading doc id");
			Object temp= sessionscope.get("actdocumentId");
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
