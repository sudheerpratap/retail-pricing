// create keyspace
CREATE KEYSPACE product WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '3'};
// create table
CREATE TABLE PRODUCT_DETAILS(PRICE DECIMAL,PRODUCT_ID BIGINT PRIMARY KEY);