package com.bsuite.utility;

import java.util.Map;
import java.util.Vector;

import javax.faces.context.FacesContext;

import com.bsuite.err.ErrorHandler;
import com.ibm.xsp.extlib.util.ExtLibUtil;
import lotus.domino.*;

 /**
  *[This class contains utility methods which helps in getting Session specific objects, ex current user name, current database]
  *@author VShashikumar
  *@created Oct 5, 2012
 */
public class SessionContext {
	
	/**
	 *[This method returns the current user's name in required format, ]
	 *@param type 1 for abbreviated, 2 for common, 3 for canonical
	 *@return username as string
	 */
	public static String getFormattedName(int type)
	{
		
		String retValue = "";
		
		try
		{
			Database db=ExtLibUtil.getCurrentDatabase();
			Name name = ExtLibUtil.getCurrentSession().createName(db.getParent().getEffectiveUserName());
			
			
			switch(type)
			{
				case 1:
						retValue =  name.getAbbreviated();
						break;
				case 2:
						retValue = name.getCommon();
						break;
				case 3: 
						retValue = name.getCanonical();
			
			}
			
			
		}
		catch(NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			
		}
		
		return retValue;
		
	}
	
	
	
	
	
	
	/**
	 *[Returns handle to current view scope object]
	 *@return, view scope as map
	 */
	public static Map getViewScope()
	{
		
		FacesContext context = FacesContext.getCurrentInstance();
		Map viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
		return viewScope;
	}
	
	
	
	
	
	
	
	/**
	 *[Returns the selected document from the UI by getting the document id set in the viewscope]
	 *@return, current document as Document
	 */
	public static Document getDocumentToProcess() 
	{
		System.out.print("inside  getDocumentToProcess");
			Object ids;
			Document ret = null;
		
			ids = getViewScope().get("seldocids");
			String classname=ids.getClass().getName();
			
		
			if(classname.equalsIgnoreCase("java.util.vector"))
			{
		
				try 
				{
					ret = ExtLibUtil.getCurrentDatabase().getDocumentByUNID((String) ((Vector)ids).get(0).toString());
					
				} 
				catch (NotesException e) 
				{
					// TODO Auto-generated catch block
					ErrorHandler erh = new ErrorHandler();
					erh.createErrorDocument(e);
				}
			}
			else 
			{
				
				String[] arr =(java.lang.String[])ids;
				
				
				try 
				{
					
					ret = ExtLibUtil.getCurrentDatabase().getDocumentByUNID(arr[0]);
					
				} 
				catch (NotesException e) 
				{
				
					ErrorHandler erh = new ErrorHandler();
					erh.createErrorDocument(e);
				}
				
				
				
			}
		
			
			return ret;
			
	}
	
	
	
	/**
	 *[Returns the user profile document of the current user belonging to current database]
	 *@return
	 */
	public static Document getUserProfile() 
	{
		
		Document uprofile = null;
		try
		{
			Database currentdb = ExtLibUtil.getCurrentDatabase();
			String currentUser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
			uprofile = currentdb.getProfileDocument("userprofile", currentUser);
			
			
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
		
		return uprofile;
	
	}

}
