package bsuite.model;

import java.util.Map;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.NotesException;

import bsuite.err.ErrorHandler;
import bsuite.security.UserAccess;
import bsuite.utility.SessionContext;
import bsuite.utility.Utility;

import bsuite.utility.Utility;

public class Extranet {
	
	
	/**
	 *[Used to generate the extranet url for the selected document]
	 *@param true to create, false to remove
	 */
	public static void generateURL()
	{
		Document currentDoc = SessionContext.getDocumentToProcess();
		boolean extranet = processExtranetURL(currentDoc);
		boolean val = Boolean.valueOf(SessionContext.getViewScope().get(
				"generateURL").toString());

		if (extranet == true)
		{
			generateExtranetURL(val);

		}

	}

	/**
	 *[Creates or removes extranet url]
	 *@param true to create, false to remove
	 */
	private static void generateExtranetURL(boolean val)
	{

		System.out.print(" inside generateExtranetURL boolean");

		Map viewScope = SessionContext.getViewScope();
		String strDomain = Utility.getFQDN();
		int level = Utility.getACLLevel();
		Database currentdb = Utility.getCurrentDatabase();
		Document currentdoc = SessionContext.getDocumentToProcess();
		try
		{
			String strURL;
			if (val)
			{
				System.out.print(" inside generateExtranetURL boolean true");
				strURL = getExtranetURL(currentdoc, strDomain, level, currentdb
						.getFileName());
				System.out.print(" inside generateExtranetURL boolean doc"+ currentdoc);
				System.out.print(" inside generateExtranetURL boolean url"+ strURL);
				
				if (currentdoc.hasItem("xtrastatus"))
				{
					currentdoc.replaceItemValue("xtrastatus", "1");
					System.out.print(" if1");
				}
				System.out.print(" before if2"+ viewScope.get("msgfor"));
				
				if (viewScope.get("msgfor")!=null && viewScope.get("msgfor").toString().equals("doc"))
				{
					
					System.out.print(" inside if2");
					viewScope
							.put("errsmsDoc",
									"You have successfully generated URL for this document.");
					viewScope.put("errvalDoc", "true");
					
					System.out.print(" if2");
				} else
				{
					viewScope
							.put("errsms",
									"You have successfully generated URL for this document.");
					viewScope.put("errval", "true");
					System.out.print(" if2 else");
				}
			} else
			{
				System.out.print(" inside generateExtranetURL boolean false");
				strURL = "";
				if (viewScope.get("msgfor").equals("doc"))
				{
					viewScope.put("errsmsDoc", "The URL has been removed.");
					viewScope.put("errvalDoc", "true");
				} else
				{
					viewScope.put("errsms", "The URL has been removed.");
					viewScope.put("errval", "true");
				}

			}

			currentdoc.replaceItemValue("ExtranetURL", strURL);
			currentdoc.save(true, false);
			System.out.print(" inside generateExtranetURL boolean end of try");
		} catch (NotesException e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		} catch (Exception e)
		{

			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}
	}

	/**
	 *[Generates the extranet url string]
	 *@param doc, document to process
	 *@param strDomain, the domain name
	 *@param intLevel, Acl access level
	 *@param ModuleName, database name
	 *@return, string extranet url
	 */
	private static String getExtranetURL(Document doc, String strDomain,
			Integer intLevel, String ModuleName)
	{
		System.out.print(" inside getExtranetURL");
		try
		{
			String strURL = "";
			Database currentDb = Utility.getCurrentDatabase();
			String strPath = Utility.getBsuitePath(currentDb);
			String strDataPath;
			String temp;
			if (currentDb.getServer().equals(""))
			{
				strDataPath = Utility.getCurrentSession()
						.getEnvironmentString("Directory", true);
				strPath = strPath.substring(strPath.indexOf(strDataPath),
						strPath.length() - 1);
			}

			temp = strDomain.substring(strDomain.length() - 2);

			if (temp.equals("\\") | temp.equals("/"))
			{
				strDomain = strDomain.substring(0, strDomain.length() - 2);
			}
			temp = strPath.substring(0, 1);
			if (temp.equals("\\") | temp.equals("/"))
			{
				strPath = strPath.substring(1, strPath.length() - 1);
			}
			if (strPath.contains("\\"))
			{
				strPath = Utility.replaceChar(strPath, "\\", "/");
			}

			// IF ANONYMOUS has Reader access or more NO READERS TO THE DOCUMENT
			System.out.println("strDomain "+strDomain+" strPath "+strPath+" moduleName "+ModuleName);
			if (doc.getItemValueString("ReaderRole").equals("")
					&& doc.getItemValueString("ReaderEmployees").equals("")
					&& doc.getItemValueString("Supervisors").equals(""))
			{// OPEN
				// FOR
				// EVERYONE
				System.out.println("0");
				if (intLevel > 1)
				{// CHECK IF ANONYMOUS ENTRY IS AVAILABLE AND
					// GET
					// HIS ACCESS
					//TODO this block doesnt get executed if there is no Anonymous entry in the database, hence it strUrl is returning "" relook into this
					System.out.println("1");
					if (doc.hasItem("CommonUNID"))
					{
						System.out.println("2");
						strURL = strDomain + "/" + strPath + ModuleName
								+ "/00/" + doc.getItemValueString("CommonUNID")
								+ "?OpenDocument";
					} else
					{
						System.out.println("3");
						strURL = strDomain + "/" + strPath + ModuleName
								+ "/00/" + doc.getUniversalID()
								+ "?OpenDocument";

					}
				}

			} else
			{
				System.out.println("4");
				if (doc.hasItem("CommonUNID"))
				{
					System.out.println("5");
					strURL = strDomain + "/" + strPath + ModuleName + "/00/"
							+ doc.getItemValueString("CommonUNID")
							+ "?OpenDocument";
				} else
				{
					System.out.println("6");
					strURL = strDomain + "/" + strPath + ModuleName + "/00/"
							+ doc.getUniversalID() + "?OpenDocument";

				}

			}

			System.out.print(" inside getExtranetURL before return"+strURL);
			return strURL;
		} catch (NotesException e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);			
		}

		catch (Exception e1)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e1);			
		}

		return "";

	}

	/**
	 *[To set appropriate view scope variables in generating url request]
	 *@param currentDoc document to process
	 *@return, true if no errors
	 */
	private static boolean processExtranetURL(Document currentDoc)
	{
		System.out.print("inside processExtranetURL");
		boolean ret = true;
		Map viewScope = SessionContext.getViewScope();
		try
		{

			if (UserAccess.checkAccess(currentDoc, Utility
					.getCurrentSession().getEffectiveUserName()))
			{
				Database mbdb = Utility.getDatabase("admntool.nsf");
				if (mbdb.isOpen())
				{

					Document mprofile = Utility.getBsuiteProfile(mbdb);
					if (mprofile == null)
					{
						viewScope
								.put("errsms",
										"Please create BSUITE Setup profile in Manage BSUITE");
						viewScope.put("errval", "true");
						ret = false;
					}

				} else
				{

					viewScope
							.put(
									"errsms",
									"Please contact your Administrator because the following Module is not available :  admntool.nsf");
					viewScope.put("errval", "true");
					ret = false;
				}
			} else
			{

				viewScope.put("errsms",
						"You do not have privileges to Edit this document");
				viewScope.put("errval", "true");
				ret = false;
			}

		} catch (NotesException e)
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
	
	
	

	public static void initGenerateURL() {

	
		System.out.print("inside initGenerateURL");
			Document currentDoc = SessionContext.getDocumentToProcess();
			boolean extranet = processExtranetURL(currentDoc);
			

			if (extranet == true) 
			{
				generateExtranetURL(currentDoc);

			}
			
			
	
		

	}
	
	
	
	
	private static void generateExtranetURL(Document currentdoc)
	{
		System.out.print("inside generateExtranetURL");
		try 
		{
			
			String url = currentdoc.getItemValueString("ExtranetURL");
			Map viewScope = SessionContext.getViewScope();
			
			if (url.equals(null) || url.equals("")) 
			{
				generateExtranetURL(true);

			} 
			else
			{
				
				
				if (viewScope.get("msgfor").equals("doc")) 
				{
					viewScope
							.put(
									"errsmsDoc",
									"This document has the URL generated already.  Would you like to overwrite?  Click YES to overwrite, NO to erase the URL and CANCEL to exit without changes.");
					viewScope.put("errvalDoc", "prompt");
				} 
				else 
				{
					viewScope
							.put(
									"errsms",
									"This document has the URL generated already.  Would you like to overwrite?  Click YES to overwrite, NO to erase the URL and CANCEL to exit without changes.");
					viewScope.put("errval", "prompt");
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

	}
	
	

}