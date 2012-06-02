package com.weberon;

import java.util.Map;

import javax.faces.context.FacesContext;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;

import com.ibm.xsp.extlib.util.ExtLibUtil;

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
		this.currentdb= ExtLibUtil.getCurrentDatabase();
		this.server_g=currentdb.getServer();
		this.session=ExtLibUtil.getCurrentSession();
		//this.bsuiteuser = session.getEffectiveUserName();
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
			System.out.println(e.id + " " + e.text);
		}catch (Exception e){
			e.printStackTrace();
		}
		return bsuitePath;
	}
	
	@SuppressWarnings("unchecked")
	public void getSetupDoc(){
		
		
		try {
			System.out.print("Inside getSetupdoc");
			//String iscreate=(String)sessionScope.get("createSetup");
			//if (iscreate.equals("true")){
			Database custdb = session.getDatabase(session.getServerName(), bsuitepath+"customer.nsf");
				View setupview=custdb.getView("Setup Profile");
				System.out.println("In sidde get setup doc");
				if(setupview!=null){
					Document setup=setupview.getFirstDocument();
					if (setup!=null){
						String docid=setup.getUniversalID();
						sessionScope.put("setupDocId", docid);
						sessionScope.put("createSetup","");
						System.out.println("docid----"+docid);
						System.out.print("complete");		
					}
				
				}
			//}
			
			
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
}
