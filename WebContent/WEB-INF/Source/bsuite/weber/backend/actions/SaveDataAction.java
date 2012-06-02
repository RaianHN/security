package bsuite.weber.backend.actions;

import bsuite.weber.backend.DataObjectExt;
import bsuite.weber.backend.PageDataBean;
import bsuite.weber.tools.JSFUtil;

import bsuite.weber.backend.actions.AbstractPageAction;

public class SaveDataAction extends AbstractPageAction {

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
			dataObject.store();
		}
		return null;
	}

}
