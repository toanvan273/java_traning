package vn.com.vndirect.report.pms.utils;

public class Constants {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String MATCH_QUANTITY = "matchQuantity";
    public static final String REAL_TRADING_FEE = "realTradingFee";
    public static final String ACCTNO = "acctNo";
    public static final String CERTIFICATE = "CCPS";

    public enum JOB_CODE {
        IMPORTING_DATA, CALCULATING_REVENUE, CHANGING_STATUS_TO_CALCULATE_COMMISSION, CALCULATING_COMMISSION;

        public static String convertName(JOB_CODE type) {
            switch (type) {
                case IMPORTING_DATA:
                    return "Import dữ liệu từ DW lên Solr";
                case CALCULATING_REVENUE:
                    return "Tính toán dữ liệu doanh thu";
                case CHANGING_STATUS_TO_CALCULATE_COMMISSION:
                    return "Đổi trạng thái các kỳ để tính toán hoa hồng";
                case CALCULATING_COMMISSION:
                    return "Tính toán dữ liệu hoa hồng";
                default: return "";
            }
        }
    }
}
