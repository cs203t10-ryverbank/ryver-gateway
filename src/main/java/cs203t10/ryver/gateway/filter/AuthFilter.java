package cs203t10.ryver.gateway.filter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static cs203t10.ryver.gateway.filter.SecurityConstants.AUTH_HEADER_KEY;
import static cs203t10.ryver.gateway.filter.SecurityConstants.BASIC_PREFIX;

@EnableDiscoveryClient
public class AuthFilter extends ZuulFilter {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String path = request.getServletPath();
        // Login route should not be filtered.
        if (path.startsWith("/login")) {
            return false;
        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String authHeader = request.getHeader(AUTH_HEADER_KEY);

        // If the request uses Basic authentication, generate and append a JWT
        // using the ryver-auth service.
        if (authHeader != null && authHeader.startsWith(BASIC_PREFIX)) {
            String jwtToken = getJWTBearerToken(authHeader);
            requestContext.addZuulRequestHeader(AUTH_HEADER_KEY, jwtToken);
        }

        return null;
    }

    private String getJWTBearerToken(String basicAuthHeader) {
        // Find an instance of the auth service.
        List<ServiceInstance> instances = discoveryClient.getInstances("ryver-auth");
        String url = instances.get(0).getUri().toString();
        String loginUrl = url + "login";

        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER_KEY, basicAuthHeader);
        HttpEntity<String> request = new HttpEntity<>("", headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);
            return response.getHeaders().getFirst(AUTH_HEADER_KEY);
        } catch (RestClientException e) {
            return "";
        }
    }

}
