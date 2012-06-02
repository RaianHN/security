package bsuite.weber.controller;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;

import javax.faces.context.FacesContext;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;

import bsuite.weber.tools.JSFUtil;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspInputText;
import com.ibm.xsp.component.xp.XspInputTextarea;
import com.ibm.xsp.dojo.DojoAttribute;

public class UIEntity {
	@SuppressWarnings("unchecked")
	private static Map viewscope;
	private static Map requestscope;
	private static int count1;
	
	
	private static UIComponent entity;
	private static DojoAttribute handle;
	private static DojoAttribute skip;
	@SuppressWarnings("unchecked")
	public UIEntity() {
		
		super();
		viewscope = (Map) JSFUtil.getVariableValue("viewScope");
		requestscope = (Map) JSFUtil.getVariableValue("requestScope");
		count1 = 0;
	}
	
		public UIComponent getEntity(){
			handle = new DojoAttribute();
			handle.setName("handle");
			skip = new DojoAttribute();
			skip.setName("skip");
			UIEntity.createEntity(null);
			return entity;
			
		}
	  public static void createEntity(UIComponent com)
	 	{	try {
			entity = null;			
				System.out.println("new field");
			  if (viewscope.get("viewcounter")==""||viewscope.get("viewcounter")==null)
			  {
				  viewscope.put("viewcounter", count1);
			  }
			// StateMap m = com._xspGetStateMap();
			// m.getEntry()
			  System.out.println("viewscopevalue: "+viewscope.toString());
			  System.out.println("requestscopevalue: "+requestscope.toString());
			  ;
			  
			  
			  String temp= viewscope.get("viewcounter").toString();
				 System.out.println("viewscopevalue: "+viewscope.toString());
				count1=Integer.parseInt(temp);
				count1++;
				UIPanelEx opan = new UIPanelEx();
				UIPanelEx pan = new UIPanelEx();
				XspInputTextarea inp = new XspInputTextarea();
				inp.setId("i"+Integer.toString(count1));
				opan.setId("opanI"+Integer.toString(count1));
				String ref = "#{requestScope."+"i"+Integer.toString(count1)+"}";
				List<DojoAttribute> x = new ArrayList<DojoAttribute>();
				pan.setId("draghandle"+Integer.toString(count1));
				String id=pan.getClientId(FacesContext.getCurrentInstance());//temp
				//handle.setValue("view:_id1:draghandle"+Integer.toString(count1));
				handle.setValue(id);
				skip.setValue("true");
				x.add(handle);
				x.add(skip);
				ValueBinding vb1  = FacesContext.getCurrentInstance().getApplication().createValueBinding(ref);
				 inp.setValueBinding( "value", vb1);
				 inp.setStyleClass("note");
					opan.setDojoType("dojo.dnd.Moveable");
					
					opan.setDojoAttributes(x);
					
					
					
					//pan.setStyle("Width:100px,Height:100px");
					pan.setStyleClass("noteHandle");
					
					
					opan.getChildren().add(pan);
					opan.getChildren().add(inp);
				 if(com!=null){
					System.out.println("new field");
					com.getChildren().add(opan);
				
					
				 }
				 
					viewscope.put("viewcounter", count1);
				
				entity = opan;
		} catch (ReferenceSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 			
	 	}
}
