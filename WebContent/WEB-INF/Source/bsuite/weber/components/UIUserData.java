package bsuite.weber.components;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;


import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.JSFUtil;

public class UIUserData {
	
	private static String m_documentId;

	

	private Session session;

	
	//m_FIELDS=new ArrayList<DataChangeLogEntry>();
	
	private static String getDocumentId() {
		if (m_documentId==null) {
			//read query parameter 'documentId'
			Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
			System.out.println("sessionscopevalue: "+sessionscope.toString());
			Object temp= sessionscope.get("userdocumentId");
			String c2=temp.getClass().getName();
			System.out.print(c2);
			String[] documentIdArr = new String[100];
			if(c2=="java.lang.String"){
				documentIdArr[0]=(String) temp;
				}else{
					documentIdArr= (String[]) temp;
				}
			
			if (documentIdArr==null || documentIdArr.length==0) {
				//we create a new document
				m_documentId="";
			}
			else {
				m_documentId=documentIdArr[0];
				System.out.println("documentId="+m_documentId);
			}
		}
		return m_documentId;
	}
	/**
	 * The method loads the instance of the database document wrapped by
	 * this class
	 * 
	 * @return document or <code>null</code> if the document has not been saved before
	 * @throws NotesException
	 */
	@SuppressWarnings("unchecked")
	public static void getfields() throws NotesException {
		String documentId=getDocumentId();
		if ("".equals(documentId))
			return;
		
		Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");		
		
		
		Document doc;
		
			//we need to reload the document
			//Session session=NotesContext.getCurrent().getCurrentSession();
		String bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			Database userdb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"user.nsf");
			doc=userdb.getDocumentByUNID(documentId);
			Vector fields = doc.getItems();
			String[] a= new String[fields.size()]; 
			 
			 
	          int i =0;
			  for(Object str : fields){
				  a[i++] = str.toString();
			  }
			//String[] a=(String[])fields.toArray(new String[fields.size()]);
			//cache it in the viewscope until it gets invalid
			viewScope.put("curfields",a );
			System.out.println("viewscopevalue: "+viewScope.toString());
		
	}
}
