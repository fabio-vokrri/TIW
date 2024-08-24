package it.polimi.tiw.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionChecker implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(
		ServletRequest servletRequest, 
		ServletResponse servletResponse, 
		FilterChain filterChain
	) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		
		HttpSession session = request.getSession();
		ServletContext context = request.getServletContext();
		
		if(session.isNew() || session.getAttribute("user") == null) {
			String loginPath = context.getContextPath() + "/login.html";
			response.sendRedirect(loginPath);
			return;
		}
		
		filterChain.doFilter(servletRequest, servletResponse);		
	}
	
	@Override
	public void destroy() {
		Filter.super.destroy();
	}

}
