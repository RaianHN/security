package com.bsuite.model;

import java.util.Map;
import java.util.Vector;

import lotus.domino.*;

import com.bsuite.err.ErrorHandler;
import com.bsuite.utility.*;
import com.ibm.xsp.extlib.util.ExtLibUtil;

 /**
  *[Class containing 'Help common action' related business logic]
  *@author VShashikumar
  *@created Oct 12, 2012
 */
public class Help 
{
	
	/**
	 *[To get contextual help for the view]
	 */
	public static void getContextHelpForView()
	{
		Map viewScope = SessionContext.getViewScope();
		Database currentdb = ExtLibUtil.getCurrentDatabase(); 
		Document uprofile = SessionContext.getUserProfile();
		
		try 
		{
			
			String strElement;
			
			View view = currentdb.getView(viewScope.get("uiview").toString());
			
			if (view == null)
			{
				strElement=uprofile.getItemValueString("Defaultview");
				if(strElement.equals(""))
				{
					strElement="doclinkprofile";
				}
			}
			else
			{
				
				Vector aliases=view.getAliases();
				strElement=(String) aliases.elementAt(0);
			}
			String strKey=currentdb.getFileName();
			strKey=strKey+strElement +"2";
			displayBsuiteHelp(strKey);
				
		} 
		catch (NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}
		catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		
		return ;
		
		
		
	}
	
	
	
	
	
	/**
	 *[Sets the view scope variable to unid of the help document]
	 *@param strKey
	 */
	private static void displayBsuiteHelp(String strKey)
	{
		
		Map viewScope = SessionContext.getViewScope();
		try 
		{
			
			String title;
			String unid=null;
			
			Document bhelp=getHelpProfile(strKey);
			
			if ((bhelp!= null))
			{
				title =bhelp.getItemValueString("Title");
				unid = bhelp.getUniversalID();	
				
			}
			else
			{
				
				title="Help content for this page is not defined. Please contact your Local support for any help. Thank you ";
			}
			
			viewScope.put("helpTitle",title);
			viewScope.put("helpDocID", unid);
			
			
		}
		catch (NotesException e) 
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		} 
		catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		
	}
	
	
	/**
	 *[Returns the context specific help document]
	 *@param strKey, dbname+viewName
	 *@return help as document
	 */
	public static Document getHelpProfile(String strKey) 
	{
		Document help=null;
		Database helpdb = Utility.getDatabase("bsthelps.nsf");
		try 
		{
			View view=helpdb.getView("helpprofile");
			
			if(view.equals(null))
			{
				
				return help;
			}
			help=view.getDocumentByKey(strKey,true);
			
			if( help != null)
			{
				return help;
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
			
		
		return help;
	} 
	
	
	
}
