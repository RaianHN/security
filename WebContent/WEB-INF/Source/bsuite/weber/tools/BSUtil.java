package bsuite.weber.tools;

import java.util.Map;

import javax.faces.context.FacesContext;

import bsuite.weber.model.Person;

import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.Database;
import lotus.domino.NotesException;
import lotus.domino.Session;

public class BSUtil {

	
	
	public static String getBsuitePath(Database tadb){
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
	
}
