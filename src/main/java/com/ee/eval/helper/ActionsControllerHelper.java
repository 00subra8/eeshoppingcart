package com.ee.eval.helper;

import com.ee.eval.configuration.ApplicationProperties;
import com.ee.eval.dao.EESCDao;
import com.ee.eval.exception.EESCInputException;
import com.ee.eval.model.CartItem;
import com.ee.eval.model.CartOrder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ActionsControllerHelper {
    private Logger logger = LoggerFactory.getLogger(ActionsControllerHelper.class);

    @Autowired
    private EESCDao eescDao;

    @Autowired
    private ApplicationProperties applicationProperties;


    public CartOrder buildCartOrder(CartItem cartItem) {
        CartOrder cartOrder = new CartOrder();
        cartOrder.setOrderTimeStamp(Timestamp.valueOf(LocalDateTime.now()));

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);

        BigDecimal totalPriceBeforeVat = getTotalPrice(cartItems);

        cartOrder.setVat(getVat(applicationProperties.getVatPercentage(), totalPriceBeforeVat));

        cartOrder.setTotalPrice(totalPriceBeforeVat);

        cartOrder.setItemList(cartItems);

        return cartOrder;
    }

    private BigDecimal getVat(String vatPercentage, BigDecimal totalPriceBeforeVat) {
        if (totalPriceBeforeVat != null && totalPriceBeforeVat.compareTo(BigDecimal.ZERO) > 0) {
            return totalPriceBeforeVat.multiply(BigDecimal.valueOf(Double.valueOf(vatPercentage) / 100));
        }

        return BigDecimal.ZERO;
    }

    private BigDecimal getTotalPrice(List<CartItem> cartItems) {
        AtomicReference<Double> calculatedTotalPrice = new AtomicReference<>(0.00);

        CollectionUtils.emptyIfNull(cartItems).stream()
                .filter(Objects::nonNull)
                .forEach(cartItem -> calculateTotalEffectivePrice(calculatedTotalPrice, cartItem));

        BigDecimal totalPrice = BigDecimal.valueOf(calculatedTotalPrice.get());

        return totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void calculateTotalEffectivePrice(AtomicReference<Double> calculatedTotalPrice, CartItem cartItem) {
        Double price = eescDao.getPrice(cartItem.getProductName());
        double effectivePrice = price * cartItem.getQuantity();
        calculatedTotalPrice.updateAndGet(v -> v + effectivePrice);
    }

    public void logAndThrowEESCInputException(String inputExceptionMessage) {
        logger.error(inputExceptionMessage);
        throw new EESCInputException(inputExceptionMessage);
    }
}