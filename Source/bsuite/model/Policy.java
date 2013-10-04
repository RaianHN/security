package bsuite.model;

import bsuite.common.*;
import bsuite.err.ErrorHandler;
import bsuite.utility.*;


import lotus.domino.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;



 /**
  *[This class will hold all the business logic related to policies]
  *@author VShashikumar
  *@created Oct 5, 2012
 */
public class Policy {
	
	private  Document currentdoc;
	
	
	
	/**
	 * @param doc, initialize the current document, mail object is initialized since it is used in all the methods 
	 */
	public Policy(Document doc)
	{
		
		currentdoc = doc;
		
		
	}
	
	
	
	
	/**
	 *[This will save the newly created policy document in draft status]
	 */
	
	public void saveAsDraft() 
	{
		
		
		try
		{
				
			currentdoc.replaceItemValue("bsuitestatus", "1");
			currentdoc.replaceItemValue("Status","1");
			currentdoc.replaceItemValue("ReviewedBy",SessionContext.getFormattedName(1));
			currentdoc.replaceItemValue("obid", "policy");
			currentdoc.replaceItemValue("DocumentModifier",SessionContext.getFormattedName(1));
			currentdoc.replaceItemValue("ModifiedOn",DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
			
			
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
	 *[This will set the policy document in sentForReview status and will send a mail for the selected reviewer with doclink for this document ]
	 */
	public void sendForReview()
	{
		
		try
		{
			currentdoc.replaceItemValue("bsuitestatus", "1");
			currentdoc.replaceItemValue("Status","2");
			currentdoc.replaceItemValue("DocumentModifier", SessionContext.getFormattedName(1));
			currentdoc.replaceItemValue("ModifiedOn",DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
			currentdoc.save();
			String strSubject = "This policy has been submitted for review";	
			String strMessage = "Please click here to open the document";
			String strSender = SessionContext.getFormattedName(1);
		
			Mail.sendMailWithUNID(strSubject, strMessage, currentdoc.getItemValue("ReviewedBy"), strSender, currentdoc, false);
			
			
			
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
	 *[This will set the policy document in Approved status and will send a mail for the policy creator with the document link for this policy document]
	 */
	public void approve() 
	{
		try
		{
			
			currentdoc.replaceItemValue("bsuitestatus", "1");
			currentdoc.replaceItemValue("Status","3");
			currentdoc.replaceItemValue("obid", "policy");
			String strSubject = "This policy has been approved";
			String strMessage = "This policy has been approved";
			String strSender = SessionContext.getFormattedName(1);
			currentdoc.replaceItemValue("ApprovedBy", strSender);
			currentdoc.replaceItemValue("ApprovedOn", DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
			currentdoc.replaceItemValue("DocumentModifier",strSender);
			currentdoc.replaceItemValue("ModifiedOn",DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
			currentdoc.save();
			
			Mail.sendMailWithUNID(strSubject, strMessage, currentdoc.getItemValue("DocumentCreator"), strSender,currentdoc, false);
			
			
			
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
	 *[This will set the policy document in Denied status and will send a mail for the policy creator with the document link for this policy document]
	 */
	public void deny() 
	{
		
		
		try
		{
			
			String strSubject = "This policy has been denied";
			String strMessage = "This policy has been denied";
			String strSender = SessionContext.getFormattedName(1);
			currentdoc.replaceItemValue("bsuitestatus", "1");
			currentdoc.replaceItemValue("Status","4");
			currentdoc.replaceItemValue("DocumentModifier",strSender);
			currentdoc.replaceItemValue("ModifiedOn",DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
			currentdoc.save();
			
			if(currentdoc.getItemValueString("Status").equals("4"))
			{
				String strType = "Denied Policy";
				strSubject = " This policy has been denied ";
				String bodyMessage=strSubject + " by " + strSender+"at :"+DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date());
				CommonActions.createResponse(strType,strSubject,bodyMessage, currentdoc);
			}
			
			Mail.sendMailWithUNID(strSubject, strMessage, currentdoc.getItemValue("DocumentCreator"), strSender,currentdoc, false);
			
			
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
	 *[This will set the policy document in Published status and will send a mail for all the employees in employee database with the document link for this policy document]
	 */
	public void publish() 
	{

		
		try
		{
			
			String var1, var2, var3;

			String strSubject = "A new policy has been published on : " + currentdoc.getItemValueString("Subject");
			String strMessage = "Please click here to open the document";
		
			
			String strSender = SessionContext.getFormattedName(1);
			
			
			var1 = currentdoc.getItemValueString("ApprovedBy");
			var2 = currentdoc.getItemValueString("ApprovedOn");
			var3 = currentdoc.getItemValueString("Status");
			
			if (var3.equals("1")) 
			{
				currentdoc.replaceItemValue("ReviewedBy", strSender);
			}

			
			currentdoc.replaceItemValue("bsuitestatus", "1");
			currentdoc.replaceItemValue("Status", "5");
			currentdoc.replaceItemValue("obid", "policy");
		
			if (var1.equals("") || var2.equals("")) 
			{
				currentdoc.replaceItemValue("ApprovedBy", strSender);
				currentdoc.replaceItemValue("ApprovedOn", DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
			}
			currentdoc.replaceItemValue("PublishedBy", strSender);
			currentdoc.replaceItemValue("PublishedOn", DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
			
			currentdoc.replaceItemValue("DocumentModifier", strSender);
			currentdoc.replaceItemValue("ModifiedOn", DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date()));
			currentdoc.save();
			
			Mail.sendMailWithUNID(strSubject, strMessage,Utility.getAllEmployees(), strSender, currentdoc, false);

			
			
			
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
	 *[Creates a request to sign the selected policy document]
	 */
	@SuppressWarnings("unchecked")
	public void signPolicy()
	{
		
		try
		{
			Vector docUNID = new Vector();
			Vector signedBy = new Vector();
			
			
			String strEmployee = SessionContext.getFormattedName(1);	
			signedBy.addElement(strEmployee);
			String unid = currentdoc.getUniversalID();
			docUNID.addElement(unid);
			BsuiteRequest.createRequest("66F","1",docUNID,"",1,signedBy);
			
			
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
	

}
