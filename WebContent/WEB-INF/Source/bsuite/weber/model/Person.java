package bsuite.weber.model;

import java.util.Vector;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;

import lotus.domino.Document;

import lotus.domino.Session;
import lotus.domino.View;
import lotus.domino.NotesException;
import lotus.domino.Name;


public class Person {
	
	

	
	private static Database db; //current db
	private String bsuiteuser;//currentusername
	private String tempuser;
	private Name tempname;
	public Person()throws NotesException{
		System.out.println("Person obj");
		/*Session session= NotesFactory.createSession();		
		db= session.getCurrentDatabase();
		
		  */
		
			db=ExtLibUtil.getCurrentDatabase();
			setBsuiteuser(db.getParent().getEffectiveUserName());
			this.setTempname(ExtLibUtil.getCurrentSession().createName(this.getBsuiteuser()));
			
		}
	public Person(String sUser){
		try {
			this.setTempuser(sUser);
			this.setTempname(ExtLibUtil.getCurrentSession().createName(sUser));
		} catch (NotesException e) {
			
			e.printStackTrace();
		}
	}
	
	public String getTempuser() {
		return tempuser;
	}

	public void setTempuser(String tempuser) {
		this.tempuser = tempuser;
	}

	public Name getTempname() {
		return tempname;
	}
	public void setTempname(Name tempname) {
		this.tempname = tempname;
	}
	
	public  String getBsuiteuser() {
		return bsuiteuser;
	}
	public  void setBsuiteuser(String bsuiteuser) {
		this.bsuiteuser = bsuiteuser;
	}
	
		
		 

		public String getFormattedName(int ctype) throws NotesException{
			try{
			
			switch(ctype)
			{
			case 1://
				return this.getTempname().getAbbreviated();
			case 2://
				return this.getTempname().getCommon();
			case 3: //
				return this.getTempname().getCanonical();
			
			}
			
			}catch(Exception e){e.printStackTrace();}return null;
		}

	@SuppressWarnings("unchecked")
		public Vector getFormattedNames(Vector varValues, int ctype)throws NotesException{
			Vector names=new Vector();
			try {
				if (varValues.equals(null)){
					return null;
				}
				
				for ( Object x : varValues) {
					Name sname = ExtLibUtil.getCurrentSession().createName(x.toString());
					
					String strName="";
					switch(ctype)
					{
					case 1:	strName= sname.getAbbreviated();
							break;
					case 2:	strName= sname.getCommon();
							break;
					case 3: strName= sname.getCanonical();
							break;
					
					}
					
					names.addElement(strName);
				}
				
				return names;
			} catch (NotesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
								
			return names;
			}
	
		
		
		public Document getUserProfile() throws NotesException{
			Document uprofile = null;
			try {
				
				uprofile=db.getProfileDocument("userprofile", bsuiteuser);
				if(uprofile!=null){
					return uprofile;
				}
			} catch (NotesException e) {
				System.out.println(e.getMessage()+ " " +e.id + " " + e.text);
			}finally{}
			return uprofile;
		
		}

		public Database getUserMaildb(){
			System.out.println("gettting user maildb");
			Database usdb=null;
			try {Session session=Person.db.getParent();
				Database nadb= session.getDatabase("", "names.nsf");
				View uview=nadb.getView("($Users)");
				Document user1=  uview.getDocumentByKey(this.bsuiteuser);
				if(user1!=null){
					String mailserver= user1.getItemValueString("mailserver");
					String mailfile= user1.getItemValueString("mailfile");
					usdb= session.getDatabase(mailserver, mailfile);
					if(usdb==null){System.out.println("no db");} 
					return usdb;
				}
				
			} catch (NotesException e) {
				
				e.printStackTrace();
			}
			
			return usdb;
		}	
		
}
