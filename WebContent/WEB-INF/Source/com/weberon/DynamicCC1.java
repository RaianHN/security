package com.weberon;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.UIPassThroughTag;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.component.outline.UIOutlineBreadCrumbs;
import com.ibm.xsp.component.xp.XspDiv;
import com.ibm.xsp.component.xp.XspOutputLink;
import com.ibm.xsp.dojo.DojoAttribute;
import com.ibm.xsp.extlib.tree.ITreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;

public class DynamicCC1 {
	
	public static void createPortlet(FacesContext s, UIComponent component, String c1, String c2, String id, String title)
	{
		try{
			
		
		UIPanelEx panel = new UIPanelEx();
		panel.setDojoType("dojox.widget.Portlet");
		panel.setTitle(title);
		panel.setStyle("height: auto ; width: auto");
		panel.setId(id);
		
		//XspDiv div = new XspDiv();
		//div.setId(id+"handle");
		//div.setStyle("height: 20px; background-color: transparent");
		
		//panel.getChildren().add(div);
		System.out.println("inside create portlet");
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
		System.out.println("inside create portlet1");
		if (toggle.equals("0")){
		
			component.getChildren().add(0, panel);	
				
			System.out.println("inside create portlet2");
		}else if(toggle.equals("1")){
			System.out.println("inside create portlet3");
			UIPanelEx panelmoveable = new UIPanelEx();
			panelmoveable.setId(id+"moveable");
			/*panelmoveable.setDojoType("dojo.dnd.Moveable");
			DojoAttribute dojoAttributes = new DojoAttribute();
			dojoAttributes.setComponent(panelmoveable);
			dojoAttributes.setName("skip");
			dojoAttributes.setValue("true");
			
			
			panelmoveable.addDojoAttribute(dojoAttributes);
			
			DojoAttribute dojoAttributes1 = new DojoAttribute();
			dojoAttributes1.setComponent(panelmoveable);
			dojoAttributes1.setName("handle");
			dojoAttributes1.setValue("view:_id1:_id18:"+id+"handle");
			panelmoveable.addDojoAttribute(dojoAttributes1);
			*/
			component.getChildren().add(0,panelmoveable);
			panelmoveable.getChildren().add(0, panel);
		
			String position=(String) viewScope.get("position");
			String zIndex=(String) viewScope.get("zIndex");
			System.out.println("inside create portlet31");
			panelmoveable.setStyle("position: absolute;  left:"+position+"px; top:"+position+"px ;z-index:"+zIndex+";");
			System.out.println("inside create portlet32");
			int val= Integer.parseInt(position);
			val=val+20;
			if (val>110){
				val=10;
			}
			System.out.println("inside create portlet33");
			viewScope.put("position", Integer.toString(val));
			viewScope.put("zIndex",Integer.toString((Integer.parseInt(zIndex)+1)));	
			System.out.println("inside create portlet4");
		}
		ControlBuilder.buildControl(s,con,true);
		System.out.println("no error while loading portlet");
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println("exception while loading portlet"+ e.toString());

		}
	}
	
	
	
	public static void removePortlet(UIComponent container, UIComponent portlet)
	{
		 container.getChildren().remove(portlet);
	}
	
	
	public static void createLink(UIComponent component, String comID, String linkID)
	{
		//Addlink to breadcrumb
		XspOutputLink link = new XspOutputLink();
		link.setText(comID);
		link.setId(linkID);
		UIComponent comp1=JSFUtil.findComponent(comID);
		String cid=comp1.getClientId(FacesContext.getCurrentInstance());
		
		String onClick="jumplink('"+cid+"');";
		//String onClick= "console.log('onclickevent'); var com = document.getElementById('view:_id1:_id18:"+comID+"' );console.log(com); if (com != null){  dojox.fx.smoothScroll({ node:dojo.byId('view:_id1:_id18:"+comID+"'), win:window, duration:400}).play(); var toggle = document.getElementById('view:_id1:_id18:Toggle');if (toggle.getAttribute('value') == '1'){var portletstyle=com.parentNode.style.cssText; com.parentNode.style.cssText=portletstyle+ 'z-index:'+zIndex; XSP.executeOnServer('view:_id1:eventHandler4','', '', ++zIndex);}}";
		//String onClick= "console.log('onclickevent'); var com = XSP.getElementById('#{id:"+comID+"}' ); if (com != null){ console.log(\"hi\"); dojox.fx.smoothScroll({ node:dojo.byId('#{id:"+comID+"}'), win:window, duration:400}).play(); var portletstyle=com.parentNode.style.cssText; com.parentNode.style.cssText=portletstyle+ 'z-index:'+zIndex++;}";
		link.setOnclick(onClick);
		//link.setOnclick("var com = document.getElementById(\"{id:comID}\" );\n\nif (com != null)\n{\n\ndojox.fx.smoothScroll({ node:dojo.byId(\"{id:comID}\" ), win:window, duration:400}).play();}" );
		//link.setStyle("padding-left:5px;padding-right:5px;");
		component.getChildren().add(link);
		addCrumb(onClick,comID);
		//UIPassThroughTag sep = new UIPassThroughTag();
       // sep.setValue("0");
       // component.getChildren().add(sep);
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
			//System.out.println("id:"+((UIPanelEx) com).getId()+"-"+ "styleonserver:"+((UIPanelEx) com).getStyle());
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
		System.out.println("LINKID:"+link.getId());
			removecrumb(link.getId());
			 linkcontainer.getChildren().remove(link);
	}
	public static void removecrumb(String label)
	{
		try {
			UIOutlineBreadCrumbs bc=(UIOutlineBreadCrumbs) JSFUtil.findComponent("breadCrumbs1");
			System.out.println("Startitr");

			List<ITreeNode> list=bc.getTreeNodes()	;
			ListIterator itr = list.listIterator(); 
			System.out.println("Stcnt:"+list.size());
			while(itr.hasNext()) {

				ComplexLeafTreeNode element = (ComplexLeafTreeNode) itr.next(); 
				System.out.println("Label:"+element.getLabel()+"link"+"::"+label);
				String glb= element.getLabel()+"link";
			    if(glb.equals(label)){
			    	//System.out.println("Label:"+element.getLabel()+"link"+"::"+label);
			    	itr.remove();
			    	return;
			    }
			    

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
			
	}
	public static void removeothercrumb(UIComponent link){
		String label = link.getId();
		System.out.println("Startitr");
		UIOutlineBreadCrumbs bc=(UIOutlineBreadCrumbs) JSFUtil.findComponent("breadCrumbs1");
		List<ITreeNode> list=bc.getTreeNodes()	;
		ListIterator itr = list.listIterator(); 
		System.out.println("Stcnt:"+list.size());
		while(itr.hasNext()) {

			ComplexLeafTreeNode element = (ComplexLeafTreeNode) itr.next(); 
			
			String glb= element.getLabel()+"link";
		    if(!(glb.equals(label))){
		    	System.out.println("Label:"+element.getLabel()+"link"+"::"+label);
		    	itr.remove();
		    	
		    }
		}
	}
	
	public static void setCompDojoType(UIComponent linkcontainer,UIComponent topcomp){
		System.out.println("toggling Move ");
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
				System.out.println("inside java function1 "+viewScope.get("moveableToggle"));
			}else if(portletmovable.getChildCount()==0)
			{
				portletmovable.getChildren().add(portlet);
				topcomp.getChildren().add(0, portletmovable);
				viewScope.put("moveableToggle", "1");
				System.out.println("inside java function2 "+viewScope.get("moveableToggle"));
			}else{
				topcomp.getChildren().add(portlet);
				JSFUtil.removeComponent(id+"moveable");
				viewScope.put("moveableToggle", "0");
				System.out.println("inside java function3 "+viewScope.get("moveableToggle"));
			}
		}
	}
	
	
	public static void createPreview(UIComponent r, FacesContext s, String cc)
	{
		int childcount=r.getChildCount();
		if(childcount>1 || childcount==1){
			for(int i=(childcount-1);i>=0;i--){
				r.getChildren().remove(i);
			}
		}	
		System.out.println("inside the create preview");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret ;
	}
	
	
	
	 public static SortData[] sort(SortData[]input)
		
		{
			Arrays.sort(input, new Compare());
			
			return input;
		}
 
	 
	 
	 
	 public static void setCompDojoType2(UIComponent topcomp, String[] sorted){
			System.out.println("toggling Move ");
			
			FacesContext context = FacesContext.getCurrentInstance();
			Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			
			String toggle = (String) viewScope.get("moveableToggle");
			if ( toggle.equalsIgnoreCase("1"))
			{
				System.out.println("setcomp 1");
				
				
				createGridContainer(topcomp);
				/*
				
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
				
				
				*/	
					
				
				
				
			}
			
			else if (toggle.equalsIgnoreCase("0"))
			{
				System.out.println("setcomp 0");
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
					System.out.println("inside java function1 "+viewScope.get("moveableToggle"));
				}else if(portletmovable.getChildCount()==0)
				{
					portletmovable.getChildren().add(portlet);
					topcomp.getChildren().add(0, portletmovable);
					viewScope.put("moveableToggle", "1");
					System.out.println("inside java function2 "+viewScope.get("moveableToggle"));
				}else{
					System.out.println("dojo type:"+((XspDiv)topcomp).getDojoType());
					topcomp.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					System.out.println("inside java function3 "+viewScope.get("moveableToggle"));
				}
			}
		}
	 
	 
	 
	
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 public static void setCompDojoType(UIComponent topcomp, String[] sorted){
			System.out.println("toggling Move ");
			
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
					System.out.println("inside java function1 "+viewScope.get("moveableToggle"));
				}else if(portletmovable.getChildCount()==0)
				{
					portletmovable.getChildren().add(portlet);
					topcomp.getChildren().add(0, portletmovable);
					viewScope.put("moveableToggle", "1");
					System.out.println("inside java function2 "+viewScope.get("moveableToggle"));
				}else{
					topcomp.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					System.out.println("inside java function3 "+viewScope.get("moveableToggle"));
				}
			}
		}
	 
	 
	 
	 public static void setCompDojoType1(UIComponent topcomp, UIComponent topcomp1, String[] sorted)
	 {
			System.out.println("toggling Move ");
			
			FacesContext context = FacesContext.getCurrentInstance();
			Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			
			String toggle = (String) viewScope.get("moveableToggle");
			
			
			int count = sorted.length;
			
			for (int i=0; i<count; ++i)
			{
				
				String id = sorted[i];
				
				
				UIPanelEx portletmovable= (UIPanelEx) JSFUtil.findComponent(topcomp,id+"moveable");
				
				if ((portletmovable != null) && (portletmovable.getChildCount()!= 0))
				{
					System.out.print("inside if block");
					UIPanelEx portlet= (UIPanelEx) JSFUtil.findComponent(topcomp,id);
					topcomp1.getChildren().add(0,portlet);
					JSFUtil.removeComponent(id+"moveable");
					viewScope.put("moveableToggle", "0");
					System.out.println("first");
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
						System.out.println("second1");
						
					}
					else
					{
						UIPanelEx moveableportlet= new UIPanelEx();
						moveableportlet.setId(id+"moveable");
						moveableportlet.setDojoType("dojo.dnd.Moveable");
						moveableportlet.getChildren().add(portlet1);
						topcomp.getChildren().add(0, moveableportlet);
						viewScope.put("moveableToggle", "1");
						System.out.println("second2");
						
						
					}
					
				}
			}
	 }
	 
	 
	 
	 
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
		 System.out.println("inside reorderlinks");
		 if (breadcrumbs == null)
		 {
			 System.out.println("breadcrumbs is null");
		 }
		 else
		 {
			 System.out.println("breadcrumbs is not null");
			 int ct = breadcrumbs.getChildCount();
			 System.out.println("breadcrumbs child count:"+ct);
			 int ct1 = breadcrumbs.getTreeNodes().size();
			 System.out.println("breadcrumbs treenodes size:"+ct1);
		 }
		 
		 
		 int count = list.getChildCount();
		 System.out.println("inside reorderlinks listcount:"+ count);
		 for (int i=0; i< count ; ++i)
		 {
			 list.getChildren().remove(0);
			 breadcrumbs.getTreeNodes().remove(0);
			 
			 
			 
		 }
		 System.out.println("inside reorderlinks after deleting previous links");
		 int len = listarr.length - 1;
		 System.out.println("inside reorderlinks listarr length:"+len);
		 for (int i=0; i< len; ++i)
		 {
			 createLink(list, listarr[i], listarr[i]+"link");
			 
		 }
		 
		 
		 
		 
	 }
}
