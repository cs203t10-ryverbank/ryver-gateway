package cs203t10.ryver.gateway.reset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import cs203t10.ryver.gateway.filter.NoInstanceException;

@RestController
public class ResetController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/reset")
    public String resetServices() {
        reset("ryver-auth");
        reset("ryver-fts");
        reset("ryver-cms");
        reset("ryver-market");
        return "Reset successful";
    }

    private void reset(String serviceName) {
        try {
            String url = getHostUrl(serviceName);
            HttpEntity<String> request = new HttpEntity<>(null);
            restTemplate.postForObject(url + "/reset", request, String.class);
        } catch (NoInstanceException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getHostUrl(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        if (instances.size() == 0) {
            throw new NoInstanceException(serviceName);
        }
        return instances.get(0).getUri().toString();
    }

}

