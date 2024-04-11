package com.ecommerce.site.admin.service;

import com.ecommerce.site.admin.exception.ShippingRateNotFoundException;
import com.ecommerce.site.admin.model.entity.Product;
import com.ecommerce.site.admin.model.entity.ShippingRate;
import com.ecommerce.site.admin.repository.ProductRepository;
import com.ecommerce.site.admin.repository.ShippingRateRepository;
import com.ecommerce.site.admin.service.ShippingRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTests {

	@MockBean
	private ShippingRateRepository rateRepository;

	@MockBean
	private ProductRepository productRepository;
	
	@InjectMocks
	private ShippingRateService shipService;
	
	@Test
	public void testCalculateShippingCost_NoRateFound() {
		Integer productId = 1;
		Integer countryId = 234;
		String state = "ABCDE";
		
		Mockito.when(rateRepository.findByCountryAndState(countryId, state)).thenReturn(null);
		
		assertThrows(ShippingRateNotFoundException.class, () -> shipService.calculateShippingCost(productId, countryId, state));
	}
	
	@Test
	public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
		Integer productId = 1;
		Integer countryId = 234;
		String state = "New York";
		
		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(10);
		
		Mockito.when(rateRepository.findByCountryAndState(countryId, state)).thenReturn(shippingRate);
		
		Product product = new Product();
		product.setWeight(5);
		product.setWidth(4);
		product.setHeight(3);
		product.setLength(8);		
		
		Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
	
		float shippingCost = shipService.calculateShippingCost(productId, countryId, state);
		
		assertEquals(50, shippingCost);
	}
}
