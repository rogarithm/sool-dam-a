package com.flab.sooldama.domain.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.flab.sooldama.domain.product.dao.ProductMapper;
import com.flab.sooldama.domain.product.domain.Product;
import com.flab.sooldama.domain.product.dto.response.ProductResponse;
import com.flab.sooldama.domain.product.exception.ProductNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	/*
	@InjectMocks, @Mock 어노테이션
	@Mock 으로 mock 객체를 생성하여, @InjectMocks 가 붙은 객체에 주입시킵니다.
	@InjectMocks(Service) @Mock(DAO) 를 활용하여 Service 테스트 목객체에 DAO 목객체를 주입시킵니다.
	 */
	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductMapper productMapper;

	private MockHttpSession session;

	private Product product;

	private static final Integer DEFAULT_OFFSET = 0;

	private static final Integer VALID_LIMIT = 5;

	private static final Long DEFAULT_CATEGORY_ID = null;

	private static final Long VALID_CATEGORY_ID = 1L;

	private static final Long VALID_PRODUCT_ID = 1L;

	private static final String SESSION_ATTR_KEY_FOR_AUTH = "USER_EMAIL";

	private static final String SESSION_ATTR_VALUE_FOR_AUTH = "test@tester.com";

	@BeforeEach
	public void setUp() {
		this.product = Product.builder()
			.id(VALID_PRODUCT_ID)
			.productCategoryId(VALID_CATEGORY_ID)
			.name("test")
			.price(1000)
			.imageUrl("test")
			.description("test")
			.abv(1.0)
			.capacity(350)
			.build();

		this.session = new MockHttpSession();
		this.session.setAttribute(SESSION_ATTR_KEY_FOR_AUTH, SESSION_ATTR_VALUE_FOR_AUTH);
	}

	@Test
	@DisplayName("제품이 존재할 때 전체 제품 조회 성공 테스트 - offeet, limit에 알맞는 리스트를 반환")
	public void testGetProducts() {

		// given
		List<Product> products = new ArrayList<>();
		for (int i = DEFAULT_OFFSET; i < VALID_LIMIT; i++) {
			products.add(this.product);
		}

		when(productMapper.selectProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID))
			.thenReturn(products);

		// when
		List<ProductResponse> productsResponse = productService
			.getProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID, this.session);

		// then
		verify(productMapper).selectProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID);
		assertEquals(VALID_LIMIT - DEFAULT_OFFSET, productsResponse.size());
		assertFalse(productsResponse.isEmpty());
	}

	@Test
	@DisplayName("제품이 존재하지 않을 때 전체 제품 조회 성공 테스트 - 비어있는 리스트를 반환")
	public void testGetProductsReturnsEmptyListWhenProductNotExists() {

		// given
		List<Product> products = new ArrayList<>();
		when(productMapper.selectProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID))
			.thenReturn(products);

		// when
		List<ProductResponse> productsResponse = productService
			.getProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID, this.session);

		// then
		verify(productMapper).selectProducts(DEFAULT_OFFSET, VALID_LIMIT, DEFAULT_CATEGORY_ID);
		assertTrue(productsResponse.isEmpty());
	}

	@Test
	@DisplayName("카테고리별 제품이 존재할 때 조회 성공 테스트 - offeet, limit, categoryId에 알맞는 리스트를 반환")
	public void testGetProductsWithCategoryId() {

		// given
		List<Product> products = new ArrayList<>();
		for (int i = DEFAULT_OFFSET; i < VALID_LIMIT; i++) {
			products.add(this.product);
		}

		when(productMapper.selectProducts(DEFAULT_OFFSET, VALID_LIMIT, VALID_CATEGORY_ID))
			.thenReturn(products);

		// when
		List<ProductResponse> productsResponse =
			productService.getProducts(DEFAULT_OFFSET, VALID_LIMIT, VALID_CATEGORY_ID,
				this.session);

		// then
		verify(productMapper).selectProducts(DEFAULT_OFFSET, VALID_LIMIT, VALID_CATEGORY_ID);
		assertEquals(VALID_LIMIT - DEFAULT_OFFSET, productsResponse.size());
		assertFalse(productsResponse.isEmpty());

		for (Product product : products) {
			assertEquals(VALID_CATEGORY_ID, product.getProductCategoryId());
		}
	}

	@Test
	@DisplayName("카테고리별 제품이 존재하지 않을 때 조회 성공 테스트 - 비어있는 리스트를 반환")
	public void testGetProductsReturnsEmptyListWhenNoProductExistsForGivenCategoryId() {

		// given
		List<Product> products = new ArrayList<>();
		when(productMapper.selectProducts(DEFAULT_OFFSET, VALID_LIMIT,
			VALID_CATEGORY_ID)).thenReturn(products);

		// when
		List<ProductResponse> productsResponse =
			productService.getProducts(DEFAULT_OFFSET, VALID_LIMIT, VALID_CATEGORY_ID,
				this.session);

		// then
		verify(productMapper).selectProducts(DEFAULT_OFFSET, VALID_LIMIT, VALID_CATEGORY_ID);
		assertTrue(productsResponse.isEmpty());
	}

	@Test
	@DisplayName("아이디로 제품 조회 성공 테스트")
	public void testGetProductById() {

		// given
		when(productMapper.selectProductById(VALID_PRODUCT_ID))
			.thenReturn(Optional.ofNullable(this.product));

		// when
		ProductResponse productResponse = productService.getProductById(VALID_PRODUCT_ID,
			this.session);

		// then
		verify(productMapper).selectProductById(VALID_PRODUCT_ID);
		assertNotNull(productResponse);
		assertEquals(VALID_PRODUCT_ID, productResponse.getId());
	}

	@Test
	@DisplayName("존재하지 않는 아이디로 제품을 조회할 수 없다")
	public void testGetProductByIdFailWhenIdNotExists() {

		// given
		Long INVALID_PRODUCT_ID = 1000L;
		when(productMapper.selectProductById(INVALID_PRODUCT_ID)).thenReturn(Optional.empty());

		// then
		assertThrows(ProductNotFoundException.class,

			// when
			() -> productService.getProductById(INVALID_PRODUCT_ID, this.session));
	}
}
