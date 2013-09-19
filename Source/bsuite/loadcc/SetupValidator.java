package bsuite.loadcc;

import java.util.Map;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import bsuite.utility.Utility;

public class SetupValidator {

	public Map sessionScope;
	private Database currentdb;
	private String server_g;
	protected Session session;
	protected String bsuitepath;
	public SetupValidator(){
		init();
	}
	

	@SuppressWarnings("unchecked")
	public void init() {
		try{
		FacesContext context = FacesContext.getCurrentInstance();
		this.sessionScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "sessionScope");
		this.currentdb= Utility.getCurrentDatabase();
		this.server_g=currentdb.getServer();
		this.session=Utility.getCurrentSession();
		
		this.bsuitepath = getBsuitePath(currentdb);
		}
		catch(Exception e)

	{
			}
	}
		
	public String getBsuitePath(Database tadb) throws NotesException{
		String bsuitePath=null;
		try {
			int len=(tadb.getFilePath()).length() - (tadb.getFileName()).length();
			bsuitePath=tadb.getFilePath().substring(0,len);
			return bsuitePath;
		} catch (NotesException e) {
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return bsuitePath;
	}
	
	@SuppressWarnings("unchecked")
	public void getSetupDoc(){
		
		
		try {
			System.out.print("Inside getSetupdoc");
			
			//if (iscreate.equals("true")){
			Database custdb = session.getDatabase(session.getServerName(), bsuitepath+"customer.nsf");
				View setupview=custdb.getView("Setup Profile");
				
				if(setupview!=null){
					Document setup=setupview.getFirstDocument();
					if (setup!=null){
						String docid=setup.getUniversalID();
						sessionScope.put("setupDocId", docid);
						sessionScope.put("createSetup","");
						
						System.out.print("complete");		
					}
				
				}
			//}
			
			
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
}
