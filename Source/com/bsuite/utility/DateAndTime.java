package com.bsuite.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bsuite.err.ErrorHandler;
import com.ibm.xsp.extlib.util.ExtLibUtil;

import lotus.domino.DateTime;
import lotus.domino.NotesException;
import lotus.domino.Session;

/**
 * [This class contains Date and time related method use in inout.]
 * @author SPorwal
 *
 */
public class DateAndTime 
{
	
	/**
	 *[This method compare two time  basic on hours and minute]
	 
	 *@param dt1 as DateTime
	 *@param dt2 as DateTime
	 *@return boolean ,true if both time equal ,false otherwise.
	 */
	public static boolean compareTime(DateTime dt1,DateTime  dt2)
	{
		boolean sameTime =false;
		try
		{
		// both time get in string format
		Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dt1.toString());
		String time1 = new SimpleDateFormat("HH:mm").format(date); 
		date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(dt2.toString());
		String time2 = new SimpleDateFormat("HH:mm").format(date);
		
		 sameTime=time1.equals(time2);
			 
		
		}
		 catch (Exception e) 
		 {
			 ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e);
		}
		
		return sameTime;
	}
 /**
 *[This method return two date difference in hours.]
 
 *@param timeIn as DateTime
 *@param timeOut as DateTime
 *@return double , hours as double
 */
public static double timeDifferenceHours(DateTime timeIn ,DateTime timeOut)
 	{
	 double totalHours =0; ;
	 try
	 {   // calculate time in double
		 double sec = timeOut.timeDifferenceDouble(timeIn);
		 double minutes=sec/60;
		 int hours=(int)minutes/60;
		 double minute =(minutes%60)/100;
		 totalHours = minute+hours;
	
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
	 return totalHours;
 	}
 /**
 *[This method return two date difference in minute]
 
 *@param dt1 as a DateTime
 *@param dt2 as A DateTime
 *@return int , minute as integer
 *@throws NotesException
 */
public static int timeCompareDates(DateTime dt1, DateTime dt2) 
 {    
	 Session session = ExtLibUtil.getCurrentSession();
		int timecomp=0;
		try {
			if (dt1.equals(null) || dt2.equals(null)){
				return 0;
			}
			DateTime dtMy1 = session.createDateTime(dt1.getDateOnly());
			System.out.print("dt1"+dtMy1);
			DateTime dtMy2 = session.createDateTime(dt2.getDateOnly());
			System.out.print("dt2"+dtMy2);
			timecomp = dtMy1.timeDifference(dtMy2)/86400; 

			return timecomp;
		} catch (NotesException e)
		 {  
			
			 ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e);
		 }
		 catch (Exception e) 
		 {
			 ErrorHandler erh = new ErrorHandler();
				erh.createErrorDocument(e);
		}
		
		return timecomp;
	}

	
}
