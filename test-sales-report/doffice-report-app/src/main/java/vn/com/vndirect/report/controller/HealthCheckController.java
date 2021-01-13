package vn.com.vndirect.report.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.com.vndirect.report.response.HealthCheckerReponse;
import vn.com.vndirect.report.response.HealthData;

@RestController
public class HealthCheckController {
	
	private final static Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

//    private final DateFormat REVISION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @GetMapping("/health")
    public ResponseEntity<HealthCheckerReponse> health() {
        try {
            List<HealthData> healthDatas = new ArrayList<>();
            return ResponseEntity.status(HttpStatus.OK).body(null); //reponse
        } catch (Exception e) {
        	logger.error(e.toString(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HealthCheckerReponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), null));
        }
    }

  
}
