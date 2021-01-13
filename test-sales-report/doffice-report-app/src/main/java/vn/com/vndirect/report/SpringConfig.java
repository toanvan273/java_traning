package vn.com.vndirect.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.solr.SolrHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ImportResource("classpath:application-config.xml")
public class SpringConfig implements WebMvcConfigurer {
    
    @Autowired
    private ObjectMapper mapper;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mapper);
        converters.add(converter);
    }

    @Bean
    public SolrHealthIndicator solrHealthIndicator(Environment env) {
    	String baseUrl = env.getProperty("solr-server");//: http://10.200.14.30:1608
        return new SolrHealthIndicator(new HttpSolrClient.Builder().withBaseSolrUrl(baseUrl + "/solr").build());
    }
}