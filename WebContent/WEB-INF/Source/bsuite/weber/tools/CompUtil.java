package bsuite.weber.tools;


import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.component.xp.XspEventHandler;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspOutputLabel;
import com.ibm.xsp.component.xp.XspPager;
import com.ibm.xsp.component.xp.XspTableCell;
import com.ibm.xsp.component.xp.XspTableRow;
import com.ibm.xsp.component.xp.XspViewColumn;
import com.ibm.xsp.component.xp.XspViewColumnHeader;
import com.ibm.xsp.component.xp.XspViewPanel;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoBorderPane;
import com.ibm.xsp.model.domino.DominoViewData;
import com.ibm.xsp.page.compiled.PageExpressionEvaluator;

public class CompUtil {
	
	
	@SuppressWarnings("unchecked")
	public static void createLabel(UIComponent com,String value) 
	{
		XspOutputLabel lab = new  XspOutputLabel();
		lab.setValue(value);
		lab.setId("l"+value);
		lab.setFor("i"+value);		
		
		com.getChildren().add(lab);
	}
	
	@SuppressWarnings("unchecked")
	public static void createLabel(UIComponent com,String value,boolean visible) 
	{
		XspOutputLabel lab = new  XspOutputLabel();
		lab.setValue(value);
		lab.setId("l"+value);
		lab.setFor("i"+value);		
		lab.setRendered(visible);
		com.getChildren().add(lab);
	}
	
	@SuppressWarnings("unchecked")
	public static   void createInput(UIComponent com,  String fieldname ,String arg) 
	{
		System.out.println("inside createINput");
		XspInputText inp = new XspInputText();
		inp.setId("i"+fieldname);
		String ref = "#{"+arg+"."+fieldname+"}";	
		System.out.println("String ref "+ref);
		
	ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref);
	 inp.setValueBinding( "value", vb1);
		
	
		com.getChildren().add(inp);
	}
	
	
	@SuppressWarnings("unchecked")
	public static  XspTableRow createRow(UIComponent com, String id)
	{
		XspTableRow row = new XspTableRow();
		row.setId(id);
		com.getChildren().add(row);
		return row;
	}
	
	@SuppressWarnings("unchecked")
	public static  XspTableCell createCell(UIComponent com, String id)
	{
		XspTableCell cell = new XspTableCell();
		cell.setId(id);
		com.getChildren().add(cell);
		return cell;
	}
	
	public static  XspCommandButton createButton(String value,String id) {
         XspCommandButton result = new XspCommandButton();
         result.setValue(value);
         result.setId(id);       
        
         return result;
     }

	public static  XspEventHandler createEventHandler(String event,String refresh, String expression,Boolean submit,String refreshid) {
         XspEventHandler result = new XspEventHandler();        
         MethodBinding action = 
        	 FacesContext.getCurrentInstance().getApplication().createMethodBinding(expression, null);
                
         result.setAction(action);
         result.setSubmit(submit);
         result.setEvent(event);
         result.setRefreshMode(refresh);
         if(refreshid!=null){
        	 result.setRefreshId(refreshid);
         }
         return result;
     }
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static void create(UIComponent r, FacesContext s, String cc)
	{
		try {
			int childcount=r.getChildCount();
			if(childcount>1 || childcount==1){
				for(int i=(childcount-1);i>=0;i--){
					r.getChildren().remove(i);
				}
			}			

			UIIncludeComposite result = new UIIncludeComposite();
			result.setPageName(cc);
			ControlBuilder objBuilder = new ControlBuilder();
			ControlImpl con = new ControlImpl(r);
			ControlImpl con1 = new ControlImpl(result);
			con.addChild(con1);
			objBuilder.buildControl(s,con,true);
		} catch (FacesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
	
	 public static UIDojoBorderContainer createDjbordercontainer(UIComponent r, String id) {
         UIDojoBorderContainer result = new UIDojoBorderContainer();
         result.setStyle("height:682px;width:100%;");
        result.setId(id);//"djBorderContainer1"
        r.getChildren().add(result);
        return result;
     }

     public static UIDojoBorderPane createDjborderpanel(UIComponent r,  String id,String position,Boolean split) {
         UIDojoBorderPane result = new UIDojoBorderPane();
         result.setStyle("WIDTH:139PX;");
         result.setRegion(position);
         result.setSplitter(split);
         result.setId(id);// setId(result, "djBorderPane1");
         r.getChildren().add(result);
         return result;
     }
	
     public static UIViewRoot getRoot(){
    	 return FacesContext.getCurrentInstance().getViewRoot();
     }
     //To be generalised
     public static XspViewPanel createViewpanel( UIComponent parent, String id, String var) {
         XspViewPanel result = new XspViewPanel();
         result.setVar(var);//"viewentry"
         result.setRows(30); 
         result.setId(id);//"viewPanel1"  
         XspPager pager = createPager1(result);
         result.setHeader(pager);
         parent.getChildren().add(result);
         return result;
     }
     
     public static DominoViewData createDominoViewData(String db,String view, String var){
    	 DominoViewData data = new DominoViewData();
    	 
    	 data.setDatabaseName(db);
    	 data.setViewName(view);         
         data.setVar(var);
        // String searchExpr = "#{javascript:requestScope.get(\"findme\");}";
     	 //ValueBinding search  = FacesContext.getCurrentInstance().getApplication().createValueBinding(searchExpr);
         //data.setValueBinding("search", search); 
    	 return data;
     } 
     
     public static XspViewColumn createViewcolumn(UIComponent parent,String columnname, String id, String link,Boolean chkbox) {
         XspViewColumn result = new XspViewColumn();
         result.setShowCheckbox(chkbox);
         result.setColumnName(columnname);
         result.setDisplayAs(link);
         result.setId(id);
       
         if(parent!=null){
         parent.getChildren().add(result);
         }
         return result;
     }
     public static XspViewColumn createViewcolumn(XspViewPanel parent,String columnname, String id) {
         XspViewColumn result = new XspViewColumn();        
         result.setColumnName(columnname);         
         result.setId(id);   
         
         //result.setValue("hi");
         parent.getChildren().add(result);      
         return result;
     }

     public static XspViewColumnHeader createViewcolumnheader(XspViewColumn parent,String value, String id ) {
         XspViewColumnHeader result = new XspViewColumnHeader();
      // result.setParent(parent);
         //result.setTitle(value);
    
         result.setValue(value);
         result.setId(id);    
        //parent.getFacets().put("header", result);
        // parent.getFacet("header").getChildren().add(result);
          // parent.getChildren().add(result);
             
         return result;
     }
     public static XspPager createPager1(UIComponent parent) {
         XspPager result = new XspPager();
         result.setPartialRefresh(true);
         result.setLayout("Previous Group Next");
         result.setId( "pager1");
         return result;
     }
   
     
}
