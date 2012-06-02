package bsuite.weber.backend;
import com.ibm.xsp.model.DataObject;
import bsuite.weber.backend.actions.SaveDataAction;

import bsuite.weber.backend.IPageAction;
public class UserActionsBean implements  DataObject{

	public Class<?> getType(Object arg0) {
		
		return IPageAction.class;
	}

	public Object getValue(Object id) {
		if ("save".equals(id)) {
			return new SaveDataAction();
		}
		return null;
	}

	public boolean isReadOnly(Object arg0) {
		
		return true;
	}

	public void setValue(Object id, Object newValue) {
		
		
	}

}
