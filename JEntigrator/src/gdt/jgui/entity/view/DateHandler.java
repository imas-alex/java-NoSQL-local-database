package gdt.jgui.entity.view;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

public class DateHandler {
public static Timestamp getTimestamp(String date$,String format$){
	try{
	if(format$==null)
		format$="yyyy-MM-dd";
	ZoneId zoneId = ZoneId.systemDefault();
	 LocalDateTime localDateTime = LocalDateTime.parse(date$);
	//System.out.println("DateHandler:getTimestamp:time="+localDateTime.toString());
	return new java.sql.Timestamp(localDateTime.atZone(zoneId).toEpochSecond());
	}catch(Exception e){
		Logger.getLogger(DateHandler.class.getName()).severe(e.toString());
		return null;
	}
}
public static long getTime(String date$,String format$){
	Timestamp ts=getTimestamp(date$,format$);
	if(ts==null)
		return -1;
	return ts.getTime();
}
public static Timestamp getTimestamp(long time){
	return new Timestamp(time);
}
public static boolean between(String date$,Timestamp begin ,Timestamp  end,String format$){
	Timestamp date=getTimestamp(date$,format$);
	if(date==null||begin==null||end==null)
		return false;
    if(date.before(end)&&date.after(begin))
    	return true;
    return false;
}
}
