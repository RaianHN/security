package bsuite.loadcc;

import lotus.domino.Database;
import lotus.domino.NotesException;

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
