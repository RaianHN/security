package bsuite.weber.tools;

import java.util.Map;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

import com.ibm.xsp.extlib.util.ExtLibUtil;

public class BsuiteMain {

	protected Session session;
	protected Database currentdb;
	protected String bsuitepath; 
	protected FacesContext context;
	protected Map viewScope;
	protected String currentuser; 
	
	
	public BsuiteMain() {
		// TODO Auto-generated constructor stub
	
		try{
			session = ExtLibUtil.getCurrentSession();
			context = FacesContext.getCurrentInstance();
			viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			currentdb= ExtLibUtil.getCurrentDatabase();
			 bsuitepath=getBsuitePath(currentdb);
			 currentuser=session.getEffectiveUserName();
			 
		}catch (Exception e) {
			// TODO: handle exception
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
