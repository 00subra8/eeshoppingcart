package com.ee.eval.controller;

import com.ee.eval.configuration.EESCConfiguration;
import com.ee.eval.helper.ActionsControllerHelper;
import com.ee.eval.model.CartItem;
import com.ee.eval.model.CartOrder;
import com.ee.eval.service.GenerateOrderReceiptService;
import com.ee.eval.service.InputValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Import({EESCConfiguration.class})
public class ActionsController {

    @Autowired
    private InputValidatorService inputValidatorService;

    @Autowired
    private GenerateOrderReceiptService generateOrderReceiptService;

    @Autowired
    private ActionsControllerHelper actionsControllerHelper;

    @RequestMapping(method = RequestMethod.POST, value = "/addCartItem")
    public String addCartItem(@RequestBody CartItem cartItem) {

        if (cartItem == null) {
            actionsControllerHelper.logAndThrowEESCInputException("No cart item received");
        }
        if (!inputValidatorService.isProductPresentAndAvailable(cartItem)) {
            actionsControllerHelper.logAndThrowEESCInputException("Item :" + cartItem.getProductName()
                    + " is not available with us at the moment.");
        }

        CartOrder cartOrder = actionsControllerHelper.buildCartOrder(cartItem);
        return generateOrderReceiptService.getReceipt(cartOrder);

    }

}
