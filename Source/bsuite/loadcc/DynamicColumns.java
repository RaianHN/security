package bsuite.loadcc;

import com.ibm.xsp.component.UIPanelEx;
import com.ibm.xsp.component.xp.XspColumn;
import com.ibm.xsp.component.xp.XspDataTableEx;

public class DynamicColumns {

	@SuppressWarnings("unused")
	private XspDataTableEx dataTb ;
	@SuppressWarnings("unused")
	private UIPanelEx viewPanel;
	@SuppressWarnings("unused")
	private static int linkc=0;
	@SuppressWarnings("unused")
	private static int colc=0;
	public String tableid="tabledemo";
	
	
	public XspColumn getDyColumns(){
		return null;
		
	}
}
