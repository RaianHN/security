
package com.bsuite.utility;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import java.util.List;
import java.util.Map;




public class JSFUtil {
       /**
         * The method creates a {@link javax.faces.el.ValueBinding} from the
         * specified value binding expression and returns its current value.<br>
         * <br>
         * If the expression references a managed bean or its properties and the bean has not
         * been created yet, it gets created by the JSF runtime.
         *
         * @param ref value binding expression, e.g. #{Bean1.property}
         * @return value of ValueBinding
         * throws javax.faces.el.ReferenceSyntaxException if the specified <code>ref</code> has invalid syntax
         */
        public static Object getBindingValue(String ref) {
                FacesContext context=FacesContext.getCurrentInstance();
                Application application=context.getApplication();
                return application.createValueBinding(ref).getValue(context);
        }

       /**
         * The method creates a {@link javax.faces.el.ValueBinding} from the
         * specified value binding expression and sets a new value for it.<br>
         * <br>
         * If the expression references a managed bean and the bean has not
         * been created yet, it gets created by the JSF runtime.
         *
         * @param ref value binding expression, e.g. #{Bean1.property}
         * @param newObject new value for the ValueBinding
         * throws javax.faces.el.ReferenceSyntaxException if the specified <code>ref</code> has invalid syntax
         */
        public static void setBindingValue(String ref, Object newObject) {
                FacesContext context=FacesContext.getCurrentInstance();
                Application application=context.getApplication();
                ValueBinding binding=application.createValueBinding(ref);
                binding.setValue(context, newObject);
        }

       /**
         * The method returns the value of a global JavaScript variable.
         *
         * @param varName variable name
         * @return value
         * @throws javax.faces.el.EvaluationException if an exception is thrown while resolving the variable name
         */
        public static Object getVariableValue(String varName) {
                FacesContext context = FacesContext.getCurrentInstance();
                return context.getApplication().getVariableResolver().resolveVariable(context, varName);
        }

       /**
         * Finds an UIComponent by its component identifier in the current
         * component tree.
         *
         * @param compId the component identifier to search for
         * @return found UIComponent or null
         *
         * @throws NullPointerException if <code>compId</code> is null
         */
        public static UIComponent findComponent(String compId) {
                return findComponent(FacesContext.getCurrentInstance().getViewRoot(), compId);
        }
        public static void removeComponent(String componentId) {
            UIViewRoot root = FacesContext.getCurrentInstance()
                    .getViewRoot();
            UIComponent component = root.findComponent(componentId);

            if (component != null) {
                UIComponent parent = component.getParent();
                parent.getChildren().remove(component);
            }
        }

        
        
        
       /**
         * Finds an UIComponent by its component identifier in the component tree
         * below the specified <code>topComponent</code> top component.
         *
         * @param topComponent first component to be checked
         * @param compId the component identifier to search for
         * @return found UIComponent or null
         *
         * @throws NullPointerException if <code>compId</code> is null
         */
        @SuppressWarnings("unchecked")
		public static UIComponent findComponent(UIComponent topComponent, String compId) {
                if (compId==null)
                        throw new NullPointerException("Component identifier cannot be null");

                if (compId.equals(topComponent.getId()))
                        return topComponent;

                if (topComponent.getChildCount()>0) {
                      List<UIComponent> childComponents= topComponent.getChildren();

                        for (UIComponent currChildComponent : childComponents) {
                                UIComponent foundComponent=findComponent(currChildComponent, compId);
                                if (foundComponent!=null)
                                        return foundComponent;
                        }
                }
                return null;
        }
        
        @SuppressWarnings("unchecked")
		public static Object getApplciationComponent(String scopeid){
	
			Map applicationscope = (Map) JSFUtil.getVariableValue("applicationScope"); 
			return applicationscope.get(scopeid);
        }
        @SuppressWarnings("unchecked")
		public static Object getSessionComponent(String scopeid){
        	Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope"); 
        	return sessionscope.get(scopeid);
        }
        @SuppressWarnings("unchecked")
		public static Object getViewComponent(String scopeid){
        	Map viewscope = (Map) JSFUtil.getVariableValue("viewScope"); 	
        	return viewscope.get(scopeid);
        }
        @SuppressWarnings("unchecked")
		public static Object getRequestComponent(String scopeid){
        	Map requestscope = (Map) JSFUtil.getVariableValue("requestScope");	
        	return requestscope.get(scopeid);
        }
    
} 
