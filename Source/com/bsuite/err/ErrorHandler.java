package com.bsuite.err;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.Vector;

import lotus.domino.*;

import com.bsuite.utility.Utility;
import com.ibm.xsp.extlib.util.ExtLibUtil;

public class ErrorHandler
{

	@SuppressWarnings("unchecked")
	private Vector stackTrace;
	private String errMsg;
	private String methodName;
	private String className;
	private int errLine;

	public void createErrorDocument(Exception ex)
	{
	
		//Database audb = Utility.getDatabase("bstaudit.nsf");
		Database audb = Utility.getDatabase("AuditTrail");
		Document edoc = null;
		setErrorLogFields(ex);
		
		try
		{
			//Database currentDb = ExtLibUtil.getCurrentSession().getCurrentDatabase();
			edoc = audb.createDocument();// 'Create an error log in audit trail
											// whenever there is an error.
			edoc.replaceItemValue("Form", "bsuiteerror");
			edoc.computeWithForm(true, true);// set authors
			//Set the status to inactive so that it cannot be opened in edit mode
			edoc.replaceItemValue("Bsuitestatus", "0");
			//Set the database title
			//edoc.replaceItemValue("ModuleTitle", currentDb.getTitle());
			//edoc.replaceItemValue("ModuleName", currentDb.getFileName());
			//set the fiel name fo the database as this audit trail may be used across																			
			// Set the Error Numbers
			edoc.replaceItemValue("errno", 0);// 0 for java error
			edoc.replaceItemValue("errline", errLine);
			edoc.replaceItemValue("errText", errMsg);
			edoc.replaceItemValue("errSource", className);// java class name
			edoc.replaceItemValue("errFunction", methodName);// Java method name
			edoc.replaceItemValue("BsuiteTitle", errMsg + " - " + errLine);
			
			/*
			String strDays = Utility.getBsuiteProfile(currentDb)
					.getItemValueString("AuditExpiry");
			
			if (strDays.equals(""))
			{
				strDays = "30";
			}
			*/
			
			String strDays = "30";
			
			if (!strDays.equals("0"))
			{// if set to 0, there is no Expiry Date
				int intDays = Integer.valueOf(strDays);
				DateTime dt1 = ExtLibUtil.getCurrentSession().createDateTime(
						"Today");
				dt1.adjustDay(intDays);
				edoc.replaceItemValue("ExpiryDate", dt1.getDateOnly());

			}

			edoc.save(true, false);

		} catch (NotesException e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

	}

	/*
	 * If an Exception is a NotesException, this method will extract the Notes
	 * error number and error message.
	 */
	private boolean setErrorLogFields(Throwable ee)
	{
		

		try
		{
			stackTrace = getStackTrace(ee);
			//System.out.print("trace "+stackTrace);
			methodName = getMethodName(stackTrace, 1);
			className = getClassName(stackTrace, 1);
			errLine = getMethodErrorLine(stackTrace, methodName);
			try
			{
				if (ee instanceof NotesException)
				{
					NotesException ne = (NotesException) ee;
					errMsg = ne.text;
				} else
				{
					errMsg = stackTrace.elementAt(0).toString();
				}
			} catch (Exception e)
			{
				errMsg = "";
			}

			return true;
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private Vector getStackTrace(Throwable ee)
	{
		return getStackTrace(ee, 0);
	}

	/*
	 * Get the stack trace of an Exception as a Vector, without the initial
	 * error message, and skipping over a given number of items (as determined
	 * by the skip variable)
	 */
	@SuppressWarnings("unchecked")
	private Vector getStackTrace(Throwable ee, int skip)
	{
		Vector v = new Vector(32);
		try
		{
			StringWriter sw = new StringWriter();
			ee.printStackTrace(new PrintWriter(sw));
			StringTokenizer st = new StringTokenizer(sw.toString(), "\n");
			int count = 0;
			while (st.hasMoreTokens())
			{
				if (skip <= count++)
					v.addElement(st.nextToken().trim());
				else
					st.nextToken();
			}

		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);		}

		return v;
	}

	/*
	 * Attempt to get the name of the method referenced a certain number of
	 * lines (linenum) down the stack trace, assuming the normal format of a
	 * stack trace, such as: java.lang.NumberFormatException: notanumber at
	 * java.lang.Integer.parseInt(Integer.java:335)
	 * JavaAgent.NotesMain(JavaAgent.java:15) ...
	 */
	@SuppressWarnings("unchecked")
	private String getMethodName(Vector trace, int linenum)
	{
		try
		{
			String s = getMethodReference(trace, linenum);
			String ms = s.substring(s.indexOf(" ") + 1);
			ms = ms.substring(0, ms.indexOf("("));
			return ms;
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			return "";
		}
	}
	
	
	/*
	 * Attempt to get the name of the class referenced a certain number of
	 * lines (linenum) down the stack trace, assuming the normal format of a
	 * stack trace, such as: java.lang.NumberFormatException: notanumber at
	 * java.lang.Integer.parseInt(Integer.java:335)
	 * JavaAgent.NotesMain(JavaAgent.java:15) ...
	 */
	@SuppressWarnings("unchecked")
	private String getClassName(Vector trace, int linenum)
	{
		try
		{
			String s = getMethodReference(trace, linenum);
			
			String ms = s.substring(s.indexOf(" ") + 1);
			ms = ms.substring(0, ms.indexOf("("));
			String[] str = ms.split("\\.");
			String cs = str[str.length-2];
			
			return cs;
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			return "";
		}
	}

	/*
	 * Get the line of text referenced a certain number of lines (linenum) down
	 * the stack trace. This corresponds to the weird stackLevel variable I use
	 * in the getBasicLogFields method.
	 */
	@SuppressWarnings("unchecked")
	private String getMethodReference(Vector trace, int linenum)
	{
		
		try
		{
			
			return trace.elementAt(linenum).toString();
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			return "";
		}
	}

	/*
	 * Given the name of a method in a stack trace, this will parse the stack
	 * trace and attempt to extract the line number in that method that was
	 * referenced in the trace, assuming the normal format of a stack trace,
	 * such as: java.lang.NumberFormatException: notanumber at
	 * java.lang.Integer.parseInt(Integer.java:335)
	 * JavaAgent.NotesMain(JavaAgent.java:15) ...
	 */
	@SuppressWarnings("unchecked")
	private int getMethodErrorLine(Vector trace, String methodName)
	{
		try
		{
			String s = getMethodReference(trace, methodName);
			String ls = s.substring(s.lastIndexOf(":") + 1);
			ls = ls.substring(0, ls.indexOf(")"));
			return Integer.parseInt(ls);
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			return 0;
		}
	}

	
	/**
	 *[Find the first line of text in a stack trace that contains a given method]
	 *@param trace stack trace as vector
	 *@param methodName method name
	 *@return
	 */
	@SuppressWarnings("unchecked")
	private String getMethodReference(Vector trace, String methodName)
	{
		try
		{
			if (methodName.length() == 0)
				return "";

			int i = 0;
			for (i = 0; i < trace.size(); i++)
			{
				if (trace.elementAt(i).toString().indexOf(methodName) >= 0)
					break;
			}

			return trace.elementAt(i).toString();
		} catch (Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
			return "";
		}
	}
	
	
	/**
	 *[This function returns the Error Message that is defined in Error Codes(errcodes.nsf) Module for the error code 'strcode'. If strcode is not defined in Error Codes Module then this function just returns the string 'BSUITE Message'.]
	 *@param strcode as String - Ex: 'B001', 'B011' etc can be passed as parameter string.
	 *@return Error Message that is defined in error repository.
	 */
	public static String getMessageString(String strCode) 
	{
	
			String ret = "";
			
			try 
			{
				
					String returnValue = "BSUITE Message"; // 'if the database is not available or if there is no match, let this retururn BSUITE message
					Database edb;
					//edb = Utility.getDatabase("errcodes.nsf");
					edb = Utility.getDatabase("ErrorCodes");
					
					if (edb.isOpen()) // 'check if the database is open
					{
						View view;
						Document doc;
						view = edb.getView("errorprofile");
						if(view == null)
						{
							view = edb.getView("errorlookup");  //'changed in rearchitecture
							if (view == null)
							{
								return returnValue ;
							}
						}
						
							
						
						
						doc = view.getDocumentByKey(strCode, true);
						if (!(doc == null))
						{
							ret =  doc.getItemValueString("errText");
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
		
			return ret;
	} 
}
