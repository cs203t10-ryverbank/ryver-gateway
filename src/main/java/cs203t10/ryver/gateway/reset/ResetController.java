package cs203t10.ryver.gateway.reset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cs203t10.ryver.gateway.filter.NoInstanceException;
import static cs203t10.ryver.gateway.filter.SecurityConstants.AUTH_HEADER_KEY;
import static cs203t10.ryver.gateway.filter.SecurityConstants.BEARER_PREFIX;

@RestController
public class ResetController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/reset")
    public String resetServices(@RequestHeader("Authorization") String basicAuthHeader) {
        HttpEntity<String> request = getRequest(basicAuthHeader);
        reset(request, "ryver-auth");
        reset(request, "ryver-fts");
        reset(request, "ryver-cms");
        reset(request, "ryver-market");
        return "Reset successful";
    }

    private HttpEntity<String> getRequest(String basicAuthHeader) {
        HttpHeaders headers = new HttpHeaders();

        String jwt = getJWTBearerToken(basicAuthHeader);

        headers.set(AUTH_HEADER_KEY, BEARER_PREFIX + jwt);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return entity;
    }

    private String getJWTBearerToken(String basicAuthHeader) {
        List<ServiceInstance> authInstances = discoveryClient.getInstances("ryver-auth") ;
        if (authInstances.size() == 0) {
            System.out.println("Cannot authenticate as ryver-auth not found");
            throw new NoInstanceException("ryver-auth");
        }
        String authUrl = authInstances.get(0).getUri().toString();
        String loginUrl = authUrl + "login";

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER_KEY, basicAuthHeader);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
            return response.getHeaders().getFirst(AUTH_HEADER_KEY);
        } catch (RestClientException e) {
            System.out.println("Cannot authenticate as login request failed");
            return "";
        }
    }

    private void reset(HttpEntity<String> request, String serviceName) {
        try {
            String url = getHostUrl(serviceName);
            restTemplate.postForObject(url + "/reset", request, String.class);
        } catch (NoInstanceException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getClass());
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

