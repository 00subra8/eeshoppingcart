DROP TABLE IF EXISTS SHOPPING_CART;
DROP TABLE IF EXISTS PRODUCT;

DROP SEQUENCE IF EXISTS SEQ_PRODUCT_ID;
DROP SEQUENCE IF EXISTS SEQ_SHOPPING_CART_ID;

CREATE SEQUENCE IF NOT EXISTS SEQ_PRODUCT_ID START WITH 1;

CREATE TABLE IF NOT EXISTS PRODUCT
(
   ID INTEGER NOT NULL,
   NAME VARCHAR(255) NOT NULL,
   AVAILABLE_QUANTITY INTEGER,
   PRICE DOUBLE,
   PRIMARY KEY(ID)
);

CREATE SEQUENCE IF NOT EXISTS SEQ_SHOPPING_CART_ID START WITH 1;

CREATE TABLE IF NOT EXISTS SHOPPING_CART
(
   ID INTEGER NOT NULL,
   PRODUCT_ID INT NOT NULL,
   QUANTITY INT NOT NULL
);

ALTER TABLE SHOPPING_CART ADD FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT(ID);
