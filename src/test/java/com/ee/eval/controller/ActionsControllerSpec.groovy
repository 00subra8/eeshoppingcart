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

    @Unroll("cartItems: #cartItems will throw an exception")
    def "Try to add a null cart items"(List<CartItem> cartItems) {
        when:
        unit.addCartItems(cartItems)

        then:
        1 * unit.logger.error("No cart items received")
        thrown(EESCInputException)

        where:
        cartItems << [null, new ArrayList<>()]
    }

    def "One Valid CartItem is added to cart order"() {
        given:
        CartItem cartItem = new CartItem()
        cartItem.productName = "Dove Soap"
        cartItem.quantity = 5

        List<CartItem> cartItems = new ArrayList<>()
        cartItems.add(cartItem)

        unit.inputValidatorService.isProductPresentAndAvailable(cartItem) >> true
        CartOrder mockCartOrder = Mock(CartOrder)
        unit.actionsControllerHelper.buildCartOrder(cartItems) >> mockCartOrder
        def receipt = "receipt"
        unit.generateOrderReceiptService.getReceipt(mockCartOrder) >> receipt

        expect:
        unit.addCartItems(cartItems) == receipt
    }

    def "Add one Valid and ane invalid CartItem into cart order"() {
        given:
        CartItem cartItem1 = new CartItem()
        cartItem1.productName = "Dove Soap"
        cartItem1.quantity = 5

        CartItem cartItem2 = new CartItem()
        cartItem2.productName = "Hamam Soap"
        cartItem2.quantity = 3

        List<CartItem> cartItems = new ArrayList<>()
        cartItems.add(cartItem1)
        cartItems.add(cartItem2)

        unit.inputValidatorService.isProductPresentAndAvailable(cartItem1) >> true
        unit.inputValidatorService.isProductPresentAndAvailable(cartItem2) >> false

        when:
        unit.addCartItems(cartItems)

        then:
        1 * unit.logger.error("Item :" + cartItem2.getProductName() + " is not available with us at the moment.")
        thrown(EESCInputException)
    }

    def "Add multiple Valid CartItems into cart order"() {
        given:
        CartItem cartItem1 = new CartItem()
        cartItem1.productName = "Dove Soap"
        cartItem1.quantity = 5

        CartItem cartItem2 = new CartItem()
        cartItem2.productName = "Dove Soap"
        cartItem2.quantity = 3

        List<CartItem> cartItems = new ArrayList<>()
        cartItems.add(cartItem1)
        cartItems.add(cartItem2)

        unit.inputValidatorService.isProductPresentAndAvailable(cartItem1) >> true
        unit.inputValidatorService.isProductPresentAndAvailable(cartItem2) >> true
        CartOrder mockCartOrder = Mock(CartOrder)
        unit.actionsControllerHelper.buildCartOrder(cartItems) >> mockCartOrder
        def receipt = "receipt"
        unit.generateOrderReceiptService.getReceipt(mockCartOrder) >> receipt

        expect:
        unit.addCartItems(cartItems) == receipt
    }


}
