package bsuite.weber.backend.nsf;

import java.io.Serializable;
//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;


import javax.annotation.PreDestroy;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;


import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;


//import com.ls11.uibackend.DataChangeLogEntry;

import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.JSFUtil;

import bsuite.weber.backend.DataObjectExt;

public class ActivityPageDataProvider implements DataObjectExt{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4802588064806836751L;

	private static final String VIEWSCOPE_DATADOCUMENT="bsuite.weber.backend.nsf.ActivityPageDataProvider.actdocument";
	
	private Map<String, String> m_cachedValues;
	private Map<String, String> m_changedValues;
	//private List<DataChangeLogEntry> m_pendingLogEntries;
	private String m_documentId;
	
	private String bsuitepath;
	//private Database actdb;
	public ActivityPageDataProvider() throws NotesException {
		m_cachedValues=new HashMap<String,String>();
		m_changedValues=new HashMap<String,String>();
		Database currentdb= ExtLibUtil.getCurrentDatabase();
		// bsuitepath=BSUtil.getBsuitePath(currentdb);
		//session =  ExtLibUtil.getCurrentSession();
		//Database actdb= session.getDatabase("", bsuitepath+"activity.nsf");
		//m_pendingLogEntries=new ArrayList<DataChangeLogEntry>();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	private String getDocumentId() {
		if (m_documentId==null) {
			//read query parameter 'documentId'
			Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
			System.out.println("sessionscopevalue: "+sessionscope.toString());
			Object temp= sessionscope.get("actdocumentId");
			String c2=temp.getClass().getName();
			//System.out.print(c2);
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
	
	private boolean isNewNote() {
		try {
			return "".equals(getDocumentId()) || getDocument()==null;
		} catch (NotesException e) {
			throw new FacesException("isNewNote check failed", e);
		}
	}
	
	/**
	 * The method loads the instance of the database document wrapped by
	 * this class
	 * 
	 * @return document or <code>null</code> if the document has not been saved before
	 * @throws NotesException
	 */
	@SuppressWarnings("unchecked")
	private Document getDocument() throws NotesException {
		String documentId=getDocumentId();
		if ("".equals(documentId))
			return null;
		
		Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");
		
		DocumentRef docRef=(DocumentRef) viewScope.get(VIEWSCOPE_DATADOCUMENT);
		Document doc=docRef==null ? null : docRef.getDocument();
		
		if (doc==null || isRecycled(doc)) {
			//we need to reload the document
			//Session session=NotesContext.getCurrent().getCurrentSession();
			//Database userdb=session.getCurrentDatabase();
			bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			Database actdb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"activity.nsf");
			doc=actdb.getDocumentByUNID(documentId);
			//cache it in the viewscope until it gets invalid
			viewScope.put(VIEWSCOPE_DATADOCUMENT, new DocumentRef(doc));
		}
		return doc;
	}
	
	private boolean isRecycled(Document doc) {
		try {
			return !doc.isValid();
		} catch (NotesException e) {
			return true;
		}
	}
	
	public void store() {
		if (!m_changedValues.isEmpty()) {
			try {
				Document doc=getDocument();
				if (doc==null) {
					//we need to create a new document in the database
			
					bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
					Database actdb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"activity.nsf");
					doc=actdb.createDocument();
					
					doc.replaceItemValue("Form", "activity");
				}
				
				//write all changed values into the document
				for (String currId : m_changedValues.keySet()) {
					String newVal=m_changedValues.get(currId);
					if (newVal==null) {
						doc.removeItem(currId);
					}
					else {
						doc.replaceItemValue(currId, newVal);
					}
				}
				doc.replaceItemValue("display_field", doc.getItemValueString("subject"));
				doc.save(true, false);
				m_documentId=doc.getUniversalID();
			} catch (NotesException e) {
				throw new FacesException("Could not store data to Notes document", e);
			}
			
			//flushChangeLog();
			
			//clear the list of changed values and force reloading all values
			//from the document
			m_changedValues.clear();
			m_cachedValues.clear();
		}
		
	}

	public Class<?> getType(Object id) {
		
		return String.class;
	}

	public Object getValue(Object id) {
		String sId=id.toString();
		if (m_changedValues.containsKey(sId)) {
			return m_changedValues.get(sId);
		}
		else if (m_cachedValues.containsKey(sId)) {
			return m_cachedValues.get(sId);
		}
		else {
			if (isNewNote()) {
				return null;
			}
			else {
				try {
					Document doc=getDocument();
					String itemValue=doc.getItemValueString(sId);
					m_cachedValues.put(sId, itemValue);
					return itemValue;
				} catch (NotesException e) {
					throw new FacesException("Could not access document value for documentId "+getDocumentId(), e);
				}
			}
		}
	}

	public boolean isReadOnly(Object arg0) {
		
		return false;
	}

	public void setValue(Object id, Object newValue) {
		Object oldValue=getValue(id);
		
		boolean changed=(newValue==null & oldValue!=null) || (newValue!=null && !newValue.equals(oldValue));
		if (changed) {
			m_changedValues.put(id.toString(), newValue==null ? null : newValue.toString());
			//logChangedData(id.toString(), oldValue, newValue);
		}
	}

	
	/**
	 * Reference class to cache the Document instance between multiple calls to
	 * {@link NSFPageDataProvider#getValue(Object)}
	 * 
	 * @author Karsten Lehmann
	 */
	private static class DocumentRef implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3217023499097036493L;
		/** document is transient to avoid serialization attempts of Domino
		 * between multiple XPage requests */
		private transient Document m_doc;
		
		public DocumentRef(Document doc) {
			m_doc=doc;
		}
		
		public Document getDocument() {
			return m_doc;
		}
	}

	
	@SuppressWarnings("unchecked")
	public void reset(){
		System.out.println(" reset called");
		m_cachedValues.clear();
		m_changedValues.clear();
		Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");
		viewScope.put(VIEWSCOPE_DATADOCUMENT, null);
		m_documentId=null;
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}
	
}
