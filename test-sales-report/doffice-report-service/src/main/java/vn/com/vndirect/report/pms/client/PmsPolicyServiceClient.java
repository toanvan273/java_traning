package vn.com.vndirect.report.pms.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.com.vndirect.report.pms.model.Policy;
import vn.com.vndirect.report.pms.model.RestResponsePage;

import java.util.ArrayList;
import java.util.List;

@Component
public class PmsPolicyServiceClient {
    private @Autowired RestTemplate restTemplate;

    @Value("${pms.repository.policy.url}")
    private String pmsPolicyUrl;

    public List<Policy> getPolicy() {
        ParameterizedTypeReference<RestResponsePage<Policy>> responseType = new ParameterizedTypeReference<RestResponsePage<Policy>>() {};
        ResponseEntity<RestResponsePage<Policy>> entity = restTemplate.exchange(pmsPolicyUrl + "/internal/policies/active?page=0&size=200", HttpMethod.GET, null, responseType);
        if (entity.getBody() != null) {
            return entity.getBody().getContent();
        }

        return new ArrayList<>();
    }
}
