package com.flab.sooldama.global.auth;

import com.flab.sooldama.global.exception.AuthenticationFailException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

	private final AuthService authService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) throws Exception {
		HttpSession session = request.getSession();

		if (session.getAttribute(authService.getAuthSessionKey()) != null) {
			throw new AuthenticationFailException("이미 로그인한 사용자입니다");
		}

		return true;
	}
}
