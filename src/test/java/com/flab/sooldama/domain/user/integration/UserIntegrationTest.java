package com.flab.sooldama.domain.user.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.sooldama.domain.user.dao.UserMapper;
import com.flab.sooldama.domain.user.domain.User;
import com.flab.sooldama.domain.user.dto.request.LoginUserRequest;
import com.flab.sooldama.domain.user.service.UserService;
import com.flab.sooldama.global.auth.AuthService;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private AuthService authService;

	@Autowired
	private MockHttpSession session;

	@BeforeEach
	public void setUp()
		throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		userMapper.deleteAllUsers();
		String validPassword = "q1w2e3!";

		UserService passwordEncryptor = new UserService(userMapper, authService);
		Method method = passwordEncryptor.getClass().getDeclaredMethod("encryptPassword", String.class);
		method.setAccessible(true);

		String encryptedValidPassword = (String) method.invoke(passwordEncryptor, validPassword);

		User user = User.builder()
			.email("joined@fmail.com")
			.password(encryptedValidPassword)
			.name("joined user")
			.phoneNumber("010-1010-1010")
			.nickname("joined")
			.isAdult(true)
			.build();
		userMapper.insertUser(user);
	}

	@Test
	@DisplayName("회원가입되지 않은 이메일로 로그인 시 로그인 실패")
	public void testLoginUserFailWithUnregisteredEmail() throws Exception {
		// 테스트 데이터
		LoginUserRequest invalidRequest = LoginUserRequest.builder()
			.email("yet-joined@fmail.com")
			.password("not-joined-yet!")
			.build();

		String content = objectMapper.writeValueAsString(invalidRequest);
		MockHttpSession session = new MockHttpSession();

		// 실행
		mockMvc.perform(post("/users/login")
				.content(content)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("등록된 사용자이더라도 비밀번호 틀리면 로그인 불가")
	public void testLoginUserFailWhenPasswordNotMatch() throws Exception {
		// 테스트 데이터
		LoginUserRequest invalidRequest = LoginUserRequest.builder()
			.email("joined@fmail.com")
			.password("cant-remember")
			.build();

		String content = objectMapper.writeValueAsString(invalidRequest);
		MockHttpSession session = new MockHttpSession();

		// 실행
		mockMvc.perform(post("/users/login")
				.content(content)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	public void testLoginUser() throws Exception {
		// 테스트 데이터
		LoginUserRequest validRequest = LoginUserRequest.builder()
			.email("joined@fmail.com")
			.password("q1w2e3!")
			.build();

		String content = objectMapper.writeValueAsString(validRequest);
		MockHttpSession session = new MockHttpSession();

		// 실행
		mockMvc.perform(post("/users/login")
				.content(content)
				.session(session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("로그아웃은 로그인으로 생성된 세션이 있을 경우 성공한다")
	public void testLogoutUser() throws Exception {
		// 테스트 데이터
		this.session.setAttribute("USER_EMAIL", "test2@gamil.com");

		// 실행
		mockMvc.perform(post("/users/logout")
				.session(this.session)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("로그인한 상태가 아니라면 로그아웃에 실패한다")
	public void testLogoutUserFailWhenUserDidNotLogin() throws Exception {

		mockMvc.perform(post("/users/logout")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
