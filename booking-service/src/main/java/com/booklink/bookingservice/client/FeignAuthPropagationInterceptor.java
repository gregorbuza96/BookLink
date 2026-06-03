package com.booklink.bookingservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Forwards the gateway-injected identity headers (X-User-*) onto outgoing
 * Feign calls, so downstream services (hotel-service) can authenticate the
 * service-to-service request the same way they authenticate gateway traffic.
 * Without this, internal calls are anonymous and protected endpoints (e.g.
 * PATCH /api/rooms/{id}/status) return 403.
 */
@Component
public class FeignAuthPropagationInterceptor implements RequestInterceptor {

    private static final String[] FORWARDED = { "X-User-Id", "X-Username", "X-User-Role" };

    @Override
    public void apply(RequestTemplate template) {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return;
        }
        HttpServletRequest request = servletAttrs.getRequest();
        for (String header : FORWARDED) {
            String value = request.getHeader(header);
            if (value != null && template.headers().getOrDefault(header, java.util.List.of()).isEmpty()) {
                template.header(header, value);
            }
        }
    }
}
