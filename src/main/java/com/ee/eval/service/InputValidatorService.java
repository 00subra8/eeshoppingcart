package com.ee.eval.service;

import com.ee.eval.dao.EESCDao;
import com.ee.eval.exception.EESCInputException;
import com.ee.eval.model.CartItem;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class InputValidatorService {

    @Autowired
    private EESCDao eescDao;

    public boolean isProductPresentAndAvailable(CartItem cartItem) {
        if (cartItem == null) {
            throw new EESCInputException("No Cart item received to check availability");
        }
        List<String> allAvailableProducts = eescDao.getAllAvailableProducts();

        Optional<String> matchedProduct = getMatch(cartItem.getProductName(), allAvailableProducts);
        if (matchedProduct.isPresent()) {
            int availableQuantity = eescDao.getAvailableQuantity(matchedProduct.get());
            return availableQuantity >= cartItem.getQuantity();
        }
        return false;
    }

    private Optional<String> getMatch(String matchString, List<String> allValues) {
        return CollectionUtils.emptyIfNull(allValues).stream()
                .filter(StringUtils::isNotBlank)
                .filter(currentPhoneNumber -> StringUtils.equalsIgnoreCase(StringUtils.trim(matchString),
                        StringUtils.trim(currentPhoneNumber)))
                .findFirst();
    }
}
