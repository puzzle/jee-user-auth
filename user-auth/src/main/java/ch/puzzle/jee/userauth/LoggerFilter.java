package ch.puzzle.jee.userauth;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class LoggerFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(LoggerFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        // set Mapped Diagnostic Context so all application log events get decorated with it
        String remoteAddr = req.getRemoteAddr();
        if (!StringUtils.isNullOrEmpty(remoteAddr)) {
            MDC.put("ip", remoteAddr);
        }

        // generate a unique transfer ID so we can trace individual requests even if they don't come from
        // the nevis environment
        MDC.put("correlationId", UUID.randomUUID().toString());

        // let the application run
        chain.doFilter(req, resp);

        // log the request itself
        sb.append(req.getMethod()).append(" ").append(req.getRequestURI());
        if (!StringUtils.isNullOrEmpty(req.getQueryString())) {
            sb.append("?");
            sb.append(req.getQueryString());
        }
        sb.append(" ").append(req.getProtocol());

        MDC.put("status", String.valueOf(resp.getStatus()));
        MDC.put("duration", String.valueOf(System.currentTimeMillis() - startTime));

        LOG.info(sb.toString());
        clearMdc();
    }

    private static void clearMdc() {
        MDC.getMap().forEach((k, v) -> MDC.remove(k));
    }
}
