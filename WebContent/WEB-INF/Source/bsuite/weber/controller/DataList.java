package bsuite.weber.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import bsuite.weber.model.DataBmod;
import bsuite.weber.tools.BSUtil;

public class DataList {
	private List<DataBmod> datalist ;
	private Session session;
	private FacesContext context;
	@SuppressWarnings("unused")
	private Map viewscope;
	private Database currentdb;
	private Object bsuitepath;
	
	private Database actdb;
	private Database userdb;
	public Document doc;
	private Vector key;
	private View actview;
	private View userview;
	private Map requestscope;
	
	
	public DataList() {
	
	}
	 public List<DataBmod> getDatalist() { 
         //init() initializes the actor list the first time it is called
		 System.out.println("start");
		 if(datalist==null){
         init(); }
         System.out.println("intialised");
         return datalist; 
 } 
	  
	  
 public void setDataList(List<DataBmod> newDataList) { 
	  this.datalist=newDataList; 
 } 
 private void init() { 
	 System.out.println("inside init");
	 datalist=null;
		initworkflow();
	 
	 
	// System.out.println(key);
	  try { 
		   
		//  System.out.println(key);
		  doc = actview.getFirstDocument();
		  while (!(doc == null) ){

			  datalist.add(new DataBmod(doc));
			  doc = actview.getNextDocument(doc);

		  }
		  doc = userview.getFirstDocument();
		  while (!(doc == null) ){

			  datalist.add(new DataBmod(doc));
			  doc = userview.getNextDocument(doc);

		  }
	  
	  } catch (NotesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
}
 
 @SuppressWarnings("unchecked")
public void findbykey() {
	 
	// System.out.println("find by key");
	 datalist=null;
	 List<DataBmod> tmpdatalist =null;
	 initworkflow();
	
	try {
		 String tmp =  (String) viewscope.get("quicksearch");
		 
		 System.out.println(tmp);
		// key.add(tmp);
		 tmpdatalist= new ArrayList<DataBmod>();
		 DocumentCollection adc;
		 if (actdb.isFTIndexed()) {
		        actdb.updateFTIndex(false);
		 }
		adc = actdb.FTSearch(tmp);
		//actview.getAllDocumentsByKey(key);
		//actview.FTSearch(tmp);
		System.out.println("got adc");
		 if (userdb.isFTIndexed()) {
		        userdb.updateFTIndex(false);
		 }
		DocumentCollection udc =userdb.FTSearch(tmp);	//userview.getAllDocumentsByKey(key);
		  doc = adc.getFirstDocument();
		  while (!(doc == null) ){
			  
				tmpdatalist.add(new DataBmod(doc));
				doc = adc.getNextDocument(doc);
			   
		   }
		  doc = udc.getFirstDocument();
		  while (!(doc == null) ){
			  
				tmpdatalist.add(new DataBmod(doc));
				doc = udc.getNextDocument(doc);
			   
		   }
		 this.setDataList(tmpdatalist);
		  
	} catch (NotesException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		  
 }
 public void initworkflow() {
	
	  session = ExtLibUtil.getCurrentSession();
		 context = FacesContext.getCurrentInstance();
		viewscope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		currentdb= ExtLibUtil.getCurrentDatabase();
		 requestscope=(Map) context.getApplication().getVariableResolver().resolveVariable(context, "requestScope");
		 try {
			 bsuitepath=BSUtil.getBsuitePath(currentdb);
			 actdb = session.getDatabase("", bsuitepath+"activity.nsf");
			userdb = session.getDatabase("", bsuitepath+"user.nsf");
			 actview = actdb.getView("alldocuments");
			   userview = userdb.getView("AllDocuments");
			   datalist=  new ArrayList<DataBmod>();
			//this.currentuser = new Person();
			//   System.out.println("inside initworkflow done");
			
			
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
	}
 public void checklength(String s){
	 int t=3;
	 System.out.println("checklength");
	StringBuffer bf = new StringBuffer();
	s=(String)viewscope.get("quicksearch");
	bf.append(s);
	 
	 if(t == bf.length()){
		 System.out.println("set search");
		 requestscope.put("searchflag", "1");
		 
	 }else{
		 System.out.println("reset search"+s+ bf.length());
		 requestscope.put("searchflag", "0");
	 }
		 
 }
}
