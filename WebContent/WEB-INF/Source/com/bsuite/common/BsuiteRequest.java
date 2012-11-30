package com.bsuite.common;

import java.util.Date;
import java.util.Vector;

import com.bsuite.err.ErrorHandler;
import com.bsuite.utility.*;

import lotus.domino.Database;
import lotus.domino.DateTime;
import lotus.domino.Document;
import lotus.domino.Item;
import lotus.domino.NotesException;
import com.ibm.xsp.extlib.util.ExtLibUtil;

 /**
  *[This class will be used in creating Bsuite requests]
  *@author VShashikumar
  *@created Oct 5, 2012
 */
public class BsuiteRequest {
	

	/**
	 *[Creates Bsuite P request]
	 *@param requestType, String [ determines the action to be taken ] - This is the number entered in string format that identifies the BSUITE-P request number. This parameter value will be used by 'processBsuitePrequests' subroutine in 'BsuitePLibrary' Script Library to trigger the respective action.
	 *@param selectionType,As String [ 0 - selected document only, 1 - dynamically selected documents ]
	 *@param selectedID As Variant [ UNID of the document/s selected ] - You may also pass a single UNID as string. If no documents are selected then pass the empty "" string
	 *@param searchFormula [ formula can be passed as  the document selection criteria ] - It is a SQL query that will be passed to it. If no query is required then just pass the empty "" string
	 *@param eventType As Integer [ False - immediate request , True - scheduled request] - Immediate requests get processed immediately in a span of 5-10 mins. Scheduled requests get processed ONLY ONCE IN A DAY at 9:00PM.
	 *@param varNewOption As Variant [ any additonal information ] - This parameter can be used effectively to pass any additional information that this BSUITE-P request requires to get a job done. For example, in a BSUITE-P request to delete a user, to varNewOption we can pass the new subsitute that might be required in the place of deleted user. If there is no additional information to be passed then just pass the empty "" string.
	 *@return returns the request document

	 */
	public static Document createRequest(String requestType ,String selectionType, Vector selectedID , String searchFormula , int eventType , Vector varNewOption)

	{
		// eventType:- 'IMMEDIATE - 1, Schedule - 0
		
		Document returnValue;
		returnValue = null;
		
		try 
		{
		
			
			
			Database currentDB = ExtLibUtil.getCurrentDatabase();
			Document request;
			Database mbdb; // 'bsuite-p database
			Document mbprofiledoc ; // ' to get lifetime from manage bsuite
			mbdb = Utility.getDatabase("admntool.nsf");
			
			
			if(!mbdb.isOpen())
			{
				
				mbdb.open();
			}
			
			if (mbdb.isOpen())
			{
				request = mbdb.createDocument();
				request.replaceItemValue("form", "requests") ;
				request.replaceItemValue("ModuleName", currentDB.getFileName());
				request.replaceItemValue("ModuleTitle", currentDB.getTitle());
				String strRequestType ;
			
				if ((requestType.length())>2)
				{
					char temp = requestType.charAt(2);
					
					if (Character.isDigit(temp))
					{
						strRequestType = requestType.substring(0,3);
					}
					else
					{
						strRequestType = requestType.substring(0,2);
					}
					
				}
				else
				{
					strRequestType = requestType.substring(0,2);
				}
				
				
				request.replaceItemValue("RequestType",strRequestType);
				request.replaceItemValue("selectionType", selectionType);
				request.replaceItemValue("SelectedDocID", selectedID); 
				request.replaceItemValue("SearchFormula", searchFormula);
			
			
			
				if (eventType == 0)
				{
					request.replaceItemValue("EventType", 0);
				}
				else
				{
					request.replaceItemValue("EventType", 1);
				}
				
				
				String strDays ;
				
				strDays = null;
				// getGlobalProfile function has been changed to getBsuiteProfile by wasim
			
				mbprofiledoc = Utility.getBsuiteProfile(mbdb); //'profile is not used intentionally as we may_
			
				// 'create requests from other modules but the lifetime of request is defined only in Manage Bsuite's setup profile
				
				if (mbprofiledoc.hasItem("LifeTime"))
				{
					strDays = mbprofiledoc.getItemValueString("LifeTime");
				
				}
				else
				{
					strDays = null;
				}
				
				
				
				
			
				
				if (strDays.isEmpty() )
				{
					
					strDays = "90";
					
				}

				if (!(strDays.equals("0")))	// if set to 0, there is no  Expiry Date
				{
				
					int intDays ;
					intDays = Integer.parseInt(strDays);
					Date date = new Date();
					DateTime dt1 = ExtLibUtil.getCurrentSession().createDateTime(date);
					dt1.setNow();
					dt1.adjustDay(intDays);
					DateTime expDate = ExtLibUtil.getCurrentSession().createDateTime(dt1.getDateOnly());
					request.replaceItemValue("ExpiryDate", expDate);
				
				}	
					
				
				request.replaceItemValue("bsuiteStatus", "0");
				request.replaceItemValue("NewOption",varNewOption); 
				
				String currentUser = ExtLibUtil.getCurrentSession().getEffectiveUserName();
				request.replaceItemValue("DocumentCreator", currentUser);
				request.computeWithForm(true, true) ;
				Item autitem = request.getFirstItem("AuthorEmployees");
		        autitem.setAuthors(true);
		        request.replaceItemValue("AuthorEmployees",currentUser);
				request.replaceItemValue("BsuiteTitle", request.getItemValueString("Request")) ; //'Request field is computed
							
						
				request.save(true, false);
				returnValue = request;
					
					
					
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

		
		return returnValue;
		
	}
	
	

}
