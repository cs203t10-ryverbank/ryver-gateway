package cs203t10.ryver.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import cs203t10.ryver.gateway.filter.AuthFilter;

@SpringBootApplication
@EnableZuulProxy
public class RyverBankGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RyverBankGatewayApplication.class, args);
	}

    @Bean
    public AuthFilter authFilter() {
        return new AuthFilter();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
