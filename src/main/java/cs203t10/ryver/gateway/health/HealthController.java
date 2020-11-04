package cs203t10.ryver.gateway.health;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/services")
    public List<String> getDetectedServices() {
        List<String> serviceNames = List.of(
                "ryver-auth", "ryver-fts", "ryver-cms", "ryver-market", "ryver-recommendations");
        return serviceNames.stream()
            .filter(this::hasInstanceOf)
            .collect(Collectors.toList());
    }

    private boolean hasInstanceOf(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        return !instances.isEmpty();
    }

}

