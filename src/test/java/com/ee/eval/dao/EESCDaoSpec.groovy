package com.ee.eval.dao

import com.ee.eval.exception.EESCDaoException
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.SQLException

class EESCDaoSpec extends Specification {
    private EESCDao unit

    void setup() {
        unit = new EESCDao()
        unit.jdbcTemplate = Mock(JdbcTemplate)
    }

    def "get all available product names"() {
        given:
        def productList = ["Dove Soap", "Hamam Soap"]
        unit.jdbcTemplate.queryForList("SELECT NAME FROM PRODUCT", String.class) >> productList

        expect:
        unit.getAllAvailableProducts() == productList
    }

    def "get all available product names - jdbc exception"() {
        given:
        unit.jdbcTemplate.queryForList("SELECT NAME FROM PRODUCT", String.class) >>
                { throw new BadSqlGrammarException("any task", "any sql", new SQLException("any reason")) }

        when:
        unit.getAllAvailableProducts()

        then:
        thrown(EESCDaoException)
    }

    @Unroll("For productName: #productName price is 0.00")
    def "get price for blank product name"(String productName) {
        expect:
        unit.getPrice(productName) == 0.00

        where:
        productName << [null, "", "  "]
    }

    def "get expected price for valid product name"() {
        given:
        double price = 39.99
        def validProduct = "valid Product"
        unit.jdbcTemplate.queryForObject("SELECT PRICE FROM PRODUCT WHERE LOWER(NAME) = ?",
                { validProduct }, Double.class) >> price
        expect:
        unit.getPrice(validProduct) == price

    }

    def "get expected price - JDBC exception"() {
        given:
        def validProduct = "valid Product"
        unit.jdbcTemplate.queryForObject("SELECT PRICE FROM PRODUCT WHERE LOWER(NAME) = ?",
                { validProduct }, Double.class) >>
                { throw new BadSqlGrammarException("any task", "any sql", new SQLException("any reason")) }

        when:
        unit.getPrice(validProduct)

        then:
        thrown(EESCDaoException)
    }

    def "get available quantity"() {
        int expectedQuantityAvailable = 50
        def validProduct = "valid product"
        unit.jdbcTemplate.queryForObject("SELECT AVAILABLE_QUANTITY FROM PRODUCT WHERE NAME = ?",
                { validProduct }, Integer.class) >> expectedQuantityAvailable
        expect:
        unit.getAvailableQuantity() == expectedQuantityAvailable
    }

    def "get available quantity - jdbc Exception"() {
        def validProduct = "valid product"
        unit.jdbcTemplate.queryForObject("SELECT AVAILABLE_QUANTITY FROM PRODUCT WHERE NAME = ?",
                { validProduct }, Integer.class) >>
                { throw new BadSqlGrammarException("any task", "any sql", new SQLException("any reason")) }

        when:
        unit.getAvailableQuantity()

        then:
        thrown(EESCDaoException)
    }

}
