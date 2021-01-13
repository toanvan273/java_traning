package vn.com.vndirect.report.pms.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudHttp2SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import java.util.List;

@Configuration
public class SolrConfiguration {

//    @Value("${solr.url}")
//    private String mode;
//
//    @Value("${solr.hosts}")
//    private List<String> hosts;

    @Bean
    @Autowired
    public SolrClient solrClient(Environment env) {
        String mode = env.getProperty("solr.mode");
        List<String> hosts = env.getProperty("solr.hosts", List.class);
        System.out.println("----------> SOLR MODE: " + mode);
        System.out.println("----------> SOLR HOSTS: " + hosts);

        SolrClient solrClient;
        switch (mode) {
            case "SINGLE":
                solrClient = new Http2SolrClient.Builder(hosts.get(0)).build();
                return solrClient;

            case "CLOUD":
                solrClient = new CloudHttp2SolrClient.Builder(hosts).build();
                return solrClient;

            case "LB":
                solrClient = new LBHttpSolrClient.Builder().withBaseSolrUrls(hosts.toArray(new String[hosts.size()])).build();
                return solrClient;

            default: throw new RuntimeException("No config for solr. Please check");
        }

    }

    @Bean
    @Autowired
    public SolrTemplate solrTemplate(Environment environment) {
        return new SolrTemplate(solrClient(environment));
    }
}
