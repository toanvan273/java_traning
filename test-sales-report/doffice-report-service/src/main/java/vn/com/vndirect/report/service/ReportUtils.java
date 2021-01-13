package vn.com.vndirect.report.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.homedirect.common.util.CalendarUtils;

public class ReportUtils {

	public static final String DATE_SHORT_FORMAT = "dd/MM/yyyy";
	public static final String DATE_LONG_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final int CONVERT_BEGIN_DATE_TYPE = 1;
	public static final int CONVERT_END_DATE_TYPE = 2;

	public static Date convertDate(String textDate, String format, int type) throws ParseException {
		if(StringUtils.isEmpty(textDate)) return null;

		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date dt = dateFormat.parse(textDate);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		if(CONVERT_BEGIN_DATE_TYPE == type) {
			CalendarUtils.toBeginDate(calendar);
		} else {
			CalendarUtils.toEndDate(calendar);
		}
		
		dt = calendar.getTime();
		return dt;
	}

	public static boolean isSameDay(Date date, Date otherDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		Calendar otherCalendar = Calendar.getInstance();
		otherCalendar.setTime(otherDate);
		/*boolean different = (calendar.get(Calendar.DATE) != otherCalendar.get(Calendar.DATE)) 
				|| (calendar.get(Calendar.MONTH) != otherCalendar.get(Calendar.MONTH))
				|| (calendar.get(Calendar.YEAR) != otherCalendar.get(Calendar.YEAR));
		return !different;*/
		return (calendar.get(Calendar.DATE) == otherCalendar.get(Calendar.DATE)) 
				&& (calendar.get(Calendar.MONTH) == otherCalendar.get(Calendar.MONTH))
				&& (calendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR));
		
	}
	
	public static String nvl(Object object, String defaultValue) {
		if(object == null) return defaultValue;
		
		return String.valueOf(object);
	}

}
