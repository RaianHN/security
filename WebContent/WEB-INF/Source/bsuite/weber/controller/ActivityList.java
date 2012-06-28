package bsuite.weber.controller;
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import bsuite.weber.model.Activity;
//import bsuite.weber.model.Person;
public class ActivityList {
	private List<Activity> activities ;
	private Session session;
	private FacesContext context;
	@SuppressWarnings({ "unused", "unchecked" })
	private Map viewScope;
	private Database currentdb;
	private Object bsuitepath;
	//private Person currentuser;
	private Database actdb;
	public Document doc;
	public ActivityList() {
		
	}

	
	
	  private void init() { 
		
			initworkflow();
		  activities=  new ArrayList<Activity>();
		  try { 
		  View actview = actdb.getView("alldocuments");
		 doc = actview.getFirstDocument();
		   while (!(doc == null) ){
			  
				activities.add(new Activity(doc));
				doc = actview.getNextDocument(doc);
			   
		   }
		  } catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	  }
	  public List<Activity> getActivities() { 
          //init() initializes the actor list the first time it is called 
          init(); 

          return activities; 
  } 
	  
	  
  public void setActivities(List<Activity> newActivities) { 
	  this.activities=newActivities; 
  } 
  
  @SuppressWarnings("unchecked")
public void initworkflow() {
	  session = ExtLibUtil.getCurrentSession();
		 context = FacesContext.getCurrentInstance();
		viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		currentdb= ExtLibUtil.getCurrentDatabase();
		
		 try {
			 bsuitepath=getBsuitePath(currentdb);
			 actdb = session.getDatabase("", bsuitepath+"Activity.nsf");
			//this.currentuser = new Person();
			
			
			
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
	}
  public String getBsuitePath(Database tadb) throws NotesException{
		String bsuitePath=null;
		try {
			int len=(tadb.getFilePath()).length() - (tadb.getFileName()).length();
			bsuitePath=tadb.getFilePath().substring(0,len);
			return bsuitePath;
		} catch (NotesException e) {
			System.out.println(e.id + " " + e.text);
		}catch (Exception e){
			e.printStackTrace();
		}
		return bsuitePath;
	}
}

