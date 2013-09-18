package bsuite.weberon;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;



import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.component.xp.XspEventHandler;

import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.outline.UIOutlineDropDownButton;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;




public class DynamicCC {
	public static void loadCC(FacesContext s, UIComponent component, String c1, String id)
	{	
	
	
	
	UIIncludeComposite result = new UIIncludeComposite();        
	        result.setPageName(c1);        
	        result.setId(id);
	        
	        ControlImpl con = new ControlImpl(component);
			ControlImpl con1 = new ControlImpl(result);
			
	con.addChild(con1);
	
	
	
	ControlBuilder.buildControl(s,con,true);
	
	}
	
	public static void loadCCinTab(FacesContext s, UIComponent component, String c1, String id,String tabtitle)
	{	
		
		UIIncludeComposite result = new UIIncludeComposite();        
	    result.setPageName(c1);        
	    result.setId(id);
	        
	    //Adding new tab panel
	    UIDojoTabPane pane2 = new UIDojoTabPane();
  	  pane2.setTitle(tabtitle);
  	  pane2.setId(tabtitle);
  	  pane2.setClosable(true);
  	  component.getChildren().add(pane2);
	    
	   ControlImpl con = new ControlImpl(pane2);
	ControlImpl con1 = new ControlImpl(result);
			
			
			
	con.addChild(con1);
			
	ControlBuilder.buildControl(s,con,true);
	}
	
	public static void removePreview(UIComponent r){
		
		
		int childcount=r.getChildCount();
		if(childcount>1 || childcount==1){
			for(int i=(childcount-1);i>=0;i--){
				r.getChildren().remove(i);
			}
		}
	}
	
	
	public static void createDropDown(UIComponent component){
		
		  UIOutlineDropDownButton result = new UIOutlineDropDownButton();
		  result.setId("dropDownButton1");
          ComplexContainerTreeNode treeNodes = new ComplexContainerTreeNode();
          treeNodes.setComponent(result);
          treeNodes.setLabel("Create");
          ComplexLeafTreeNode children = new ComplexLeafTreeNode();
          children.setComponent(result);
          children.setLabel("Employee");
          children.setSubmitValue("employee");
                
          treeNodes.addChild(children);       
      
         String onItemClickExpr = "#{javascript:viewScope.ppChoice=context.getSubmittedValue()\nprintln(\"Selected Choice \",viewScope.ppChoice);\n}";
    
      	
      	
      /*  XspEventHandler event = new XspEventHandler();        
        MethodBinding action = 
       	 FacesContext.getCurrentInstance().getApplication().createMethodBinding(onItemClickExpr, null);
          event.setAction(action);              
          event.setSubmit(true);
          event.setEvent("onItemClick");
          event.setRefreshMode("partial");
          event.setRefreshId("panel1");*/
      	
      	
      	
      	XspEventHandler ev2= CompUtil.createEventHandler("onItemClick", "partial",onItemClickExpr, true,"panel1");
      	result.getChildren().add(ev2);
      	
      	//result.setValueBinding("onItemClick", vb2);
         // result.getChildren().add(treeNodes);
         // return result;
      	result.addNode(treeNodes);
          component.getChildren().add(result);
		
		
	}
	
	
	
}
