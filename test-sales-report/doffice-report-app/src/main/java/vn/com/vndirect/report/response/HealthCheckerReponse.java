package vn.com.vndirect.report.response;

import java.util.Arrays;
import java.util.List;

public class HealthCheckerReponse {

    private String status;

    private List<HealthData> data;

    private String version;

    public HealthCheckerReponse() {
    }

    public HealthCheckerReponse(String status, HealthData... data) {
        this.status = status;
        this.data = Arrays.asList(data);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<HealthData> getData() { return data; }
    public void setData(List<HealthData> data) { this.data = data; }

    public String getVersion() {return version;}

    public void setVersion(String version) {this.version = version;}

    @Override
    public String toString() {
        return "HealthCheckerReponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
