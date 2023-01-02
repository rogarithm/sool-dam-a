package com.flab.sooldama.global.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

	private AuthService authService;

	@BeforeEach
	public void setUp() {
		authService = new AuthService();
	}

	@Test
	public void testGetAuthSessionKey() {
		assertThat(authService.getAuthSessionKey()).isEqualTo("USER_EMAIL");
	}

}