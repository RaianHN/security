package com.weberon;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import bsuite.weber.loadcc.BSUtil;
import bsuite.weber.loadcc.CompUtil;
import bsuite.weber.loadcc.JSFUtil;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspColumn;
import com.ibm.xsp.component.xp.XspDataTableEx;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspOutputLink;
import com.ibm.xsp.component.xp.XspPager;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGrid;
import com.ibm.xsp.extlib.component.dojo.grid.UIDojoDataGridColumn;
import com.ibm.xsp.extlib.util.ExtLibUtil;



/**
 * @author SBangalorkar
 *
 */
public class DynamicTable {
	private XspDataTableEx dataTb ;
	private UIPanelEx viewPanel;
	private Session sessionObj;
	private String servName;
	private static int linkc=0;
	private static int colc=0;
	public static String tableid="tabledemo";
	
	 public static String headerexp=  "#{javascript:toggleSortOrder(\'"+tableid+"\' ," ;
	 
	 
	 
	public UIPanelEx getDyTable(){
		
		
		viewPanel = new UIPanelEx();
		viewPanel.setId("v1");
		dataTb = null ;//**
		
		String valueExpr = "#{javascript:var currentdb=session.getCurrentDatabase();\nvar serv=currentdb.getServer();\nvar dbpath=currentdb.getFilePath();\nvar pathname=@LeftBack(dbpath,12);\n\nvar tadb : NotesDatabase=session.getDatabase(serv, pathname + \"customer.nsf\");\nprintln(tadb.getFileName());\nViewEntriesSearch(tadb, \'Xview\');}";
		
		//For table value
        
        
		try {
			dataTb=CompUtil.createDataTable(viewPanel, tableid, "ticket", valueExpr);
			dataTb.setStyleClass("bstDataTable");
			dataTb.setRowClasses("even,odd");
			dataTb.setHeaderClass("xspPanelViewColumnHeader");
			dataTb.setRows(25);
			createColumns(dataTb);
			
		} catch (NotesException ne) {
			// TODO Auto-generated catch block
			ne.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return viewPanel;
		
	}
	
	public  void createColumns(XspDataTableEx com)throws NotesException
	{
		 View view = null;//**
		 Document doc = null;//**
		
		try
		{
			doc= getviewProfile();
			String viewName1 = null;
			Vector cols = null;
			if (doc != null)
			{
				
				viewName1 = doc.getItemValueString("viewName");
				if (viewName1 != null && viewName1.equalsIgnoreCase("Xview"))
				{
					cols = doc.getItemValue("sColumns");
		//			break;
				}
		
				String hdexp;
				String arg="ticket";
				String linkexp = "#{javascript:"+arg+".getColumnValues()[";
			
				if (cols != null)
				{
			
					int cnt = cols.size();
					System.out.println(Integer.toString(cnt));
					for (int i=0; i< cnt; ++i){	
						String lv= (String)cols.get(i);						
						String[] a= lv.split("#");					
						XspColumn hcol =CompUtil.createDataColumn(com , Integer.toString(colc++)); //create column

						hdexp= headerexp+ a[0]+")}";
						UIComponent header = this.createHeaderLink(a[1],Integer.toString( linkc++));    // create a header comp
						header.getChildren().add(CompUtil.createEventHandler("onclick", "partial", hdexp, true,tableid ));//

						hcol.setHeader(header);
						if(i==0){
							UIComponent doclink = CompUtil.createOpenDocLink(hcol, a[0], linkexp, Integer.toString( linkc++));
							//UIComponent doclink = this.createLink(a[0],Integer.toString( linkc++));    // create a header comp
							doclink.getChildren().add(this.createEventhandler());//

						}else{
						CompUtil.createTabComputedfield(hcol, a[0], arg, Integer.toString(i));
						}
					
					
					}
			 
				}
				
			}	
			
			
			
		}		
		catch (NotesException ne) 
		{
			// TODO Auto-generated catch block
			ne.printStackTrace();
		}
	catch (Exception e){
		e.printStackTrace();
	}
		finally{
		//	view.recycle();//**
			doc.recycle();//**
		//	view = null;//**
			doc = null;//**
		}
		
    
		
		
	}
	
	public  Document getviewProfile(){
		 sessionObj = ExtLibUtil.getCurrentSession();
			View v1;
			try {
				servName = ExtLibUtil.getCurrentDatabase().getServer();		
				
				v1 = sessionObj.getDatabase(servName, "Test/customer.nsf").getView("Testview");
				return v1.getFirstDocument();
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
			
		
		
	}
	
	public static void refreshColumns(XspDataTableEx table, String[] columns){
		int cnt = table.getChildCount();
		for (int j=0; j< cnt; ++j)
		{
			
			table.getChildren().remove(0);
			
			
		}
		String hdexp;
		int len = columns.length;
		for (int i=0; i< len; ++i)
		{
			String lv= columns[i];			
			String[] a= lv.split("#");
			XspColumn result = CompUtil.createDataColumn(table , Integer.toString(i));

			hdexp= headerexp+ a[0]+")}";
			UIComponent header = createHeaderLink(a[1],Integer.toString( i));    // create a header comp
			result.setHeader(header);
			CompUtil.createTabComputedfield(result, a[0], "ticket", Integer.toString(i));
			
			
		}
	}
	
	private static UIComponent createEventhandler(){
		

		XspEventHandler result = new XspEventHandler();
		String expression="#{javascript:var doc : NotesDocument = ticket.getDocument();\nsessionScope.docunid = doc.getUniversalID();\nvar ccname=\"/ccPreviewTT.xsp\";\n\nvar ticket=\"Ticket\";\nsessionScope.ttno=\"Number: \" + doc.getItemValueString(\"Troubleno_Tt\");\nif (getComponent(\"Ticket\") == null)\n{\nvar r = getComponent(\'container\'); \nvar s = facesContext; \nvar ccheader=\"/ccHeader.xsp\";\ncom.weberon.DynamicCC1.createPortlet(s, r,ccheader,ccname,\"Ticket\",\"Ticket\");\n var l = getComponent(\'linklist\');\n com.weberon.DynamicCC1.createLink(l, \"Ticket\", \"Ticket\"+\"link\");\n}\nelse\n{\n \tif(viewScope.moveableToggle==\"1\"){\n \t\tvar parent = getComponent(\'container\');\t\n\t  \tvar child = getComponent(\"Ticket\"+\"moveable\");\n\t  \tparent.getChildren().add(0, child);\n \t}else if(viewScope.moveableToggle==\"0\"){\n \t\tvar parent = getComponent(\'container\');\n \t\tvar child = getComponent(\"Ticket\");\n \t\tparent.getChildren().add(0, child); \n \t\t\n \t}\n}}";
	     
        MethodBinding action1 =    FacesContext.getCurrentInstance().getApplication().createMethodBinding(expression, null);
        
        result.setAction(action1);
        result.setRefreshId("mainpane");
        String onCompleteExpr = "XSP.partialRefreshGet(\"#{id:banner1}\");";
        ValueBinding onComplete = FacesContext.getCurrentInstance().getApplication().createValueBinding( onCompleteExpr);
        result.setValueBinding("onComplete", onComplete);
        result.setSubmit(true);
        result.setEvent("onclick");
        MethodBinding script =FacesContext.getCurrentInstance().getApplication().createMethodBinding(
                "zIndex++;\nXSP.executeOnServer(\'view:_id1:eventHandler2\', \'\', \'\', zIndex);",
                null);
        result.setScript(script);
        result.setRefreshMode("partial");
        return result;

		
		//return null;
		
	}
	
	private static UIComponent createHeaderLink(String label,String id){
		 XspOutputLink result = new XspOutputLink();
         result.setEscape(true);
         result.setId("link"+id);
         result.setText(label);
         return result;
	 }
	
	
}
