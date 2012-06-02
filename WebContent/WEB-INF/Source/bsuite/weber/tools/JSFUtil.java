
package bsuite.weber.tools;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewEntryCollection;



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
        
        public static Object getApplciationComponent(String scopeid){
	
			Map applicationscope = (Map) JSFUtil.getVariableValue("applicationScope"); 
			return applicationscope.get(scopeid);
        }
        public static Object getSessionComponent(String scopeid){
        	Map sessionscope = (Map) JSFUtil.getVariableValue("sessionScope"); 
        	return sessionscope.get(scopeid);
        }
        public static Object getViewComponent(String scopeid){
        	Map viewscope = (Map) JSFUtil.getVariableValue("viewScope"); 	
        	return viewscope.get(scopeid);
        }
        public static Object getRequestComponent(String scopeid){
        	Map requestscope = (Map) JSFUtil.getVariableValue("requestScope");	
        	return requestscope.get(scopeid);
        }
        @SuppressWarnings("unchecked")
		public static String DBLookupString(String Dbname, String vwname,Vector key, int i){
        	System.out.println("test4");
        	if (vwname.trim() == "") {return null;}
        	if (i < 0){ return null;}//Then Exit Function
        	//if (key.length==0){return null;}// Then Exit Function
        	//Set vwEmp = DB.GetView(strView)
        	String nlist=null;// = new Vector();
        	Vector column=getColumnValue( Dbname,  vwname, key,  i);
        	if(column.size()!=0){
        	nlist=   (String) column.elementAt(i);
        	}
			return nlist;
        }
        public static Vector DBLookupVector(String Dbname, String vwname,Vector key, int i){
        	System.out.println("test5");
        	Vector nlist = new Vector();;
        	Vector column = new Vector();
			try {
				
				
				if (vwname.trim() == "") {return null;}
				if (i < 0){ return null;}
				 column=getallColumnValue( Dbname,  vwname, key,  i);
				if(column.size()!=0){
					System.out.println(Integer.toString(column.size()));
					
					//Object item= column.elementAt(i);
					//String c2=item.getClass().getName();
					//System.out.println(c2+":"+item);
					
					return column;
					}else{System.out.println("did not fimd reportees");}
				return nlist;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        	return nlist;
        }
        
        private static Vector getColumnValue(String Dbname, String vwname,Vector key, int i){
        	Database db;
        	
        	Vector columns= new Vector();
        
			try {
				db = ExtLibUtil.getCurrentSession().getDatabase("", BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase())+"relation.nsf");  
				if(db==null){
					System.out.println("db is null");
				}
				View view1 = db.getView(vwname);
				
				if(view1==null){
					System.out.println("view is null");
				}
				
				ViewEntry ve =  view1.getEntryByKey(key);
				
				if(ve==null){
					System.out.println("test6ve null");
					
					return columns;}
				 columns = ve.getColumnValues();
				 
				 
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        	return columns;
        }
        private static Vector getallColumnValue(String Dbname, String vwname,Vector key, int i){
        	Database db;
        	
        	Vector<String> columns= new Vector();
        	String[] str1 = null;
			try {
				db = ExtLibUtil.getCurrentSession().getDatabase("", BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase())+"relation.nsf");  
				if(db==null){
					System.out.println("db is null");
				}
				View view1 = db.getView(vwname);
				
				if(view1==null){
					System.out.println("view is null");
				}
				
				
				ViewEntryCollection vec =  view1.getAllEntriesByKey(key);
				ViewEntry ve ;
				if(vec==null){
					System.out.println("test6ve null");
					
					return columns;}
				ve =vec.getFirstEntry();
				for(int j=0;j<vec.getCount();j++){
				
				 columns.add((String) ve.getColumnValues().elementAt(i));
				ve = vec.getNextEntry(ve);
				}
				return columns;
				 
				 
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
        	return columns;
        }
        
} 
