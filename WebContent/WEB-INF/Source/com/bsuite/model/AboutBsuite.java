package com.bsuite.model;


import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.Form;
import lotus.domino.Item;
import lotus.domino.NoteCollection;
import lotus.domino.NotesException;
import lotus.domino.View;

import com.bsuite.err.ErrorHandler;
import com.bsuite.utility.*;
import com.ibm.domino.services.util.JsonWriter;


 /**
  *[Contains business logic related to About bsuite common action]
  *@author VShashikumar
  *@created Oct 12, 2012
 */
public class AboutBsuite 
{
	
	
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
				Database mbdb = Utility.getDatabase("admntool.nsf");	
			
				if (mbdb.isOpen())
				{
					View moduleView=mbdb.getView("moduleprofile");
					Document module=moduleView.getFirstDocument();
					while(module!=null)
					{
						Database tadb =Utility.getDatabase(module.getItemValueString("ModuleName"));	
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
	

}
