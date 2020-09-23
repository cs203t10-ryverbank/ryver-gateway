package cs203t10.ryver.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class RyverBankGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RyverBankGatewayApplication.class, args);
	}

}
