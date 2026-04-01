package com.esun.shopping.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.owasp.html.PolicyFactory;

public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final PolicyFactory policy;

    public XssRequestWrapper(HttpServletRequest request, PolicyFactory policy) {
        super(request);
        this.policy = policy;
    }

    @Override
    public String getParameter(String name) {
        return sanitize(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        String[] sanitized = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitized[i] = sanitize(values[i]);
        }
        return sanitized;
    }

    @Override
    public String getHeader(String name) {
        return sanitize(super.getHeader(name));
    }

    private String sanitize(String value) {
        if (value == null) return null;
        return policy.sanitize(value);
    }
}