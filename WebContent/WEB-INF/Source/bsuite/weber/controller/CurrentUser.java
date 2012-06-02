package bsuite.weber.controller;
import java.util.Map;

import javax.faces.context.FacesContext;

import lotus.domino.NotesException;

import bsuite.weber.model.BsuiteWorkFlow;
import bsuite.weber.model.User;


public class CurrentUser extends BsuiteWorkFlow{
 private User cur_user;


private Map sessionScope;

public CurrentUser() {
	super();
	// TODO Auto-generated constructor stub
}
 public void init(){
	 
		sessionScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "sessionScope");
		this.getDocumenttoProcess();
 }
//UI Document handlers
	protected void getDocumenttoProcess() {
		
		Object ids= sessionScope.get("currentunid");
		//System.out.println(ids.getClass().getName());
		String[] arr = (java.lang.String[]) ids;
		try {
			//System.out.println(ids);
			cur_user= new User(currentdb.getDocumentByID(arr[0]));
	
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
	}
public User getCur_user() {
	init();
	return cur_user;
}

public void setCur_user(User cur_user) {
	this.cur_user = cur_user;
}
 
}
