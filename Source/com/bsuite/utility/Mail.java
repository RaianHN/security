package com.bsuite.utility;


import java.util.Vector;
import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;
import lotus.domino.RichTextItem;
import lotus.domino.Session;
import lotus.domino.View;

import com.bsuite.err.ErrorHandler;
import com.ibm.xsp.extlib.util.ExtLibUtil;

 /**
  *[This class will contain all mail related methods, ex sendMailWithDocLink etc]
  *@author VShashikumar
  *@created Oct 5, 2012
 */
public class Mail 
{
	
	
	/**
	 *[Sends mail for the list of users with the link containing the unid of the document]
	 *@param strSubject, subject seen in the mail
	 *@param strMessage, message in the body
	 *@param strTo, Vector, list of mail ids
	 *@param strSender, senders name
	 *@param currentDoc, document object whose unid has to be sent
	 *@param intThread, False to include the doclink
	
	 */
	
	
	@SuppressWarnings("unchecked")
	public static void sendMailWithUNID(String strSubject,String strMessage,Vector strTo,String strSender,Document currentDoc ,boolean intThread )
	{
		
		try{
			
			Document tempdoc = currentDoc;
			Document Memo = ExtLibUtil.getCurrentDatabase().createDocument();
			Memo.replaceItemValue("form", "Memo");
			Memo.replaceItemValue("SendTo", strTo);
			Memo.replaceItemValue("Subject", strSubject);
			RichTextItem x = Memo.createRichTextItem("Body");
			x.appendText("hello");
			x.addNewLine(2);
			
			if (strMessage.equals(null)) 
			{
				strMessage = strSubject + ". Please click the highlighted link to view the document";
			}
			
			x.appendText(strMessage);
			x.addNewLine(2);
			
			if (!intThread)
			{
				String url[]=ExtLibUtil.getXspContext().getUrl().toString().split("\\?");
				x.appendText(url[0]+"?docUNID="+tempdoc.getUniversalID()+"&action=openDocument");
			}
			
			
			x.addNewLine(2);
			
			if (strSender.equals(null)) 
			{
				x.appendText("BSUITE");
			} 
			else 
			{
				x.appendText(strSender);
			}
			
			
			Memo.send();
			Memo.recycle();
		} 
		catch (NotesException e) 
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		
		}
	
		
	}
	
	

	
	
	
	
	//public static void sendMailWithUNID(String strSubject,String strMessage,Vector strTo,String strSender,Document currentDoc ,boolean intThread )
	@SuppressWarnings("unchecked")
	public static void sendMailWithUNID(String dbName, String strSubject,String strMessage,Vector strTo,String strSender,Document currentDoc ,boolean intThread )
	{
		
		try{
			
			Document tempdoc = currentDoc;
			// Document Memo = ExtLibUtil.getCurrentDatabase().createDocument();
			Document Memo = Utility.getDatabase(dbName).createDocument();
			Memo.replaceItemValue("form", "Memo");
			Memo.replaceItemValue("SendTo", strTo);
			Memo.replaceItemValue("Subject", strSubject);
			RichTextItem x = Memo.createRichTextItem("Body");
			x.appendText("hello");
			x.addNewLine(2);
			
			if (strMessage.equals(null)) 
			{
				strMessage = strSubject + ". Please click the highlighted link to view the document";
			}
			
			x.appendText(strMessage);
			x.addNewLine(2);
			/*
			if (!intThread)
			{
				String url[]=ExtLibUtil.getXspContext().getUrl().toString().split("\\?");
				x.appendText(url[0]+"?docUNID="+tempdoc.getUniversalID()+"&action=openDocument");
			}
			*/
			
			if (!intThread)
			{
				String url[]=ExtLibUtil.getXspContext().getUrl().toString().split("\\?");
				x.appendText(url[0]+"?docUNID="+tempdoc.getUniversalID()+"&action=openDocument&module="+dbName);
			}
			
			x.addNewLine(2);
			
			if (strSender.equals(null)) 
			{
				x.appendText("BSUITE");
			} 
			else 
			{
				x.appendText(strSender);
			}
			
			
			Memo.send();
			Memo.recycle();
		} 
		catch (NotesException e) 
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		
		}
	
		
	}
	
	
	
	
	
	/**
	 *[Returns the current users mail database]
	 *@return Mail as Database
	 */
	public static Database getCurrentUserMailDB()
	{
		
		Database usdb=null;
		try 
		{
			Session session=ExtLibUtil.getCurrentSession();
			Database nadb= session.getDatabase("", "names.nsf");
			View uview=nadb.getView("($Users)");
			Document user1=  uview.getDocumentByKey(ExtLibUtil.getCurrentSession().getEffectiveUserName());
			
			if(user1!=null)
			{
				String mailserver= user1.getItemValueString("mailserver");
				String mailfile= user1.getItemValueString("mailfile");
				usdb= session.getDatabase(mailserver, mailfile);
				
			}
			
		} 
		catch (NotesException e) 
		{
			
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		
		return usdb;
	}	

}
