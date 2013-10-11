package bsuite.loadcc;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIIncludeComposite;

import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.component.dojo.layout.UIDojoTabPane;




public class DynamicCC {
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
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
}
