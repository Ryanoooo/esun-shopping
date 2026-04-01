package com.esun.shopping.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class XssFilter implements Filter {

    private final PolicyFactory policy = Sanitizers.FORMATTING;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(httpRequest, policy);
        chain.doFilter(wrappedRequest, response);
    }
}