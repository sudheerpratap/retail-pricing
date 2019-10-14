package com.target.retail.pricing.dao;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.target.retail.pricing.domain.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PricingDao {

    @Autowired
    CassandraTemplate cassandraTemplate;

    public Product getProductDetails(Long id){
        Product product = null;
        if(cassandraTemplate != null){
            Select select = QueryBuilder.select().from("product_details").where(QueryBuilder.eq("product_id", id)).allowFiltering();
            product = cassandraTemplate.selectOne(select, Product.class);
        }

        return product;
    }

    public void updateProductDetails(Product product) {
        if(cassandraTemplate != null) {
            cassandraTemplate.update(product);
        }
    }
}
