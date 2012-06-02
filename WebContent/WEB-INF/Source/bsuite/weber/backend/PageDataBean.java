package bsuite.weber.backend;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.model.DataObject;


import bsuite.weber.tools.JSFUtil;

public class PageDataBean implements DataObject{
	
private  String VIEWSCOPE_DATAPROVIDERCLASS;
	
	private String m_dataProviderClassName;
	
	/**
	 * This method is called by JSF with the value of the managed
	 * property 'dataProviderClass'
	 * 
	 * @param className class
	 */
	public void setDataProviderClass(String className) {
		m_dataProviderClassName=className;
		System.out.println(className);
	}
	/**
	 * This method is called by JSF with the value of the managed
	 * property 'viewScopeVariable'
	 * 
	 * @param varName class
	 */
	public void setViewScopeVariable(String varName) {
		VIEWSCOPE_DATAPROVIDERCLASS=varName;
		System.out.println(varName);
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
			//ClassLoader cl=FacesContext.getCurrentInstance().getContextClassLoader();
			
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
	
	public void reset(){
		System.out.println("resetbean");
		getDataProvider().reset();
	}
}
