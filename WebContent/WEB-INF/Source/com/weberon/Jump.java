package com.weberon;

import java.util.List;

import com.ibm.xsp.component.xp.XspDiv;
import com.ibm.xsp.component.xp.XspOutputLink;
import com.ibm.xsp.extlib.tree.complex.ComplexContainerTreeNode;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicNodeList;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.script.ScriptEngine;  
import javax.script.ScriptEngineFactory;  
import javax.script.ScriptEngineManager;  
import javax.script.ScriptException;  
import com.weberon.JSFUtil;
public class Jump extends BasicNodeList{
public Jump() {
	addTree();
		
}

private void addTree() {
	
	
	XspDiv linklist=(XspDiv) JSFUtil.findComponent("linklist");
	List list=linklist.getChildren();
	
	for (int i=1;i<=list.size();i++){
		
		XspOutputLink link =(XspOutputLink) list.get(i-1);
		String id=link.getText();	
		UIComponent comp1=JSFUtil.findComponent(id);
		String cid=comp1.getClientId(FacesContext.getCurrentInstance());
		//FacesContext context = FacesContext.getCurrentInstance();
		try {
			ComplexLeafTreeNode children;
			children = new ComplexLeafTreeNode();
			children.setLabel(id);
			//children.setOnClick("var com = document.getElementById('view:_id1:_id18:"+id+"' ); if (com != null){  dojox.fx.smoothScroll({ node:dojo.byId('view:_id1:_id18:"+id+"'), win:window, duration:400}).play();var toggle = document.getElementById('view:_id1:_id18:Toggle');if (toggle.getAttribute('value') == '1'){ var portletstyle=com.parentNode.style.cssText; com.parentNode.style.cssText=portletstyle+ 'z-index:'+zIndex; XSP.executeOnServer('view:_id1:eventHandler4','', '', ++zIndex);}}" );
			children.setOnClick("jumplink('"+cid+"');");
			addChild(children);	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
return;
}
}
