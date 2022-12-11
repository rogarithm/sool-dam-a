package com.flab.sooldama.domain.product.service;

import com.flab.sooldama.domain.product.dao.ProductMapper;
import com.flab.sooldama.domain.product.domain.Product;
import com.flab.sooldama.domain.product.dto.response.ProductResponse;
import com.flab.sooldama.domain.product.exception.ProductNotFoundException;
import com.flab.sooldama.global.exception.AuthenticationFailException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

	private static final String USER_EMAIL = "USER_EMAIL";

	public List<ProductResponse> getProducts(Integer offset, Integer limit, Long categoryId, HttpSession session) {

		if (session.getAttribute(USER_EMAIL) == null) {
			throw new AuthenticationFailException("로그인이 필요한 서비스입니다");
		}

		List<Product> products = productMapper.selectProducts(offset, limit, categoryId);
		List<ProductResponse> productResponses = new ArrayList<>();

		for (Product product : products) {
			productResponses.add(ProductResponse.of(product));
		}

        return productResponses;
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productMapper.selectProductById(productId)
			.orElseThrow(() -> new ProductNotFoundException("제품이 존재하지 않습니다."));

        return ProductResponse.of(product);
    }
}
