package bsuite.loadcc;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.ibm.xsp.component.xp.XspDiv;
import com.ibm.xsp.component.xp.XspOutputLink;
import com.ibm.xsp.extlib.tree.complex.ComplexLeafTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicNodeList;
@SuppressWarnings("serial")
public class Jump extends BasicNodeList{
public Jump() {
	addTree();
		
}

@SuppressWarnings("unchecked")
private void addTree() {
	
	
	XspDiv linklist=(XspDiv) JSFUtil.findComponent("linklist");
	List list=linklist.getChildren();
	
	for (int i=1;i<=list.size();i++){
		
		XspOutputLink link =(XspOutputLink) list.get(i-1);
		String id=link.getText();	
		UIComponent comp1=JSFUtil.findComponent(id);
		String cid=comp1.getClientId(FacesContext.getCurrentInstance());
		
		try {
			ComplexLeafTreeNode children;
			children = new ComplexLeafTreeNode();
			children.setLabel(id);
			
			children.setOnClick("jumplink('"+cid+"');");
			addChild(children);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
return;
}
}
