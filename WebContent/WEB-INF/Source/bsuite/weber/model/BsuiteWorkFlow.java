package bsuite.weber.model;


	import java.util.Collections;
	import java.util.Date;
	import java.util.Map;
	import java.util.Vector;


	import javax.faces.context.FacesContext;

	//import bsuite.validator.CommonValidator;

	import lotus.domino.Database;
	import lotus.domino.DateTime;
	import lotus.domino.Document;
	import lotus.domino.DocumentCollection;
	import lotus.domino.Item;
	import lotus.domino.NotesException;
	import lotus.domino.Session;
	import lotus.domino.View;



import com.ibm.xsp.extlib.util.ExtLibUtil;

	public class BsuiteWorkFlow {

		protected Session session;
		protected Document currentdoc;
		protected Database currentdb;
		protected DocumentCollection currentcollection;
		protected String server_g;
		protected FacesContext context;
		@SuppressWarnings("unchecked")
		protected Map viewScope;
		protected String code_g;
		protected String message_g;
		protected Person currentuser; 
		protected static Document uprofile; //currentuser profile
		protected String bsuitepath; 
		protected Document profile;//currentdb setupprofile
		protected Database favdb;//favdb.nsf
		protected Database erdb;//error.nsf
		protected Database sudb;//subscription.nsf
		
		protected Database audb;
		protected Database helpdb;
		
		public BsuiteWorkFlow(){
			try{
			session = ExtLibUtil.getCurrentSession();
			initworkflow();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public BsuiteWorkFlow(Document doc) throws NotesException{
			this();
			initbsuite(doc);
			
		}
		
		public BsuiteWorkFlow(DocumentCollection dc){
			this();
			initbsuite(dc);
			
		}
		
		@SuppressWarnings("unchecked")
		public void initworkflow() {
			 context = FacesContext.getCurrentInstance();
			viewScope = (Map) context.getApplication().getVariableResolver().resolveVariable(context, "viewScope");
			currentdb= ExtLibUtil.getCurrentDatabase();
			
			 try {
				 bsuitepath=getBsuitePath(currentdb);
				profile=getGlobalProfile(currentdb);
				this.currentuser = new Person();
				uprofile = this.currentuser.getUserProfile();
				if(uprofile==null){
					return ;
				}
				
			} catch (NotesException e) {
				
				e.printStackTrace();
			}
		}
		
		
		
		
		/*Initialise current single doucment to be processed
		 * */
			private void initbsuite(Document doc) throws NotesException {
				setCurrentdoc(doc);

			}
			
			private void initbsuite(DocumentCollection dc) {
				setCurrentcollection(dc);
				
			}
			

			protected Document getCurrentdoc() {
				return currentdoc;
			}
			private void setCurrentdoc(Document currentdoc) {
				this.currentdoc = currentdoc;
			}
			
			public DocumentCollection getCurrentcollection() {
				return currentcollection;
			}
			public void setCurrentcollection(DocumentCollection currentcollection) {
				this.currentcollection = currentcollection;
			}

			
			/*initialise Help databse object.
			 * */
			protected void inithelpdb(){
				try {
					helpdb = session.getDatabase("", bsuitepath+"bsthelps.nsf");
				} catch (NotesException e) {

					e.printStackTrace();
				}
			}
			/*initialise Favorites databse object.
			 * */
			protected void initfavdb() {

				try {
					favdb = session.getDatabase("",bsuitepath+ "favorites.nsf");
				} catch (NotesException e) {

					e.printStackTrace();
				}

			}
			
		/*initialise Subscription databse object.
		 * */
			protected void initsudb() {

				try {
					sudb = session.getDatabase("", bsuitepath+"subscrib.nsf");
				} catch (NotesException e) {

					e.printStackTrace();
				}

			}
			protected void initerdb() {
				
				try {
					erdb = session.getDatabase("", bsuitepath+"errorcodes.nsf");
				} catch (NotesException e) {

					e.printStackTrace();
				}
			}
			
			public String getMessageString(String strCode) 
			{

				String returnValue = "BSUITE Message"; // 'if the database is not available or if there is no match, let this retururn BSUITE message
					
					try 
					{							
							initerdb();
							if (erdb.isOpen()) // 'check if the database is open
							{
								View view;
								Document doc;
								view = erdb.getView("errorprofile");
								if(view == null)
								{
									view = erdb.getView("errorlookup");  //'changed in rearchitecture
									if (view == null)
									{
										return "" ;
									}
								}			
								
								doc = view.getDocumentByKey(strCode, true);
								if (!(doc == null))
								{
									returnValue=doc.getItemValueString("errText");
									return returnValue;
								}
								
							}
					
					}
				
					catch(NotesException e) 
					{
						System.out.println(e.id + " " + e.text);
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}	
				
					return returnValue;
			}
			public void setLastModified(Document doc) {
				
				try 
				{
					Date date = new Date(); 
					DateTime dt1 = session.createDateTime(date);
					dt1.setNow();
					doc.replaceItemValue("DocumentModifier", currentuser.getBsuiteuser());
					doc.replaceItemValue("ModifiedOn", dt1);
				}
				
				catch(NotesException e) 
				{
					System.out.println(e.id + " " + e.text);
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			protected void handleException(NotesException ne) {
				// TODO Auto-generated method stub
				
			}
			
			public boolean checkAccess(Document doc, String canEmployee) 
			{
					try 
					{
						String strEditor;
					
						if (this.checkBsuiteRole("[Maintenance]") ==  false)
						{
								if (this.checkBsuiteRole("[SuperUser]") == false)
							{
							
								strEditor = doc.getItemValueString("BranchHead") ;
								if (strEditor != canEmployee)
								{
									for(Object v1 : doc.getItemValue("AuthorEmployees"))
									{
										if (((String)v1).equalsIgnoreCase(canEmployee))
										{
											return (true);
										}
										else 
										{
											return (false);
										}
									}				
								}
							}			
						}
					
					}
			
					catch(NotesException e) 
					{
						System.out.println(e.id + " " + e.text);
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
				
					return (true);	
			}
			
			@SuppressWarnings("unchecked")
			public boolean checkBsuiteRole(String strRoleTitle) 
			{
				
					try 
					{
					
					
						Vector varRoles;
						varRoles = currentdb.queryAccessRoles(session.getEffectiveUserName());

						for (Object x : varRoles)
						{
							if ( ((String)x).equalsIgnoreCase(strRoleTitle) )
							{
								return (true);
							
							}
						}

						
				
					}
			
					catch(NotesException e) 
					{
						System.out.println(e.id + " " + e.text);
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
			
					return (false);
			}
			
			public String getBsuitePath(Database tadb) throws NotesException{
				String bsuitePath=null;
				try {
					int len=(tadb.getFilePath()).length() - (tadb.getFileName()).length();
					bsuitePath=tadb.getFilePath().substring(0,len);
					return bsuitePath;
				} catch (NotesException e) {
					System.out.println(e.id + " " + e.text);
				}catch (Exception e){
					e.printStackTrace();
				}
				return bsuitePath;
			}
			public Document getGlobalProfile(Database tadb) throws NotesException{
				Document profile=null;
				try {
					View view=tadb.getView("setupprofile");
					profile=view.getFirstDocument();
					if(!(profile.equals(null))){
						return profile;
					}
				} catch (NotesException e) {
					System.out.println(e.id + " " + e.text);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				return profile;
				
			}
			public Document getBsuiteProfile(Database mbdb) throws NotesException{
				Document bprofile=null;
				try {
					if (mbdb.isOpen()){
						View view=mbdb.getView("setupprofile");
						bprofile=view.getFirstDocument();
						if(!(bprofile.equals(null))){
							return bprofile;
						}
					}
				} catch (NotesException e) {
					System.out.println(e.id + " " + e.text);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				return bprofile;
			}
			
			public Document getModuleProfile(Database mbdb,String ModuleName) throws NotesException{
				Document module=null;
				try {
					if (mbdb.isOpen()){
						View view=mbdb.getView("moduleprofile");
						module=view.getDocumentByKey(ModuleName,true);
						if( module != null){
							return module;
						}
					}
				} catch (NotesException e) {
					System.out.println(e.id + " " + e.text);
				} finally{}
					return module;
			}
			
			public Document getOfficeProfile(Database mbdb,String strBranch) throws NotesException{
				Document office=null;
				try {
					View view=mbdb.getView("officeprofile");
					office=view.getDocumentByKey(strBranch,true);
					if( office != null){
						return office;
					}
				} catch (NotesException e) {
					System.out.println(e.id + " " + e.text);
				} finally{}
					return office;
			} 
			
			@SuppressWarnings("unchecked")
			public Document createRequest(String requestType ,String selectionType, Vector selectedID , String searchFormula , int eventType , Vector varNewOption)

			{
				// eventType:- 'IMMEDIATE - 1, Schedule - 0
				
				Document returnValue;
				returnValue = null;
				
				try 
				{
				
				
					Document request;
					Database mbdb; // 'bsuite-p database
					Document mbprofiledoc ; // ' to get lifetime from manage bsuite
					mbdb = session.getDatabase(server_g, bsuitepath + "admntool.nsf");
					
					if (mbdb.isOpen())
					{
						request = mbdb.createDocument();
						request.replaceItemValue("form", "requests") ;
						request.replaceItemValue("ModuleName", currentdb.getFileName());
						request.replaceItemValue("ModuleTitle", currentdb.getTitle());
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
					
						mbprofiledoc = this.getBsuiteProfile(mbdb); //'profile is not used intentionally as we may_
					
						// 'create requests from other modules but the lifetime of request is defined only in Manage Bsuite's setup profile
						
						if (mbprofiledoc.hasItem("LifeTime"))
						{
							strDays = mbprofiledoc.getItemValueString("LifeTime");
						}
						else
						{
							strDays = null;
						}
						
						
						
						
					
						System.out.print(strDays+" :strDays" );
						if (strDays.isEmpty() )
						{
							System.out.print(strDays+" :strDays" );
							strDays = "90";
							System.out.print(strDays+" :strDays" );
						}

						if (!(strDays.equals("0")))	// if set to 0, there is no  Expiry Date
						{
							System.out.print(strDays+" :strDays" );
							int intDays ;
							intDays = Integer.parseInt(strDays);
							Date date = new Date();
							DateTime dt1 = session.createDateTime(date);
							dt1.setNow();
							dt1.adjustDay(intDays);
							DateTime expDate = session.createDateTime(dt1.getDateOnly());
							request.replaceItemValue("ExpiryDate", expDate);
						
						}	
							
						
						request.replaceItemValue("bsuiteStatus", "0");
						request.replaceItemValue("NewOption",varNewOption); 
						//request.replaceItemValue("AuthorEmployees", bsuiteuser);
						
						request.replaceItemValue("DocumentCreator", currentuser.getBsuiteuser());
						request.computeWithForm(true, true) ;
						Item autitem = request.getFirstItem("AuthorEmployees");
				        autitem.setAuthors(true);
				        request.replaceItemValue("AuthorEmployees",currentuser.getBsuiteuser());
						request.replaceItemValue("BsuiteTitle", request.getItemValueString("Request")) ; //'Request field is computed
									
								
						request.save(true, false);
						returnValue = request;
							
							
							
					}
					
				
					
				}

				catch(NotesException e) 
				{
					System.out.println(e.id + " " + e.text);
				}
				
				catch(Exception e)
				{
					e.printStackTrace();
				}

				
				return (returnValue);
				
			}
			
		public void createAuditDocument(String strReferenceID, String strObject, String strAction, String strResult, String strAuthor)
			
			{
					try 
					{
					
							if (audb.isOpen())
							{
								Document audit;
								audit = audb.createDocument(); 
								audit.replaceItemValue("form", "bsuiteaudit");
								audit.replaceItemValue("ObjectName", strObject);
								audit.replaceItemValue("ObjectAction", strAction);
								audit.replaceItemValue("ObjectReference", strReferenceID);
					
								audit.replaceItemValue("ActionResult", strResult); //'This shows the Result of an action
					
				
								//'Hidden Fields	
								Date date = new Date();
								audit.replaceItemValue("Createdon",session.createDateTime(date)); 
								audit.replaceItemValue("Bsuitestatus", "0") ; 	//'set the status to inactive so that it cannot be opened in edit mode
								audit.replaceItemValue("ModuleTitle", currentdb.getTitle()) ;	//'set the database title
								audit.replaceItemValue("ModuleName", currentdb.getFileName()) ;	//'set the file name of the database as this audit trail may be used across
					
								String strDays;
								strDays = profile.getItemValueString("AuditExpiry");
					
					
								if (strDays.equalsIgnoreCase(""))
								{
									strDays = "30";
								}
					
								if (!(strDays.equals("0")))	// if set to 0, there is no  Expiry Date
								{
									int intDays ;
									intDays = Integer.parseInt(strDays);
									DateTime dt1 = session.createDateTime(date);
									dt1.setNow();
									dt1.adjustDay(intDays);
									audit.replaceItemValue("ExpiryDate", dt1.getDateOnly());	
								}
					
					
								Item AuthItem = audit.replaceItemValue("RoleAuthors", "[Maintenance]"); //'Canonical format
								AuthItem.setAuthors(true);
					
					
								Item AuthItem1 = audit.replaceItemValue("AuthorEmployees", strAuthor);	//'Canonical format
								AuthItem1.setAuthors(true);
					
					
								Item NameItem = audit.replaceItemValue("DocumentCreator", strAuthor);	//'Canonical format
								NameItem.setNames(true);
					
					
					
								Item NameItem1 = audit.replaceItemValue("DocumentModifier", strAuthor);	//'Canonical format
								NameItem1.setNames(true); 
					
						
					
								audit.save(true, false);	
						
					
					
							}
				
				
					}
			
					
					catch(NotesException e) 
					{
						System.out.println(e.id + " " + e.text);
					}
					
					catch(Exception e)
					{
						e.printStackTrace();
					}
				
				
			}
			

		@SuppressWarnings("unchecked")
		public Vector getRoleBasedEmployees(String strFileName,String strRoleName) throws NotesException{
			Vector  emp =null;
			try {
				
				
				Database pfdb=session.getDatabase(currentdb.getServer(), bsuitepath+ "profiles.nsf");
				if (pfdb.isOpen()){
					View view=pfdb.getView("roleprofile");
					if (view.equals(null)){
						System.out.print("view is null");
						return null;
					}
					String key = strFileName+strRoleName;
				
					Document pfdoc=view.getDocumentByKey(key , true);
					if (pfdoc!=null){

						emp=new Person().getFormattedNames(pfdoc.getItemValue("Members"),1);
						Collections.sort(emp);
						//call sortArray(emp);
						return emp;
					}
				}
			} catch (NotesException e) {
				System.out.println(e.id + " " + e.text);
			} catch (Exception e){
				e.printStackTrace();
			}
				return emp;
		}

	}


