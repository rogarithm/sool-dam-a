package com.flab.sooldama.domain.product.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class ProductIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MockHttpSession session;

	private static final String SESSION_KEY = "USER_EMAIL";

	private static final String SESSION_VALUE = "test@tester.com";

	@BeforeEach
	public void setUp() {
		this.session = new MockHttpSession();
		this.session.setAttribute(SESSION_KEY, SESSION_VALUE);
	}

	@Test
	@DisplayName("제품 조회 성공 테스트")
	public void getProductsTest() throws Exception {
		this.mockMvc
			.perform(get("/products")
				.session(this.session))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("offset이 0 이하일 때 제품 조회 실패")
	public void getProductsFailTest() throws Exception {
		this.mockMvc
			.perform(get("/products?offset=-1")
				.session(this.session))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("categoryId를 사용하여 categoryId에 알맞는 제품 조회 성공")
	public void getProductsByCategoryIdTest() throws Exception {
		this.mockMvc
			.perform(get("/products?categoryId=1")
				.session(this.session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productCategoryId").value(1));
	}

    @Test
    @DisplayName("아이디로 제품 조회 성공")
    public void getProductTest() throws Exception {
        this.mockMvc
			.perform(get("/products/1")
				.session(this.session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("아이디로 제품 조회 실패")
    public void getProductFailTest() throws Exception {
        this.mockMvc
			.perform(get("/products/1000")
				.session(this.session))
			.andExpect(status().isNotFound());
    }
}
