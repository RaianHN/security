package com.bsuite.utility;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bsuite.utility.SessionContext;
import com.bsuite.utility.DbNames;


import com.bsuite.utility.JSFUtil;

import com.ibm.designer.runtime.lib.I18n;
import com.ibm.domino.services.util.JsonWriter;
import com.ibm.xsp.model.domino.wrapped.DominoDocument;
import lotus.domino.*;

import com.bsuite.err.ErrorHandler;
import com.ibm.xsp.component.UIIncludeComposite;
import com.ibm.xsp.designer.context.XSPContext;
import com.ibm.xsp.extlib.builder.ControlBuilder;
import com.ibm.xsp.extlib.builder.ControlBuilder.ControlImpl;
import com.ibm.xsp.extlib.util.ExtLibUtil;


 /**
  *[This class contains all the helper/utility methods used across bsuite]
  *@author VShashikumar
  *@created Oct 5, 2012
 */

public class Utility 
{
	
	
	
	
	/**
	 *[Returns all the employee names from employee database]
	 *@return Vector containing the employee names
	 */
	@SuppressWarnings("unchecked")
	public static Vector getAllEmployees() 
	{
		
		Vector v = new Vector();  
	
		
		try 
		{
			//Database emdb = getDatabase("employee.nsf");
			Database emdb = getDatabase("Employees");
			if( emdb.isOpen())
			{
				
					
					View emview = emdb.getView("employeeprofile");
					if (emview == null) 
					{
					
						return null;
					}
					
					Document employee = emview.getFirstDocument();
					Name tempname = null;
					while (employee!=null) 
					{
						tempname = ExtLibUtil.getCurrentSession().createName(employee.getItemValueString("BsuiteUser"));
						v.add(tempname.getAbbreviated());
						employee = emview.getNextDocument(employee);
					}
					
					
				
									
			}
		}
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		return v;
		
	}
	
	
	
	
	
	
	
	/**
	 *[Returns the relative path of the database from the data folder of the server]
	 *@param tadb, any database object 
	 *@return the db's relative path in string format
	 */
	public static String getBsuitePath(Database tadb) 
	{
		String bsuitePath=null;
		try 
		{
			int len=(tadb.getFilePath()).length() - (tadb.getFileName()).length();
			bsuitePath=tadb.getFilePath().substring(0,len);
		
		} 
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		
		return bsuitePath;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 *[Returns the database of the specified name 
	 * 
	 * @param dbTitle Title of the database ex:AuditTrail, Documents, Employees, ErrorCodes, Favorites, Help, InOut,
	Policies, Profiles, Reports, Telemarketing, ManageBsuite
	 *      
	 *@return the database object of required database
	 */
	public static Database getDatabase(String dbTitle) {
		/*Pass the given below key strings to get the databases ex: if you are looking for employees.nsf call getDatabase("Employees")
		 * 
		 * AuditTrail Documents Employees ErrorCodes Favorites Help InOut
		 * Policies Profiles Reports Telemarketing, ManageBsuite
		 */

		
		Database db = null;
		DbNames dbNames = (DbNames) JSFUtil.getVariableValue("dbnames");
		
		if(dbNames==null){
			Utility.setErrorString("dbNames bean is not initialized", "true");
			return null;
		}
		try {
			
			String dbPath = dbNames.getDbPath(dbTitle);
			//db = Utility.getCurrentSession().getDatabase("", dbPath);
			db = ExtLibUtil.getCurrentSession().getDatabase("", dbPath);

		}

		catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}

		return db;

	}

	
	
	
	
	/**
	 *[Returns the setup profile document of any database]
	 *@param mbdb, manage bsuite database object
	 *@return setup profile document
	 */
	public static Document getBsuiteProfile(Database db) 
	{
		Document bprofile=null;
		try 
		{
			if (db.isOpen())
			{
				View view=db.getView("setupprofile");
				bprofile=view.getFirstDocument();
				
				if(!(bprofile.equals(null)))
				{
					return bprofile;
				}
			}
		} 
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		
		return bprofile;
	}
	
	
	
	
	/**
	 *[Returns the module document by looking into the module in manage bsuite module profile view]
	 *@param mbdb, manage bsuite database object
	 *@param ModuleName, name of the module you want
	 *@return, module profile as document
	 */
	public static Document getModuleProfile(Database mbdb,String ModuleName) 
	{
		Document module = null;
		
		try 
		{
			if (mbdb.isOpen())
			{
				View view = mbdb.getView("moduleprofile");
				module = view.getDocumentByKey(ModuleName,true);
				
			}
		} 
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		
		return module;
	}
	/**
	 *[Returns the fully qualified domain name as specified in the setup profile document of manage bsuite]
	 *@return, fqdn as string
	 */
	public static String getFQDN() {
		//Database mbdb = getDatabase("admntool.nsf");
		Database mbdb = getDatabase("ManageBsuite");
		String fqdn = "";
		try {
			if (mbdb.isOpen()) {
				Document mprofile = getBsuiteProfile(mbdb);
				if (mprofile != null) {
					fqdn = mprofile.getItemValueString("FQDN");
				}
			}
		} catch (NotesException e) {
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
		return fqdn;
	}
	
	
	/**
	 *[Returns the appropriate number related to the acl level of the current database]
	 *@return, acl level as int
	 */
	
	
	public static int getACLLevel() {
		int level = 0;
		try {
			ACL acl = ExtLibUtil.getCurrentDatabase().getACL();
			ACLEntry aclentry = acl.getEntry("Anonymous");
			
			if (aclentry == null) {
				
				level = 0;
			} else {
				
				level = aclentry.getLevel();
			}
		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		return level;

	}
	
	
	
	
	
	
	
	
	
	
	
	
	//public static int getACLLevel()
	public static int getACLLevel(String dbName) {
		int level = 0;
		try {
			//ACL acl = ExtLibUtil.getCurrentDatabase().getACL();
			ACL acl = getDatabase(dbName).getACL();
			ACLEntry aclentry = acl.getEntry("Anonymous");
			
			if (aclentry == null) {
				
				level = 0;
			} else {
				
				level = aclentry.getLevel();
			}
		} catch (NotesException e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e) {

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		return level;

	}
	
	/**
	 *[Returns the modified string by replacing it with new substring]
	 *@param mainStr, main string
	 *@param SearchStr, string to be replaced
	 *@param replaceStr, will be replaced by this string
	 *@return, modified string
	 */
	public static String replaceChar(String mainStr, String SearchStr,String replaceStr) {
		try {
			String replaceChar = mainStr;
			int pos;
			pos = replaceChar.indexOf(SearchStr);
			// Loop through the contents of the MainStr until all the SearchStr
			// are
			// replaced by ReplaceStr
			if (pos != 0) {
				replaceChar = replaceChar.substring(0, pos) + replaceStr+ replaceChar.substring(pos);
				pos = replaceChar.indexOf(SearchStr);
				while (pos != 0) {
					replaceChar = replaceChar.substring(0, pos) + replaceStr+ replaceChar.substring(pos);
					pos = replaceChar.indexOf(SearchStr);
				}
			}
			return replaceChar;
		} catch (Exception ex) {
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(ex);			
		}
		return "";
	}
	
	
	/**
	 *[Returns IP Address or host name based on type]
	 *@param strType, IP to get the ip address
	 *@return, host infor as string
	 */
	public static String getHostInfo(String strType) 
	{
		String strHost = "";
		try 
		{
			ExternalContext exCon = FacesContext.getCurrentInstance().getExternalContext(); 
			HttpServletRequest request= (HttpServletRequest) exCon.getRequest();
			String remoteAddress = request.getLocalAddr();//getRemoteAddr();//getLocalName();//getRemoteHost();//
			InetAddress inetAddress = InetAddress.getByName(remoteAddress);
		
			
			if (strType.equals("IP"))
			{
				strHost = inetAddress.getHostAddress();
			}
			else 
			{
				strHost = inetAddress.getHostName();
			}
		} 
		
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}

		return strHost;
	}

	/**
	 *[Sets the last DocumentModifier and ModifiedOn fields of the given document to current username and current time]
	 *@param doc, given document
	 */
	public static void setLastModified(Document doc) {
		
		try 
		{
			Date date = new Date(); 
			DateTime dt1 = ExtLibUtil.getCurrentSession().createDateTime(date);
			dt1.setNow();
			doc.replaceItemValue("DocumentModifier", ExtLibUtil.getCurrentSession().getEffectiveUserName());
			doc.replaceItemValue("ModifiedOn", dt1);
		}
		
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}

	}
	
	/**
	 *[Sets the last DocumentModifier and ModifiedOn fields of the given document to current username and current time]
	 *@param doc, given DominoDocument
	 */
public static void setLastModified(DominoDocument doc) {
		
		try 
		{
			// update document modifier name and modified date 
			Date dt = I18n.convertDateFromServerTimeZone(new Date(), Utility.getXspContext().getTimeZone());
			doc.replaceItemValue("DocumentModifier", ExtLibUtil.getCurrentSession().getEffectiveUserName());
			doc.replaceItemValue("ModifiedOn", dt);
		}
		
		catch(NotesException e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}
		catch(Exception e)
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}

	}
	
	
	
	/**
	 *[This method returns the users name in required format, ]
	 *@param varValues List of user names
	 *@param ctype type 1 for abbreviated, 2 for common, 3 for canonical
	 *@return Vector formated usernames
	 */
	@SuppressWarnings("unchecked")
	public static Vector getFormattedNames(Vector varValues, int ctype)
	{
		Vector names = new Vector();
		try
		{
			if (varValues.equals(null))
			{
				return null;
			}

			for (Object x : varValues)
			{
				Name sname = ExtLibUtil.getCurrentSession().createName(
						x.toString());

				String strName = "";
				switch (ctype)
				{
				case 1:
					strName = sname.getAbbreviated();
					break;
				case 2:
					strName = sname.getCommon();
					break;
				case 3:
					strName = sname.getCanonical();
					break;

				}

				names.addElement(strName);
			}

			return names;
		} catch (NotesException e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);

		} catch (Exception e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);

		}

		return names;
	}
	

public static Document getEmployeeProfile(String empName) 
{
	/*
	 *  this method return employee document of employee name pass in argument
	 */
	Document employee = null; 

	
	try 
	{
		//Database emdb = getDatabase("employee.nsf");
		Database emdb = getDatabase("Employees");
		if( emdb.isOpen())
		{
			View emview = emdb.getView("employeeprofile");
				if (emview != null) 
				{
					employee = emview.getDocumentByKey(empName);
			
				}
				
		}	
				
			
								
		
	}
	catch(NotesException e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);			
	}
	catch(Exception e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);			
	}
	return employee;
	
}










/**
 * Return the current XspContext.
 */
public static XSPContext getXspContext() {
    return XSPContext.getXSPContext(FacesContext.getCurrentInstance());
}







/**
 *[This method is check the Bsuite role ]
 *[Other comments describing preconditions, postconditions, algorithm notes,usage instructions, reminders, etc.]
 *@param strRoleTitle
 *@return true if user have role , false if user do not have role
 *
*/
@SuppressWarnings("unchecked")
public static boolean checkBsuiteRole(String strRoleTitle) 
{
	
		try 
		{
		
		
			Vector varRoles;
			//Database emdb = getDatabase("employee.nsf");
			Database emdb = getDatabase("Employees");
			
			
			varRoles = emdb.queryAccessRoles( ExtLibUtil.getCurrentSession().getEffectiveUserName());

			for (Object x : varRoles)
			{
				if ( ((String)x).equalsIgnoreCase(strRoleTitle) )
				{
					return (true);
				
				}
			}

			
	
		}

		catch(NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
			
		}
		catch(Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
			
		}

		return (false);
}







/**
 *[This method return the branch profile document from branch profile view]
 *[Other comments describing preconditions, postconditions, algorithm notes,usage instructions, reminders, etc.]
 *@param strBranch, Branch to get Branch document
 *@return Branch document
 *@throws NotesException
 *
*/
public static Document getBranchProfile(String strBranch) throws NotesException{
	Document branch=null;
	try {
		if(strBranch != "" ){
			//Database emdb = getDatabase("employee.nsf");
			Database emdb = getDatabase("Employees");
			View view=emdb.getView("branchprofile");
			branch=view.getDocumentByKey(strBranch,true);
			if( branch != null){
				return branch;
			}	
		}
	} catch(NotesException e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	catch(Exception e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	} finally{}
		return branch;
}







/**
 *[This method returns the set up profile document]
 *[Other comments describing preconditions, postconditions, algorithm notes,usage instructions, reminders, etc.]
 *@param tadb, datebase name
 *@return setup profile document
 *@throws NotesException
 *
*/
public static Document getGlobalProfile(Database tadb) throws NotesException{
	Document profile=null;
	try {
		View view=tadb.getView("setupprofile");
		profile=view.getFirstDocument();
		if(!(profile.equals(null))){
			return profile;
		}
	} catch (NotesException e) {
		System.out.println(e.id + " " + e.text);
	}catch(Exception e)
	{
		e.printStackTrace();
	}
	return profile;
	
}







/**
 *[This method returns the Leave Account profile of the user.]
 *[Other comments describing preconditions, postconditions, algorithm notes,usage instructions, reminders, etc.]
 *@param strEmployee, user to get Leave account
 *@return Leave account document
 *
*/
public static Document getLeaveAccount(String strEmployee){
	 Document lprofile=null;
	 View view;
	 try{
		 //Database db=ExtLibUtil.getCurrentDatabase();
		 Database db = getDatabase("Employees");
		 view = db.getView("accountprofile");
		 strEmployee = SessionContext.getFormattedNamewithagrs(strEmployee, 3);
		 lprofile = view.getDocumentByKey(strEmployee, true);
		 return lprofile;
	 }
	 
	 catch(NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
			
		}
		catch(Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
			
		}
	 return null;
 }







/**
 *[ This method returns the percentage of Leave, number of employee on leave and name of user ]
 *@param date , date for need to calculate percentage
 *@param BsuiteUser , current user
 *
*/
@SuppressWarnings("unchecked")
public static void getLeavePercentage(DateTime date,String BsuiteUser)
{
	try{
		double count=0,empcount=0;
	 Session session = ExtLibUtil.getCurrentSession();
		String office;
		DocumentCollection dc;
		DateTime leavedate;
		Vector<String> empleave=new Vector<String>();
		 //Database db=ExtLibUtil.getCurrentDatabase();
		 Database db= getDatabase("Employees");
	
		StringBuilder searchFormula=new StringBuilder();
		searchFormula.append("([Form = bleave OR leave ] AND ([Status] CONTAINS 3) )");
		dc=db.FTSearch(searchFormula.toString());
		Document empdoc=getEmployeeProfile(SessionContext.getFormattedName(1));
		office=empdoc.getItemValueString("Office");
		Document doc=dc.getFirstDocument();
		while(doc!=null)
		{
			
			if(office.equals(doc.getItemValueString("Office")))
			{
				
				
				for(Object val:doc.getItemValue("LeaveDates"))
				{
					
					String []ret=val.toString().split("~");
					if ((ret[1].equalsIgnoreCase("Holiday")) || (ret[1].equalsIgnoreCase("Weekly Holiday"))||(ret[1].equalsIgnoreCase("Leave applied in the Past")) ||(ret[1].equalsIgnoreCase("Signed In"))) 
					{
						
						continue;
					}
					
					else
					{
						
						leavedate=session.createDateTime(ret[0]);
						
					
						if(Utility.timeCompareDates(date, leavedate)==0)
						
						{
							
							
							count=count+1;
							Name nam = session.createName(doc.getItemValueString("BsuiteUser"));

					          

							empleave.add(nam.getAbbreviated());
						}
						
					}
				}
				
			}
			
			
			doc=dc.getNextDocument(doc);
		}
				
		View view = db.getView("employeeprofile");
		Document empdoc1=view.getFirstDocument();
		while(empdoc1!=null)
		{
			if(office.equals(empdoc1.getItemValueString("Office")))
			{
				empcount=empcount+1;
			}
			empdoc1=view.getNextDocument(empdoc1);
		}
		Double peremp=round(((count/empcount)*100),2);
		
	Map viewScope=SessionContext.getViewScope();
	viewScope.put("emplist",empleave);
	
	viewScope.put("percentage",peremp);
	viewScope.put("numberemp",empcount);
	viewScope.put("leaveemp",count);
	
				
	
	}
	catch(NotesException e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	catch(Exception e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	
}







/**
 *[This method returns the office profile of the branch from manage bsuite]
 *
 *@param mbdb
 *@param strBranch
 *@return - Document object, office document
 *@throws NotesException
 *
*/
public static Document getOfficeProfile(Database mbdb,String strBranch) throws NotesException{
	Document office=null;
	try {
		View view=mbdb.getView("officeprofile");
		office=view.getDocumentByKey(strBranch,true);
		if( office != null){
			return office;
		}
	} catch(NotesException e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	catch(Exception e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	} finally{}
		return office;
}







public static double round(double d, int decimalPlace){
    // see the Javadoc about why we use a String in the constructor
    // http://java.sun.com/j2se/1.5.0/docs/api/java/math/BigDecimal.html#BigDecimal(double)
    BigDecimal bd = new BigDecimal(Double.toString(d));
    bd = bd.setScale(decimalPlace,BigDecimal.ROUND_HALF_UP);
    return bd.doubleValue();
  }







/**
 *[This method is use to compare two date and returns the difference of date]
 *[Other comments describing preconditions, postconditions, algorithm notes,usage instructions, reminders, etc.]
 *@param dt1, first date
 *@param dt2, second date
 *@return 0 if both date is equal, >1 if First document is greater, <1 if second date is greater
 *@throws NotesException
 *
*/
public static int timeCompareDates(DateTime dt1, DateTime dt2) throws NotesException{
	 Session session;
	 session = ExtLibUtil.getCurrentSession();
	int timecomp=0;
	try {
		if (dt1.equals(null) || dt2.equals(null)){
			return 0;
		}
		DateTime dtMy1 = session.createDateTime(dt1.getDateOnly());
		
		DateTime dtMy2 = session.createDateTime(dt2.getDateOnly());
		
		timecomp = dtMy1.timeDifference(dtMy2)/86400; 
		
		return timecomp;
	} catch(NotesException e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	catch(Exception e)
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
		
		
	}
	
	return timecomp;
}






/**This method will return only dbfilename with .nsf when the database title is passed
@param dbTitle Title of the database ex:AuditTrail, Documents, Employees, ErrorCodes, Favorites, Help, InOut,
Policies, Profiles, Reports, Telemarketing, ManageBsuite
*@return
*/
public static String getDatabaseFileName(String dbTitle){
	DbNames dbNames = (DbNames) JSFUtil.getVariableValue("dbnames");
	
	if(dbNames==null){
		Utility.setErrorString("dbNames bean is not initialized", "true");
		return null;
	}
	return dbNames.getDbFileName(dbTitle);
	
}



@SuppressWarnings("unchecked")
public static boolean setErrorString(String errsms, String errval) {
	try {

		Map viewScope = SessionContext.getViewScope();
		viewScope.put("errsms", errsms);
		viewScope.put("errval", errval);
	} catch (Exception e) {

		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);

	}
	return true;

}


public static String getSetupDocID(String dbName)
{
	String docid = null;
	try 
	{
		Database currentdb = getDatabase(dbName);
		View setupview=currentdb.getView("setupprofile");
		if(setupview!=null)
		{	
			Document setup=setupview.getFirstDocument();
			if (setup!=null)
			{
				docid=setup.getUniversalID();
			}
		}
	}
	catch (NotesException e) 
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
	} 
	catch (Exception e) 
	{
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);
	}
	return docid;
}





@SuppressWarnings({ "unchecked", "static-access" })
public static void loadCustomControl(UIComponent r, FacesContext s, String cc)
{
	int childcount=r.getChildCount();
	if(childcount>0)
	{
		for(int i=0;i< childcount;i++)
		{
			r.getChildren().remove(0);
		}
	}	
	

    UIIncludeComposite result = new UIIncludeComposite();
    result.setPageName(cc);
    ControlBuilder objBuilder = new ControlBuilder();
	ControlImpl con = new ControlImpl(r);
	ControlImpl con1 = new ControlImpl(result);
	con.addChild(con1);
    objBuilder.buildControl(s,con,true);
    
	
}











/**
 *[Returns the last refreshed date of the given database]
 *@param tadb, database object for which last refresh date is required
 *@return date string
 */
private static String getLastRefreshed(Database tadb) 
{

	try
	{
		Document doc;
		Item item;
		NoteCollection nc;
		String nid;
		String nextid;
		String strModified = "";
		int i;
		nc = tadb.createNoteCollection(false);
		nc.setSelectForms(true);
		nc.buildCollection();
		nid = nc.getFirstNoteID();
		
		for (i = 1; i < nc.getCount(); i++) 
		{
			nextid = nc.getNextNoteID(nid);
			doc = tadb.getDocumentByID(nid);
			item = doc.getFirstItem("$TITLE");
			if (item.containsValue("AboutBSUITE")) 
			{
				strModified = doc.getLastModified().toString();
				return strModified;
			}
			nid = nextid;
		}
		return strModified;
	}
	catch(NotesException e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);				
	}
	catch(Exception e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);				
	}
	return "";

}









/**
 *[Calls renderServideJsonGet]
 */
public static void renderService() 
{
	try
	{
		FacesContext ctx = FacesContext.getCurrentInstance();		
		ExternalContext exCon = ctx.getExternalContext(); 			
		HttpServletRequest request = (HttpServletRequest)exCon.getRequest();

		String method = request.getMethod();
		if ("GET".equalsIgnoreCase(method)) 
		{
			renderServiceJSONGet();
		} 
	}
	
	catch(Exception e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);				
	}
}



/**
 *[Creates required data as a json string for about bsuite request]
 */
@SuppressWarnings({ "unchecked" })
private static void renderServiceJSONGet()
{

	try 
	{
		FacesContext ctx = FacesContext.getCurrentInstance();		
		ExternalContext exCon = ctx.getExternalContext(); 			
		ResponseWriter writer = ctx.getResponseWriter();
		HttpServletResponse response = (HttpServletResponse)exCon.getResponse();			

		response.setContentType("application/json");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("utf-8");
		
		boolean compact = false;
	
		JsonWriter g = new JsonWriter(writer, compact); 

		g.startArray();
	
		Vector modules=new Vector();
		Vector version=new Vector();
		Vector base=new Vector();
		Vector refreshedon=new Vector();
		Database mbdb = Utility.getDatabase("ManageBsuite");	
	
		if (mbdb.isOpen())
		{
			View moduleView=mbdb.getView("moduleprofile");
			Document module=moduleView.getFirstDocument();
			while(module!=null)
			{
				//Database tadb =Utility.getDatabase(module.getItemValueString("ModuleName"));
				Database tadb =Utility.getDatabase(module.getItemValueString("ModuleTitle").replaceAll(" ", ""));
				
				
				
				if (tadb.isOpen())
				{
					Form abtBsuite=tadb.getForm("AboutBsuite");
					if (abtBsuite!=null)
					{
						String alias=(String) abtBsuite.getAliases().elementAt(0); 
						if (!(alias.equals(null)))
						{
							String moduleTitle=module.getItemValueString("ModuleTitle");
							if (moduleTitle.length()>22)
							{
								moduleTitle=moduleTitle.substring(0, 22)+"...";
								modules.addElement(moduleTitle);
							}
							else
							{
								modules.addElement(moduleTitle);
							}
							if (alias.contains("~"))
							{
								String[] aliaslist=alias.split("~");
							
								if (!(aliaslist[0].equals("")))
								{
								version.add(aliaslist[0].substring(0, 8));
								}
								else
								{
									version.add("-");
								}
								if (!(aliaslist[2].equals("")))
								{
									base.add(aliaslist[2].substring(0, 8));
								}
								else
								{
									base.add("-");
								}
							
							}
							else
							{
								version.add(alias.substring(0, 8));
								base.add("-");
							}
						
							refreshedon.add(getLastRefreshed(tadb));
						}
					
					}
				}
				module=moduleView.getNextDocument(module);
			}
		}

	 
		for (int i = 1; i <= modules.size(); i++) 
		{
			
			g.startArrayItem();	        	
			g.startObject();
			g.startProperty("id");
			g.outStringLiteral((String)(""+i));
			g.endProperty();
			g.startProperty("property1");
			g.outStringLiteral((String)modules.elementAt(i-1));
			g.endProperty();
			g.startProperty("property2");
			g.outStringLiteral((String)version.elementAt(i-1));
			g.endProperty();
			g.startProperty("property3");
			g.outStringLiteral((String)base.elementAt(i-1));
			g.endProperty();
			g.startProperty("property4");
			g.outStringLiteral((String)refreshedon.elementAt(i-1));
			g.endProperty();
			g.endObject();        
			g.endArrayItem();		        		       			
		}	
		g.endArray();
	 
	} 
	catch(NotesException e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);				
	}
	catch(Exception e)
	{
		
		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);				
	}
}	



@SuppressWarnings("unchecked")
public static Vector getSupervisors(String empName)
{
	
	String manager= "";
	String employee = "";
	Vector ret = new Vector();
	try
	{
		Document doc = getEmployeeProfile(empName);
		while (doc != null)
		{
			manager = doc.getItemValueString("Manager");
			employee = doc.getItemValueString("BsuiteUser");
			if ((!manager.equals(""))&&(manager != null))
			{
				if (manager.equals(employee))
				{
					ret.add(manager);
					doc = null;
					
				}
				else
				{
					ret.add(manager);
					doc = getEmployeeProfile(manager);	
					
				}
				
			}
			else
			{
				doc = null;
			}
			
		}
		
		
	}
	

	
	catch (Exception e) 
	{

		ErrorHandler erh = new ErrorHandler();
		erh.createErrorDocument(e);

	}
	
	
	if (ret.size() == 0)
	{
		ret = null;
	}
	
	return ret;


}




}
