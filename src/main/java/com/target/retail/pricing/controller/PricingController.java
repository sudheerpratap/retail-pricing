package com.target.retail.pricing.controller;
import com.target.retail.pricing.domain.ProductResponse;
import com.target.retail.pricing.service.PricingService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/retail-pricing", produces ="application/json")
public class PricingController {

    @Autowired
    PricingService pricingService;

    @RequestMapping(value = "/products/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "getProduct", notes = "enter id to get the product details")
    public ResponseEntity<Object> getProduct(@PathVariable("id") Long id){
        log.info("Entering into getProduct");
        return pricingService.getProductDetails(id);
    }


    @RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateProduct(@PathVariable("id") Long id, @RequestBody ProductResponse product)throws Exception {
        log.info("label=RetailController updateProduct started");
        return pricingService.updateProductDetails(id,product);
    }
}
