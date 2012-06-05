package bsuite.weber.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;


import bsuite.weber.tools.BSUtil;
import bsuite.weber.tools.JSFUtil;

import com.ibm.domino.xsp.module.nsf.NotesContext;
import com.ibm.xsp.extlib.util.ExtLibUtil;


public class NSFPageDataProvider implements DataObjectExt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1532054140313463648L;

	private static final String VIEWSCOPE_DATADOCUMENT="bsuite.weber.data.NSFPageDataProvider.document";
	
	private Map<String, String> m_cachedValues;
	private Map<String, String> m_changedValues;

	
	private String m_documentId;
	private String bsuitepath;
	//to get the viewScope variable
	Map viewScope=(Map) JSFUtil.getVariableValue("viewScope");
	
	public NSFPageDataProvider() {
		m_cachedValues=new HashMap<String,String>();
		m_changedValues=new HashMap<String,String>();
		
	}

	@SuppressWarnings("unchecked")
	private String getDocumentId() {
		if (m_documentId==null) {
			//read query parameter 'documentId'
			Map sessionscope=(Map) JSFUtil.getVariableValue("sessionScope");
			Object temp= sessionscope.get("documentId");
		//	String[] documentIdArr=(String[]) paramValues.get("documentId");
			
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
	private Document getDocument() throws NotesException {
		String documentId=getDocumentId();
		if ("".equals(documentId))
			return null;
		
		//System.out.println("getDocument reference");
		DocumentRef docRef=(DocumentRef) viewScope.get(VIEWSCOPE_DATADOCUMENT);
		Document doc=docRef==null ? null : docRef.getDocument();
		
		if (doc==null || isRecycled(doc)) {
			//we need to reload the document
			//Session session=NotesContext.getCurrent().getCurrentSession();
			System.out.println("getDocument reference is null");
			bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
			Database entitydb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"Employees.nsf");
			doc=entitydb.getDocumentByUNID(documentId);
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

	public boolean isReadOnly(Object id) {
		return false;
	}

	public void setValue(Object id, Object newValue) {
		Object oldValue=getValue(id);
		
		boolean changed=(newValue==null & oldValue!=null) || (newValue!=null && !newValue.equals(oldValue));
		if (changed) {
			m_changedValues.put(id.toString(), newValue==null ? null : newValue.toString());
			
		}
	}

	
	
	public void store() {
		
		if (!m_changedValues.isEmpty()) {
			try {
				Document doc=getDocument();
				if (doc==null) {
					//we need to create a new document in the database
					//we need to create a new document in the database
					//Session session=NotesContext.getCurrent().getCurrentSession();
					bsuitepath=BSUtil.getBsuitePath(ExtLibUtil.getCurrentDatabase());
					Database userdb=ExtLibUtil.getCurrentSession().getDatabase("", bsuitepath+"Employees.nsf");
					doc=userdb.createDocument();
					
				//	doc.replaceItemValue("Form", viewScope.get("entityName"));
					doc.replaceItemValue("obid", viewScope.get("entityName"));
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
				doc.save(true, false);
				m_documentId=doc.getUniversalID();
			} catch (NotesException e) {
				throw new FacesException("Could not store data to Notes document", e);
			}
			
		
			
			//clear the list of changed values and force reloading all values
			//from the document
			m_changedValues.clear();
			m_cachedValues.clear();
		}
		
	}

	/**
	 * Reference class to cache the Document instance between multiple calls to
	 * {@link NSFPageDataProvider#getValue(Object)}
	 * 
	 * @author Karsten Lehmann
	 */
	private static class DocumentRef implements Serializable {
		
		private static final long serialVersionUID = -1954569034088424056L;
		
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
	
	public void delete(){
		try{
			
			Document doc=getDocument();
			doc.remove(true);
			
		}catch (Exception e) {
			// TODO: handle exception
		}

		
		
		
		
	}
}
