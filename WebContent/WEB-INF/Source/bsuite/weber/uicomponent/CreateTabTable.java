package bsuite.weber.uicomponent;

import javax.faces.component.UIComponent;

import bsuite.weber.tools.CompUtil;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspCommandButton;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabContainer;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;
import com.ibm.xsp.extlib.component.rest.UIBaseRestService;

public class CreateTabTable {
	
	
	@SuppressWarnings("unchecked")
	public static UIComponent createTT(UIComponent com, boolean homePanel){
		UIPanelEx panel=new UIPanelEx();
		
		  UIDojoTabContainer tabcon = new UIDojoTabContainer();
		  tabcon.setStyle("height:682px;width:100%;");
		  tabcon.setId("MainTabContainer");		  	    
		  
		  UIDojoTabPane pane1 = new UIDojoTabPane();
         pane1.setTitle("HOME");
           pane1.setId("Home");
           
          if(homePanel){
        	  tabcon.getChildren().add(pane1);    //Add home panel if required 
          }
            
          panel.getChildren().add(tabcon);
		com.getChildren().add(panel);
		
		return tabcon;
		
		
	}
	
      @SuppressWarnings("unchecked")
	public static UIComponent createTabPane(UIComponent tabcont, String title ){
    	  UIDojoTabPane pane2 = new UIDojoTabPane();
    	  pane2.setTitle(title);
    	  pane2.setId(title);
    	  tabcont.getChildren().add(pane2);
    	  return pane2;
      }
      
}
