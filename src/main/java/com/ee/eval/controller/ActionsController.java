package com.ee.eval.controller;

import com.ee.eval.configuration.EESCConfiguration;
import com.ee.eval.exception.EESCInputException;
import com.ee.eval.helper.ActionsControllerHelper;
import com.ee.eval.model.CartItem;
import com.ee.eval.model.CartOrder;
import com.ee.eval.service.GenerateOrderReceiptService;
import com.ee.eval.service.InputValidatorService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Import({EESCConfiguration.class})
public class ActionsController {
    private Logger logger = LoggerFactory.getLogger(ActionsController.class);

    @Autowired
    private InputValidatorService inputValidatorService;

    @Autowired
    private GenerateOrderReceiptService generateOrderReceiptService;

    @Autowired
    private ActionsControllerHelper actionsControllerHelper;

    @RequestMapping(method = RequestMethod.POST, value = "/addCartItems")
    public String addCartItems(@RequestBody List<CartItem> cartItems) {

        if (CollectionUtils.isEmpty(cartItems)) {
            logAndThrowEESCInputException("No cart items received");
        }

        cartItems.stream()
                .filter(Objects::nonNull)
                .forEach(this::validateEachCartItem);


        CartOrder cartOrder = actionsControllerHelper.buildCartOrder(cartItems);
        return generateOrderReceiptService.getReceipt(cartOrder);

    }

    private void validateEachCartItem(CartItem cartItem) {
        if (!inputValidatorService.isProductPresentAndAvailable(cartItem)) {
            logAndThrowEESCInputException("Item :" + cartItem.getProductName()
                    + " is not available with us at the moment.");
        }
    }

    private void logAndThrowEESCInputException(String inputExceptionMessage) {
        logger.error(inputExceptionMessage);
        throw new EESCInputException(inputExceptionMessage);
    }

}
