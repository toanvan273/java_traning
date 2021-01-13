package vn.com.vndirect.report.pms.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SolrUtils {

    public static String formatSolrDate(LocalDateTime date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_PATTERN);
        return simpleDateFormat.format(DateUtils.localDateTimeToDate(date));
    }

    public static String formatStringQuerySolr(String field, String string) {
        if (string == null || "all".equals(string)) return field + ": *";

        return field + ": " + "\"" + string + "\"";
    }

    public static String formatListStringQuerySolr(String field, String operator, List<String> values) {
        if (values.isEmpty()) {
            return field + ":" + "*";
        }
        StringBuilder query = new StringBuilder();
        switch (operator) {
            case "and": {
                for (String value : values) {
                    query.append(field).append(":").append("\"").append(value).append("\"");
                }
                break;
            }
            case "or": {
                query.append(field).append(":").append("(").append(String.join(" or ", values.stream().collect(Collectors.joining("\", \"", "\"", "\"")))).append(")");
                break;
            }

            default:
                query.append(field).append(": *");
        }

        return query.toString();
    }
}
