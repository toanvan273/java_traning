package vn.com.vndirect.report.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HealthCheckSupport {
	
	private final static Logger logger = LoggerFactory.getLogger(HealthCheckSupport.class);

//    private static final DateFormat MESSAGE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Autowired
    private RestTemplate restTemplate;
//	private final static Logger logger = LoggerFactory.getLogger(HealthCheckSupport.class);
//
//    private static final DateFormat MESSAGE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//
//    @Autowired
//    private RestTemplate restTemplate;

}
