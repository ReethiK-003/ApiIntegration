package com.apiintegration.core.jwt;

import java.io.IOException;
import javax.servlet.*;
import org.slf4j.MDC;

import com.apiintegration.core.model.User;

public class LogEnhancerFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		User user = ((User) servletRequest.getAttribute("user"));
		if (user != null) {
			MDC.put("user.id", user.getId().toString());
			MDC.put("user.email", user.getUserEmail());
		}

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}