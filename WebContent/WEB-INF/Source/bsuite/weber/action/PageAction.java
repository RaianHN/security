package bsuite.weber.action;

import com.ibm.xsp.model.DataObject;

public class PageAction implements DataObject{
	
	public Class<?> getType(Object arg0) {
		return IPageAction.class;
	}

	public Object getValue(Object id) {
		if ("save".equals(id)) {
			return new SaveDataAction();
		}
		
		if("delete".equals(id)){
			return new DeleteDataAction();
		}
		return null;
	}

	public boolean isReadOnly(Object arg0) {
		return true;
	}

	public void setValue(Object id, Object newValue) {
	}
}
