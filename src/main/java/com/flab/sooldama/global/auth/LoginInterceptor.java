package com.flab.sooldama.global.auth;

import com.flab.sooldama.domain.user.exception.UserAlreadyLoggedinException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

	private static final String USER_EMAIL = "USER_EMAIL";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) throws Exception {
		HttpSession session = request.getSession();

		if (session.getAttribute(USER_EMAIL) != null) {
			throw new UserAlreadyLoggedinException("이미 로그인한 사용자입니다");
		}

		return true;
	}
}
