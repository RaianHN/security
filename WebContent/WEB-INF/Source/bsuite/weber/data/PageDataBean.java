package bsuite.weber.data;

import java.util.Map;

import javax.faces.FacesException;

import bsuite.weber.tools.JSFUtil;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.model.DataObject;


public class PageDataBean implements DataObject {
	private final String VIEWSCOPE_DATAPROVIDERCLASS="bsuite.weber.data.PageDataBean.provider";
	
	private String m_dataProviderClassName;
	
	/**
	 * This method is called by JSF with the value of the managed
	 * property 'dataProviderClass'
	 * 
	 * @param className class
	 */
	public void setDataProviderClass(String className) {
		m_dataProviderClassName=className;
	}
	
	/**
	 * The method returns the instance of {@link DataObjectExt} that is
	 * used for the actual data storage
	 * 
	 * @return instance
	 */
	@SuppressWarnings("unchecked")
	public DataObjectExt getDataProvider() {
		Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");

		DataObjectExt provider=(DataObjectExt) viewScope.get(VIEWSCOPE_DATAPROVIDERCLASS);
		if (provider==null) {
			if (m_dataProviderClassName==null)
				throw new FacesException("No data provider specified. Use managed property 'dataProviderClass' for declaration");

			//create new instance with the specified class name and store it
			//in the viewSope
			ClassLoader cl=NotesContext.getCurrent().getModule().getModuleClassLoader();
			try {
				Class<DataObjectExt> providerClass=(Class<DataObjectExt>) cl.loadClass(m_dataProviderClassName);
				provider=providerClass.newInstance();

				viewScope.put(VIEWSCOPE_DATAPROVIDERCLASS, provider);
			} catch (ClassNotFoundException e) {
				throw new FacesException("Could not resolve data provider for the XPage", e);
			} catch (IllegalAccessException e) {
				throw new FacesException("Could not resolve data provider for the XPage", e);
			} catch (InstantiationException e) {
				throw new FacesException("Could not resolve data provider for the XPage", e);
			}
		}
		return provider;
	}
	
	public Class<?> getType(Object id) {
		return getDataProvider().getType(id);
	}

	public Object getValue(Object id) {
		return getDataProvider().getValue(id);
	}

	public boolean isReadOnly(Object id) {
		return getDataProvider().isReadOnly(id);
	}

	public void setValue(Object id, Object value) {
		getDataProvider().setValue(id, value);
	}
}
