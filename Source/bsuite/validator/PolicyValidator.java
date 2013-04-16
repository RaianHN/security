package bsuite.validator;

import lotus.domino.*;

import bsuite.err.ErrorHandler;

import bsuite.utility.Utility;

 /**
  *Policy specific validations are done in this class
  *@author JPrakash
  *@created Sep 25, 2012
 */
public class PolicyValidator extends Validator{
	
	/**
	 *Checks if there is a document already published with the given title
	 *@param key [category + title] value 
	 *@return true if there are no documents with the same title in published status otherwise false
	 */
	public boolean validateTitle(String key,boolean role,Document doc)
	{
		
		boolean flag=false;
		String compare=null;
		try
		{
			Database db= Utility.getCurrentDatabase();
			View view = db.getView("PublishedPolicyLookup");
			Document ct = view.getDocumentByKey(key,true);
			if((role==true) && doc.getItemValueString("Status").equals("5"))
			{
				compare=doc.getItemValueString("Categories")+doc.getItemValueString("Subject");
				
				if(key.equals(compare))
				{
					flag=true;
				}
				else if(ct==null)
			    {
			    	flag=true;
			    }
			}
			else
			{
			    
			    if(ct!=null)
			    {
			    	flag=false;
			    }
			    else
			    {
			    	flag=true;
			    }
			}
			
		

		}
		catch(Exception e)
		{
			ErrorHandler erh = new ErrorHandler();
			erh.createErrorDocument(e);
		}
		
	
		
		return flag;
	}
}
