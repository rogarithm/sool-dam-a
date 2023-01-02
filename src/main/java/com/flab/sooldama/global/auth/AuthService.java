package com.flab.sooldama.global.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class AuthService {

	private final String authSessionKey = "USER_EMAIL";

}
