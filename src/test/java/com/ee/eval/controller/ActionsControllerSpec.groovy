package com.ee.eval.controller

import com.ee.eval.exception.EESCInputException
import com.ee.eval.helper.ActionsControllerHelper
import com.ee.eval.model.CartItem
import com.ee.eval.model.CartOrder
import com.ee.eval.service.GenerateOrderReceiptService
import com.ee.eval.service.InputValidatorService
import org.slf4j.Logger
import spock.lang.Specification
import spock.lang.Unroll

class ActionsControllerSpec extends Specification {
    private ActionsController unit

    void setup() {
        unit = new ActionsController()
        unit.inputValidatorService = Mock(InputValidatorService)
        unit.generateOrderReceiptService = Mock(GenerateOrderReceiptService)
        unit.actionsControllerHelper = Mock(ActionsControllerHelper)
        unit.logger = Mock(Logger)
    }

    def "Try to add a null cart item"() {
        when:
        unit.addCartItem(null)

        then:
        1 * unit.logger.error("No cart item received")
        thrown(EESCInputException)
    }

    @Unroll("For productName: #productName with quantity: #quantity exception is thrown")
    def "Throws exception and exits if product not available"(String productName, int quantity) {
        given:
        CartItem cartItem = new CartItem()
        cartItem.productName = productName
        cartItem.quantity = quantity
        unit.inputValidatorService.isProductPresentAndAvailable(cartItem) >> false

        when:
        unit.addCartItem(cartItem)

        then:
        1 * unit.logger.error("Item :" + productName
                + " is not available with us at the moment.")
        thrown(EESCInputException)

        where:
        productName       | quantity
        "valid product"   | 50
        "invalid product" | 5
    }

    def "Valid CartItem is added to cart order"() {
        given:
        CartItem cartItem = new CartItem()
        cartItem.productName = "Dove Soap"
        cartItem.quantity = 5
        unit.inputValidatorService.isProductPresentAndAvailable(cartItem) >> true
        CartOrder mockCartOrder = Mock(CartOrder)
        unit.actionsControllerHelper.buildCartOrder(cartItem) >> mockCartOrder
        def receipt = "receipt"
        unit.generateOrderReceiptService.getReceipt(mockCartOrder) >> receipt

        expect:
        unit.addCartItem(cartItem) == receipt
    }


}
