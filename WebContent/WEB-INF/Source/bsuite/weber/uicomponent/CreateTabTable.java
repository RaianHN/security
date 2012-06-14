package bsuite.weber.uicomponent;

import javax.faces.component.UIComponent;

import bsuite.weber.tools.CompUtil;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.rest.UIBaseRestService;

public class CreateTabTable {
	
	
	public static UIComponent createTT(UIComponent com){
		UIPanelEx panel=new UIPanelEx();
		
		  UIDojoTabContainer tabcon = new UIDojoTabContainer();
		  tabcon.setStyle("height:300px;width:300px;");
		  tabcon.setId("uiDojoTabContainer1");		  	    
		  
		  UIDojoTabPane pane1 = new UIDojoTabPane();
         pane1.setTitle("HOME");
          pane1.setId("djTabPane1");
          
          
       /*   
		  UIDojoTabPane pane2 = new UIDojoTabPane();
         pane2.setTitle("Employees");
          pane2.setId("djTabPane2");
         // pane2.setRendered(false);
          XspCommandButton button1=CompUtil.createButton("Add", "abc");
          button1.setStyle("background-color:rgb(192,192,192)");
          pane2.getChildren().add(button1);
          
*/          
          tabcon.getChildren().add(pane1);      
          panel.getChildren().add(tabcon);
		com.getChildren().add(panel);
		
		return tabcon;
		
		
	}
	
      public static UIComponent createTabPane(UIComponent tabcont, String title ){
    	  UIDojoTabPane pane2 = new UIDojoTabPane();
    	  pane2.setTitle(title);
    	  pane2.setId(title);
    	  tabcont.getChildren().add(pane2);
    	  return pane2;
      }
      
}
