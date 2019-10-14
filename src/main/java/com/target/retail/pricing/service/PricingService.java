package com.target.retail.pricing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.target.retail.pricing.dao.PricingDao;
import com.target.retail.pricing.domain.Error;
import com.target.retail.pricing.domain.Price;
import com.target.retail.pricing.domain.ProductResponse;
import com.target.retail.pricing.domain.entity.Product;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PricingService {

    @Autowired
    PricingDao pricingDao;

    @Value("${product.url}")
    String productUrl;

    @Value(("${currency.code}"))
    String currencyCode;


    public ResponseEntity<Object> getProductDetails(Long id) {
        Product product = null;
        ResponseEntity<Object> responseEntity = null;
        Object productResponse = null;
        try {
            product = pricingDao.getProductDetails(id);
            if (product != null) {
                Map<String, Object> productMap = getProductInfo(id);
                productResponse = populateProductDetails(product, productMap);
                responseEntity = new ResponseEntity<>(productResponse, HttpStatus.OK);
            } else {
                responseEntity = new ResponseEntity<>(populateErrorMessage("No product details found for the id:" + id), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.info("Unable to retreive the prodct details for the productId=" + id);
            responseEntity = new ResponseEntity<>(populateErrorMessage("An exception occured while fetching product details: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return responseEntity;
    }

    public ResponseEntity<Object> updateProductDetails(Long id, ProductResponse product) {
        try {
            if (product != null && product.getPrice() != null) {
                Product productEntity = pricingDao.getProductDetails(id);
                if (productEntity != null) {
                    BigDecimal price = new BigDecimal(product.getPrice().getValue());
                    productEntity.setPrice(price);
                    pricingDao.updateProductDetails(productEntity);
                } else {
                    return new ResponseEntity<>(populateErrorMessage("No product details found for the id:" + id), HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            log.error("updateProductDetails an exception occured while saving product details ex={}", e.getMessage());
            return new ResponseEntity<>(populateErrorMessage("An exception occured while saving product details: "+e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Product details updated successfully", HttpStatus.OK);
    }

    public Object populateProductDetails(Product product, Map<String, Object> productMap) {
        ProductResponse productResponse = null;
        if (product != null && productMap != null && productMap.get("product") != null) {
            productResponse = new ProductResponse();
            productResponse.setId(product.getProductId());
            if (((Map<String, Object>) productMap.get("product")).get("item") != null &&
                    ((Map<String, Object>) (((Map<String, Object>) productMap.get("product")).get("item"))).get("product_description") != null) {
                Map<String, Object> prodDescriptionMap = (Map<String, Object>) ((Map<String, Object>) (((Map<String, Object>) productMap.get("product")).get("item"))).get("product_description");
                String title = (String) prodDescriptionMap.get("title");
                productResponse.setName(title);
            }
            Price price = new Price();
            price.setCurrencyCode(currencyCode);
            price.setValue(Double.valueOf(product.getPrice().toString()));
            productResponse.setPrice(price);
        } else {
            log.error("populateProductDetails productInfo={} productMap={}", product, productMap);
            return populateErrorMessage("ProductResponse Details are not available for the productId");
        }
        return productResponse;
    }

    @HystrixCommand(fallbackMethod = "productDetailsFallBack")
    public Map<String, Object> getProductInfo(Long productId) {
        Map<String, Object> productInfo = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<HttpHeaders> entity = new HttpEntity<>(httpHeaders);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> responseEntity = restTemplate.exchange(productUrl, HttpMethod.GET, entity, Map.class, productId);
            if (responseEntity != null) {
                productInfo = responseEntity.getBody();
            }
        } catch (Exception e) {
            log.error("An exception occured while fetching prodcutdetails ={}", e.getMessage());
            productInfo.put("error", populateErrorMessage("ProductResponse details not found for the id:" + productId));
            return productInfo;
        }
        return productInfo;
    }

    public Map<String, Object> productDetailsFallBack() {
        Map<String, Object> productInfo = new HashMap<>();
        productInfo.put("errorMessage", "Product api is currently unavailable");
        return productInfo;
    }


    public Error populateErrorMessage(String errorMessage) {
        Error error = new Error();
        error.setErrorMessage(errorMessage);
        return error;
    }
}
