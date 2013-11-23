package bsuite.weberon;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspDiv;
import com.ibm.xsp.component.xp.XspOutputLink;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.component.outline.UIOutlineBreadCrumbs;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;

public class DynamicCC1 {
	
	@SuppressWarnings("unchecked")
	public static void createPortlet(FacesContext s, UIComponent component, String c1, String c2, String id, String title)
	{
		try{
			
		
		UIPanelEx panel = new UIPanelEx();
		panel.setDojoType("dojox.widget.Portlet");
		panel.setTitle(title);
		panel.setStyle("height: auto ; width: auto");
		panel.setId(id);
		
		
		
		
		
		
		
		UIIncludeComposite result = new UIIncludeComposite();
	    result.setPageName(c1);
	    result.setId(id+"header");
	    
        UIIncludeComposite result1 = new UIIncludeComposite();        
        result1.setPageName(c2);        
        result1.setId(id+"pane");
        result1.setStyleClass("borderpane");
        ControlImpl con = new ControlImpl(panel);
		ControlImpl con1 = new ControlImpl(result);
		ControlImpl con2 = new ControlImpl(result1);
		
		con.addChild(con1);
		con.addChild(con2);
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		
		String toggle=(String) viewScope.get("moveableToggle");
		
		if (toggle.equals("0")){
		
			component.getChildren().add(0, panel);	
				
			
		}else if(toggle.equals("1")){
			
			UIPanelEx panelmoveable = new UIPanelEx();
			panelmoveable.setId(id+"moveable");
			
			component.getChildren().add(0,panelmoveable);
			panelmoveable.getChildren().add(0, panel);
		
			String position=(String) viewScope.get("position");
			String zIndex=(String) viewScope.get("zIndex");
			
			panelmoveable.setStyle("position: absolute;  left:"+position+"px; top:"+position+"px ;z-index:"+zIndex+";");
			
			int val= Integer.parseInt(position);
			val=val+20;
			if (val>110){
				val=10;
			}
			
			viewScope.put("position", Integer.toString(val));
			viewScope.put("zIndex",Integer.toString((Integer.parseInt(zIndex)+1)));	
			
		}
		ControlBuilder.buildControl(s,con,true);
		
		}catch (Exception e) {
			e.printStackTrace();
			

		}
	}
	
	
	
	public static void removePortlet(UIComponent container, UIComponent portlet)
	{
		 container.getChildren().remove(portlet);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void createLink(UIComponent component, String comID, String linkID)
	{
		//Addlink to breadcrumb
		XspOutputLink link = new XspOutputLink();
		link.setText(comID);
		link.setId(linkID);
		UIComponent comp1=JSFUtil.findComponent(comID);
		String cid=comp1.getClientId(FacesContext.getCurrentInstance());
		
		String onClick="jumplink('"+cid+"');";
		
		
		link.setOnclick(onClick);
		
		
		component.getChildren().add(link);
		addCrumb(onClick,comID);
		
       
       
	}
	public static void addCrumb(String onClick, String label){
		UIOutlineBreadCrumbs bc=(UIOutlineBreadCrumbs) JSFUtil.findComponent("breadCrumbs1");
		
		ComplexLeafTreeNode node= new ComplexLeafTreeNode();
		node.setEnabled(true);
		node.setLabel(label);
	
		node.setOnClick(onClick);
	
		bc.addNode(node);
		
	}
	
	public static void setStyle(UIComponent com, String left, String top, String zindex)
	{
			((UIPanelEx) com).setStyle("position: absolute;  left:" + left + "; top:" + top+ "; z-index:"+zindex+";" );
	}
	
	
	
	public static void setPosition(UIComponent com, String display, String height, String width)
	{
		
		if (display == null)
		{
			((UIIncludeComposite) com).setStyle("display:" +" " +"; height:"+height+ "; width:"+ width);
			
		}
		else 
		{
			((UIIncludeComposite) com).setStyle("display:"+ display + "; height:"+height+ "; width:"+ width);
			
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static void setPanelvisible(UIComponent com){
		Map viewscope = (Map) JSFUtil.getVariableValue("viewScope"); 
		viewscope.put("msg","1" );
		((UIPanelEx) com).setStyle("visibility:visible;z-index:999");
		 
		
		
	}
	public static void setPanelinvisible(UIComponent com){
		((UIPanelEx) com).setStyle("visibility:hidden");
	}
	
	public static void removeLink(UIComponent linkcontainer, UIComponent link)
	{
		
			removecrumb(link.getId());
			 linkcontainer.getChildren().remove(link);
	}
	@SuppressWarnings("unchecked")
	public static void removecrumb(String label)
	{
		try {
			UIOutlineBreadCrumbs bc=(UIOutlineBreadCrumbs) JSFUtil.findComponent("breadCrumbs1");
			

			List<ITreeNode> list=bc.getTreeNodes()	;
			ListIterator itr = list.listIterator(); 
			
			while(itr.hasNext()) {

				ComplexLeafTreeNode element = (ComplexLeafTreeNode) itr.next(); 
				
				String glb= element.getLabel()+"link";
			    if(glb.equals(label)){
			    	itr.remove();
			    	return;
			    }
			    

			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
			
	}
	@SuppressWarnings("unchecked")
	public static void removeothercrumb(UIComponent link){
		String label = link.getId();
		
		UIOutlineBreadCrumbs bc=(UIOutlineBreadCrumbs) JSFUtil.findComponent("breadCrumbs1");
		List<ITreeNode> list=bc.getTreeNodes()	;
		ListIterator itr = list.listIterator(); 
		
		while(itr.hasNext()) {

			ComplexLeafTreeNode element = (ComplexLeafTreeNode) itr.next(); 
			
			String glb= element.getLabel()+"link";
		    if(!(glb.equals(label))){
		    	
		    	itr.remove();
		    	
		    }
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void setCompDojoType(UIComponent linkcontainer,UIComponent topcomp){
		
		List list=linkcontainer.getChildren();
		FacesContext context = FacesContext.getCurrentInstance();
		Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		
		for (int i=1;i<=list.size();i++){
			XspOutputLink link =(XspOutputLink) list.get(i-1);
			String id=link.getText();
			
			UIPanelEx portlet= (UIPanelEx) JSFUtil.findComponent(topcomp,id);
			UIPanelEx portletmovable= (UIPanelEx) JSFUtil.findComponent(topcomp,id+"moveable");
			
			if (portletmovable==null)
			{
				UIPanelEx moveableportlet= new UIPanelEx();
				moveableportlet.setId(id+"moveable");
				moveableportlet.setDojoType("dojo.dnd.Moveable");
				moveableportlet.getChildren().add(portlet);
				topcomp.getChildren().add(0, moveableportlet);
				viewScope.put("moveableToggle", "1");
				
			}else if(portletmovable.getChildCount()==0)
			{
				portletmovable.getChildren().add(portlet);
				topcomp.getChildren().add(0, portletmovable);
				viewScope.put("moveableToggle", "1");
				
			}else{
				topcomp.getChildren().add(portlet);
				JSFUtil.removeComponent(id+"moveable");
				viewScope.put("moveableToggle", "0");
				
			}
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "static-access" })
	public static void createPreview(UIComponent r, FacesContext s, String cc)
	{
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
		
	}
	public static void removePreview(UIComponent r){
		int childcount=r.getChildCount();
		if(childcount>1 || childcount==1){
			for(int i=(childcount-1);i>=0;i--){
				r.getChildren().remove(i);
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static void restore(String linklist)
	{
	
		XspDiv con= (XspDiv) JSFUtil.findComponent("container");
		XspDiv listdiv=(XspDiv) JSFUtil.findComponent("linklist");
		FacesContext context = FacesContext.getCurrentInstance();
		Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		String[] listval=linklist.split(";");
		viewScope.put("zIndex", "0");
		
		
		for (int i=1; i<=listval.length; i++){
			viewScope.put("zIndex", Integer.toString(i));
			createPortlet(context, con, "ccHeader.xsp", "cc"+listval[i-1]+".xsp", listval[i-1], listval[i-1]);
			createLink(listdiv, listval[i-1], listval[i-1]+"link");
		}
	}
	
	public static String[] sortData(String str[])
	{
		String ret[] =null;
		
		try {
			int cnt = str.length;
			SortData sarr[] = new SortData[cnt]; 
			for (int i=0; i< cnt; ++i)
			{
				String []str1 = str[i].split("-");
				SortData data = new SortData(str1[0], str1[1]);
				sarr[i] = data;

				
			}
			
			
			SortData []sorted = sort(sarr);
			
			
			 ret = new String[cnt];
			for (int i=0; i<cnt ; ++i)
			{
				ret[i] = sorted[i].getIndex()+"-"+ sorted[i].getZindex();
				
				
			}
			return ret ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret ;
	}
	
	
	
	 @SuppressWarnings("unchecked")
	public static SortData[] sort(SortData[]input)
		
		{
			Arrays.sort(input, new Compare());
			
			return input;
		}
 
	 
	 
	 
	 @SuppressWarnings("unchecked")
	public static void setCompDojoType2(UIComponent topcomp, String[] sorted){
			
			
			FacesContext context = FacesContext.getCurrentInstance();
			Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			
			String toggle = (String) viewScope.get("moveableToggle");
			if ( toggle.equalsIgnoreCase("1"))
			{
				
				
				
				createGridContainer(topcomp);
				
				
				
				
			}
			
			else if (toggle.equalsIgnoreCase("0"))
			{
				
				((XspDiv)topcomp).setDojoType("");
				
			}
			
			
			int count = sorted.length;
			
			
			
			
			
			
			
			for (int i=0; i<count; ++i)
			{
				
				String id = sorted[i];
				
				UIPanelEx portlet= (UIPanelEx) JSFUtil.findComponent(topcomp,id);
				UIPanelEx portletmovable= (UIPanelEx) JSFUtil.findComponent(topcomp,id+"moveable");
				
				if (portletmovable==null)
				{
					UIPanelEx moveableportlet= new UIPanelEx();
					moveableportlet.setId(id+"moveable");
					moveableportlet.setDojoType("dojo.dnd.Moveable");
					moveableportlet.getChildren().add(portlet);
					topcomp.getChildren().add(0, moveableportlet);
					viewScope.put("moveableToggle", "1");
					
				}else if(portletmovable.getChildCount()==0)
				{
					portletmovable.getChildren().add(portlet);
					topcomp.getChildren().add(0, portletmovable);
					viewScope.put("moveableToggle", "1");
					
				}else{
					
					topcomp.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					
				}
			}
		}
	 
	 
	 
	
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 @SuppressWarnings("unchecked")
	public static void setCompDojoType(UIComponent topcomp, String[] sorted){
			
			
			FacesContext context = FacesContext.getCurrentInstance();
			Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			
			int count = sorted.length;
			
			for (int i=0; i<count; ++i)
			{
				
				String id = sorted[i];
				
				UIPanelEx portlet= (UIPanelEx) JSFUtil.findComponent(topcomp,id);
				UIPanelEx portletmovable= (UIPanelEx) JSFUtil.findComponent(topcomp,id+"moveable");
				
				if (portletmovable==null)
				{
					UIPanelEx moveableportlet= new UIPanelEx();
					moveableportlet.setId(id+"moveable");
					moveableportlet.setDojoType("dojo.dnd.Moveable");
					moveableportlet.getChildren().add(portlet);
					topcomp.getChildren().add(0, moveableportlet);
					viewScope.put("moveableToggle", "1");
					
				}else if(portletmovable.getChildCount()==0)
				{
					portletmovable.getChildren().add(portlet);
					topcomp.getChildren().add(0, portletmovable);
					viewScope.put("moveableToggle", "1");
					
				}else{
					topcomp.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					
				}
			}
		}
	 
	 
	 
	 @SuppressWarnings("unchecked")
	public static void setCompDojoType1(UIComponent topcomp, UIComponent topcomp1, String[] sorted)
	 {
			
			
			FacesContext context = FacesContext.getCurrentInstance();
			Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			
			int count = sorted.length;
			
			for (int i=0; i<count; ++i)
			{
				
				String id = sorted[i];
				
				
				UIPanelEx portletmovable= (UIPanelEx) JSFUtil.findComponent(topcomp,id+"moveable");
				
				if ((portletmovable != null) && (portletmovable.getChildCount()!= 0))
				{
					UIPanelEx portlet= (UIPanelEx) JSFUtil.findComponent(topcomp,id);
					topcomp1.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					
					continue;
				}
				
				
				UIPanelEx portlet1 = (UIPanelEx) JSFUtil.findComponent(topcomp1,id);
				
				if (portlet1 != null)
				{
					if ((portletmovable != null) && (portletmovable.getChildCount()== 0))
					{
						portletmovable.getChildren().add(portlet1);
						topcomp.getChildren().add(0, portletmovable);
						viewScope.put("moveableToggle", "1");
						
						
					}
					else
					{
						UIPanelEx moveableportlet= new UIPanelEx();
						moveableportlet.setId(id+"moveable");
						moveableportlet.setDojoType("dojo.dnd.Moveable");
						moveableportlet.getChildren().add(portlet1);
						topcomp.getChildren().add(0, moveableportlet);
						viewScope.put("moveableToggle", "1");
						
						
						
					}
					
				}
			}
	 }
	 
	 
	 
	 
	 @SuppressWarnings("unchecked")
	public static void reorderInsideGrid(UIComponent topcomp, String[] sorted)
	 {
		 int count = sorted.length;
		 Object[] portlets = topcomp.getChildren().toArray();
		 
		 for (int i=0; i< count ; ++i)
		 {
			 topcomp.getChildren().remove(0);
			 
		 }
		
	
		 
		 String id ; 
		 String id1 ;
		 for (int j=0; j< count; ++j)
		 {
			 id = sorted[j];
			 
			 
			 for (int k=0; k< count; ++k)
			 {
				 id1 = ((UIPanelEx)portlets[k]).getId();
				 if (id.equals(id1))
				 {
					 topcomp.getChildren().add(((UIPanelEx)portlets[k]));
					 break;
					 
				 }
				 
				 
			 }
		 }
		 
		 
		
	 }
	 
	 
	 
	 
	 public static void createGridContainer(UIComponent topcomp)

	 {
	 				
	 				
	 				((XspDiv)topcomp).setDojoType("dojox.layout.GridContainer");
	 				
	 				DojoAttribute dojoAttribute1 = new DojoAttribute();
	 				dojoAttribute1.setComponent(topcomp);
	 				dojoAttribute1.setName("allowAutoScroll");
	 				dojoAttribute1.setValue("false");
	 				
	 				
	 				DojoAttribute dojoAttribute2 = new DojoAttribute();
	 				dojoAttribute2.setComponent(topcomp);
	 				dojoAttribute2.setName("nbZones");
	 				dojoAttribute2.setValue("1");
	 				
	 				
	 				DojoAttribute dojoAttribute3 = new DojoAttribute();
	 				dojoAttribute3.setComponent(topcomp);
	 				dojoAttribute3.setName("hasResizableColumns");
	 				dojoAttribute3.setValue("false");
	 				
	 				
	 				DojoAttribute dojoAttribute4 = new DojoAttribute();
	 				dojoAttribute4.setComponent(topcomp);
	 				dojoAttribute4.setName("acceptTypes");
	 				dojoAttribute4.setValue("dojox.widget.Portlet");
	 				
	 				
	 				DojoAttribute dojoAttribute5 = new DojoAttribute();
	 				dojoAttribute5.setComponent(topcomp);
	 				dojoAttribute5.setName("withHandles");
	 				dojoAttribute5.setValue("true");
	 				
	 				DojoAttribute dojoAttribute6 = new DojoAttribute();
	 				dojoAttribute6.setComponent(topcomp);
	 				dojoAttribute6.setName("handleClasses");
	 				dojoAttribute6.setValue("portletHeader");
	 				
	 				
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute1);
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute2);
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute3);
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute4);
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute5);
	 				((XspDiv)topcomp).addDojoAttribute(dojoAttribute6);
	 				
	 }
	 
	 
	 public static void reorderLinks(UIComponent list,UIOutlineBreadCrumbs breadcrumbs, String [] listarr)
	 {
		 
		 if (breadcrumbs == null)
		 {
			 
		 }
		 else
		 {
			 
		 }
		 
		 
		 int count = list.getChildCount();
		 
		 for (int i=0; i< count ; ++i)
		 {
			 list.getChildren().remove(0);
			 breadcrumbs.getTreeNodes().remove(0);
			 
			 
			 
		 }
		 
		 int len = listarr.length - 1;
		 
		 for (int i=0; i< len; ++i)
		 {
			 createLink(list, listarr[i], listarr[i]+"link");
			 
		 }
		 
		 
		 
		 
	 }
}
