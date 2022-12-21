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

	private static final String SESSION_ATTR_KEY_FOR_AUTH = "USER_EMAIL";

	private static final String SESSION_ATTR_VALUE_FOR_AUTH = "test@tester.com";

	private static final Integer DEFAULT_OFFSET = 0;

	private static final Integer DEFAULT_LIMIT = 20;

	@BeforeEach
	public void setUp() {
		this.session = new MockHttpSession();
		this.session.setAttribute(SESSION_ATTR_KEY_FOR_AUTH, SESSION_ATTR_VALUE_FOR_AUTH);
	}

	@Test
	@DisplayName("제품 조회 성공 테스트")
	public void getProductsTest() throws Exception {
		this.mockMvc
			.perform(get("/products")
				.param("offset", DEFAULT_OFFSET.toString())
				.param("limit", DEFAULT_LIMIT.toString())
				.session(this.session))
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("요청 파라미터 값이 유효하지 않으면 제품 조회 실패")
	public void getProductsFailTest() throws Exception {
		Integer INVALID_OFFSET = -1;

		this.mockMvc
			.perform(get("/products")
				.param("offset", INVALID_OFFSET.toString())
				.session(this.session))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("categoryId를 사용하여 categoryId에 알맞는 제품 조회 성공")
	public void getProductsByCategoryIdTest() throws Exception {
		Long VALID_CATEGORY_ID = 1L;

		this.mockMvc
			.perform(get("/products")
				.param("categoryId", VALID_CATEGORY_ID.toString())
				.session(this.session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].productCategoryId").value(1));
	}

    @Test
    @DisplayName("아이디로 제품 조회 성공")
    public void getProductTest() throws Exception {
		Long VALID_PRODUCT_ID = 1L;

        this.mockMvc
			.perform(get("/products/{PRODUCT_ID}", VALID_PRODUCT_ID)
				.session(this.session))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("아이디로 제품 조회 실패")
    public void getProductFailTest() throws Exception {
		Long INVALID_PRODUCT_ID = 1000L;

        this.mockMvc
			.perform(get("/products/{INVALID_PRODUCT_ID}", INVALID_PRODUCT_ID)
				.session(this.session))
			.andExpect(status().isNotFound());
    }
}
