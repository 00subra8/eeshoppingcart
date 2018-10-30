package com.ee.eval.dao;

import com.ee.eval.exception.EESCDaoException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class EESCDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<String> getAllAvailableProducts() {
        try {
            return jdbcTemplate.queryForList("SELECT NAME FROM PRODUCT", String.class);
        } catch (DataAccessException dae) {
            throw new EESCDaoException("Unable to retrieve product information from DB");
        }
    }

    public Double getPrice(String productName) {
        if (StringUtils.isBlank(productName)) {
            return 0.00;
        }
        try {
            return jdbcTemplate.queryForObject("SELECT PRICE FROM PRODUCT WHERE LOWER(NAME) = ?",
                    new Object[]{StringUtils.lowerCase(productName)}, Double.class);
        } catch (DataAccessException dae) {
            throw new EESCDaoException("Unable to retrieve PRICE from DB");
        }

    }

    public Integer getAvailableQuantity(String productName) {
        try {
            return jdbcTemplate.queryForObject("SELECT AVAILABLE_QUANTITY FROM PRODUCT WHERE NAME = ?",
                    new Object[]{productName}, Integer.class);
        } catch (DataAccessException dae) {
            throw new EESCDaoException("Unable to retrieve available information from DB");
        }
    }
}
