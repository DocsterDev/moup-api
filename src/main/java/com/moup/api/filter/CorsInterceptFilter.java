package com.moup.api.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CorsInterceptFilter extends OncePerRequestFilter {

	@Value("${server.publicAddress}")
	private String publicAddress;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", publicAddress);
		response.addHeader("Access-Control-Allow-Headers", "Content-Type, Token, email, pin");
		response.addHeader("Access-Control-Allow-Methods", "POST,GET,PUT,DELETE,OPTIONS");
		response.addHeader("Access-Control-Allow-Credentials", "true");
		filterChain.doFilter(request, response);
	}

}
