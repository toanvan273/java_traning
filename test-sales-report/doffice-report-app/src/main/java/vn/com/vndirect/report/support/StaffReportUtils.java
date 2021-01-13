package vn.com.vndirect.report.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import com.homedirect.common.util.CalendarUtils;

public class StaffReportUtils {
	
	public static final String TOTAL = "total";
	public static final String DATE_SHORT_FORMAT = "dd/MM/yyyy";
	public static final String DATE_LONG_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final int CONVERT_BEGIN_DATE_TYPE = 1;
	public static final int CONVERT_END_DATE_TYPE = 2;
	
	public static final String REPORT_TYPE_STAFF = "REPORT_TYPE_STAFF";
	public static final String REPORT_TYPE_MANAGER = "REPORT_TYPE_MANAGER";
	public static final String REPORT_TYPE_DIRECTOR = "REPORT_TYPE_DIRECTOR";
	
	public static Date convertDate(String textDate, String format, int type) throws ParseException {
		Date dt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		if(!StringUtils.isEmpty(textDate)) {
			dt = dateFormat.parse(textDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			if(CONVERT_BEGIN_DATE_TYPE == type) {
				CalendarUtils.toBeginDate(calendar);
			} else {
				CalendarUtils.toEndDate(calendar);
			}
			dt = calendar.getTime();
		}
		return dt;
	}
	
	public static String createFileNameReport(String prefix, String infoReport, String firstDayText, String lastDayText, boolean isEndperiodReport, boolean isTotal) {
		return createFileNameReport(prefix, infoReport, firstDayText, lastDayText, isEndperiodReport, isTotal, REPORT_TYPE_STAFF);
	}
	
	public static String createFileNameReport(String prefix, String infoReport, String firstDayText, String lastDayText, boolean isEndperiodReport, boolean isTotal, String reportType) {
		if(isEndperiodReport)  firstDayText = "";
		
		StringBuilder fileNameBuilder = new StringBuilder();
		fileNameBuilder.append(prefix).append("_");
		
		String info = getInfo(infoReport, isTotal, reportType, fileNameBuilder);		
		if(!StringUtils.isEmpty(info)) {
			fileNameBuilder.append(info).append("_");
		}
		
		if(!StringUtils.isEmpty(firstDayText)) {
			firstDayText = firstDayText.replace("/", "");
			fileNameBuilder.append(firstDayText).append('_');
		}
		
		if(!StringUtils.isEmpty(lastDayText)) {
			lastDayText = lastDayText.replace("/", "");
			fileNameBuilder.append(lastDayText);
		}

		fileNameBuilder.append(".xls");
		return fileNameBuilder.toString();

	}
	
	private static String getInfo(String infoReport, boolean isTotal, String reportType, StringBuilder fileNameBuilder) {
		if(REPORT_TYPE_STAFF.equals(reportType)) {
			if(StringUtils.isEmpty(infoReport) || isTotal) {
				infoReport = TOTAL;
			}
		} else if (REPORT_TYPE_MANAGER.equals(reportType)) {
			if(isTotal) {
				infoReport = TOTAL;
			}
			
			String[] listInfoReport = infoReport.split(" ");
			if(listInfoReport.length > 1) {
				infoReport = "";
			}
		} else {
			// TODO
		}
		return infoReport;
	}
	
	public static String getHtmlHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("  <title>Staff Report</title>\n");
		sb.append("  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
		sb.append("  <link rel=\"stylesheet\" href=\"/resources/static/bootstrap/css/bootstrap.min.css\" type=\"text/css\" /> \n");
		sb.append("  <link rel=\"stylesheet\" href=\"/resources/css/stylePopup.css\" type=\"text/css\" /> \n");
		sb.append("  <script src=\"/resources/static/jquery/jquery.min.js\" type=\"text/javascript\"></script>\n");
//		sb.append("  <script src=\"/resources/static/bootstrap/js/bootstrap.bundle.min.js\" type=\"text/javascript\"></script>\n");
		sb.append("  <script src=\"/resources/js/staff-report-popup.js\" type=\"text/javascript\"></script>\n");
		sb.append("  <style>\n");
		sb.append("     @media screen and (min-width: 676px) {\n");
		sb.append("       .modal-dialog {\n");
		sb.append("          max-width: 1750px; \n");
		sb.append("          margin: 0px auto;");
		sb.append("       }\n");
		sb.append("     }\n");
		sb.append("  </style>\n");
		sb.append("</head>\n");
		sb.append("<body text=\"#000000\" link=\"#000000\" alink=\"#000000\" vlink=\"#000000\">\n");

		return sb.toString();
	}

	public static String getHtmlFooter(HttpSession session) {
		String totalPage = session.getAttribute("totalPage").toString();
		StringBuilder sb = new StringBuilder();
		sb.append(" <div class=\"modal\" id=\"reportPopupModel\" tabindex=\"-1\" role=\"dialog\" aria-hidden=\"false\"> \n");
		sb.append("  <div class=\"modal-dialog w3-animate-top\">  \n");
		sb.append("    <div class=\"modal-content\"> \n");
		sb.append("       <div class=\"modal-header\"> \n");
		sb.append("          <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button> \n");
		sb.append("       </div>\n");
		sb.append("       <div class=\"modal-text\">Content\n");
		sb.append("       </div>\n");
		sb.append("       <div class=\"wrap-button-download\" >\n" );
		sb.append("         <input value=\"Download\" type=\"button\" id=\"button-download\" onclick=\"queryUrlAndDownloadExcel()\">\n");
		sb.append("        </div>");
		sb.append("        <div class=\"loader\"></div>");
		sb.append("       <div class=\"modal-body\"> \n");
		sb.append("       </div> \n");
		sb.append("    </div> \n");
		sb.append("  </div> \n");
		sb.append(" </div>\n");
		sb.append("<div id=\"myModalError\" class=\"modalError\">\n" +
				"        <div class=\"modal-contentError w3-animate-top\" >\n" +
				"            <div style=\"background-color:white\">\n" +
				"                <span class=\"closeError\" style=\"background-color: white;\">&times;</span>\n" +
				"            </div>\n" +
				"            <div>\n" +
				"                <p style=\"background-color: white;margin:30px 0 0px 0\">Lỗi hệ thống</p>\n" +
				"                <p style=\"margin:5px\">Vui lòng thao tác lại !</p>\n" +
				"            </div>\n" +
				"        </div>\n" +
				"    </div>\n");
		sb.append("<span id=\"number-page\">"+totalPage+"</span>");
		sb.append("<script>\n");
		sb.append("$(document).ready(function() {\n");		
		sb.append("\tinstallPopupListener('td');\n");
		sb.append("});\n");
		sb.append("let span = document.getElementsByClassName(\"close\")[0];\n" +
				" let modal = document.getElementsByClassName(\"modal\")[0];\n" +
				" \n" +
				" span.onclick = () => {\n" +
				" modal.style.display = \"none\";\n" +
				" }\n" +
				" \n");
		sb.append("let spanError = document.getElementsByClassName(\"closeError\")[0];\n" +
				"    let modalError = document.getElementById(\"myModalError\");\n" +
				"\n" +
				"\n" +
				"    spanError.onclick = () => {\n" +
				"        modalError.style.display = \"none\";\n" +
				"    }\n" +
				"\n" +
				"    window.onclick = (event) => {\n" +
				"        if (event.target == modalError) {\n" +
				"            modalError.style.display = \"none\";\n" +
				"        }\n" +
				" if (event.target == modal) {\n" +
				" modal.style.display = \"none\";\n" +
				" }\n" +
				"    }\n");
		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}

}
