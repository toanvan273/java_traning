package vn.com.vndirect.report.job;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DWJobStatus {
	
    public static final String DW_JOB_STATUS_FAILED = "failed";
    public static final String DW_JOB_STATUS_SUCCESS = "success";
    public static final String DW_JOB_AS = "account_statement";

    private String jobs;
    private String code;
    private String status;
    private String time;
    private String date;
    private List<DWJobDetail> detail;

	@Override
	public String toString() {
		return "DWJobStatus [jobs=" + jobs + ", code=" + code + ", status=" + status + ", time=" + time + ", date="
				+ date + ", detail=" + detail + "]";
	}


	@Getter
	@Setter
	public static class DWJobDetail {
    	
        public String job_id;
        public String duration;
        public String dag;
        
		@Override
		public String toString() {
			return "DWJobDetail [job_id=" + job_id + ", duration=" + duration + ", dag=" + dag + "]";
		}

        
    }
}