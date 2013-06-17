package bsuite.security;

import java.util.Collections;
import java.util.Vector;

import bsuite.err.ErrorHandler;
import bsuite.utility.SessionContext;
import bsuite.utility.Utility;

import bsuite.utility.Utility;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.View;

 /**
  *[Class containing security related methods]
  *@author VShashikumar
  *@created Oct 12, 2012
 */
public class UserAccess 
{
	
	
	/**
	 *[Sentence summarizing the method's purpose and functionality.]
	 *@param doc
	 *@param canEmployee
	 *@return
	 */
	public static boolean checkAccess(Document doc, String canEmployee) 
	{
		
			boolean ret = true;
			
			try 
			{
				String strEditor;
			
				if (checkBsuiteRole("[Maintenance]") ==  false)
				{
					if (checkBsuiteRole("[SuperUser]") == false)
					{
					
						strEditor = doc.getItemValueString("BranchHead") ;
						if (strEditor != canEmployee)
						{
							for(Object v1 : doc.getItemValue("AuthorEmployees"))
							{
								if (((String)v1).equalsIgnoreCase(canEmployee))
								{
									ret = true;
									break;
								}
								else 
								{
									ret = false;
									break;
								}
							}				
						}
					}			
				}
			
			}
	
			catch(NotesException e)
			{
				ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e);
			}
			
			catch (Exception e1)
			{
				ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e1);
			}
		
			return ret;	
	}
	
	
	
	
	/**
	 *[Checks if the user has the given role for the current databse]
	 *@param strRoleTitle, role name
	 *@return, true if has the given role, false otherwise
	 */
	public static boolean checkBsuiteRole(String strRoleTitle) 
	{
		
		boolean ret = false;
		try 
		{
			
			
			Vector varRoles;
			varRoles = Utility.getCurrentDatabase().queryAccessRoles(Utility.getCurrentSession().getEffectiveUserName());

			for (Object x : varRoles)
			{
				if ( ((String)x).equalsIgnoreCase(strRoleTitle) )
				{
					ret = true;
					break;
					
				}
			}

				
		
		}
	
		catch(NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
			
		catch (Exception e1)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);
		}
	
		return ret;
	}
	/**
	 *[Checks if the user belongs to the given role as specified in the profiles databases role based employee document]
	 *@param strRoleTitle, rolename
	 *@param dbName, module name
	 *@return, true if the current user belongs to given role
	 */
	public static boolean hasModuleRole(String strRoleTitle, String dbName)
	{

		Vector varValues;
		varValues = getRoleBasedEmployees(dbName, strRoleTitle);
		if (!(varValues == null))
		{
			for (Object x : varValues)
			{
				String fname = SessionContext.getFormattedName(1);
				String nam = x.toString();
				if (nam.equals(fname))
				{
					return true;
				}
			}
		}

		return false;

	}
	/**
	 *[Gets the list of users who belong to a given role from profile databases role based employee document]
	 *@param strFileName
	 *@param strRoleName
	 *@return
	 */
	public static Vector getRoleBasedEmployees(String strFileName,
			String strRoleName)
	{
		Vector emp = null;
		try
		{

			Database pfdb = Utility.getDatabase("profiles.nsf");
			if (pfdb.isOpen())
			{
				View view = pfdb.getView("roleprofile");
				if (view.equals(null))
				{
					return null;
				}
				String key = strFileName + strRoleName;
				Document pfdoc = view.getDocumentByKey(key, true);
				if (pfdoc != null)
				{

					emp = Utility.getFormattedNames(pfdoc
							.getItemValue("Members"), 1);
					Collections.sort(emp);
					return emp;
				}
			}
		} catch (NotesException e){
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		return emp;
	}

}
