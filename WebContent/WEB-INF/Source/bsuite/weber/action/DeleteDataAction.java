package bsuite.weber.action;

import bsuite.weber.data.DataObjectExt;
import bsuite.weber.data.PageDataBean;
import bsuite.weber.tools.JSFUtil;

public class DeleteDataAction {
	
	public Object execute(String arg) {
		if(arg==null){
			return null;
		}
		//grab the data bean instance or let JSF create it:
		PageDataBean dataBean=(PageDataBean) JSFUtil.getBindingValue("#{"+arg+"}");
		DataObjectExt dataObject=dataBean.getDataProvider();
		if (dataObject!=null) {
			//please note that we don't need to know the actual implementation class
			//for the data object
			dataObject.delete();
		}
		return null;
	}


}
