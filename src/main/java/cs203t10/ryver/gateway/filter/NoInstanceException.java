package cs203t10.ryver.gateway.filter;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GATEWAY_TIMEOUT, reason = "Service instance not found")
public class NoInstanceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public NoInstanceException(String serviceName) {
        super(String.format("Instance %s not found", serviceName));
    }
}
