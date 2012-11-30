package com.bsuite.utility;

import java.net.InetAddress;
import java.util.Date;
import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lotus.domino.*;

import com.bsuite.err.ErrorHandler;
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
	public static Vector getAllEmployees() 
	{
		
		Vector v = new Vector();  
	
		
		try 
		{
			Database emdb = getDatabase("employee.nsf");
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
	 *[Returns the database of the specified name if it contains in the data folder]
	 *@param dbName name of the database including .nsf, ex: employee.nsf
	 *@return the database object of required database
	 */
	public static Database getDatabase(String dbName)
	{
		Database db = null;
		try
		{
			Database currentDB = ExtLibUtil.getCurrentDatabase();
			String bsuitepath = getBsuitePath(currentDB);
			db = currentDB.getParent().getDatabase( "",bsuitepath + dbName);
			
			
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
		Database mbdb = getDatabase("admntool.nsf");
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
			System.out.println("acl entry 0");
			if (aclentry == null) {
				System.out.println("acl entry 1");
				level = 0;
			} else {
				System.out.println("acl entry 2");
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
	 *[This method returns the users name in required format, ]
	 *@param varValues List of user names
	 *@param ctype type 1 for abbreviated, 2 for common, 3 for canonical
	 *@return Vector formated usernames
	 */
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
}
