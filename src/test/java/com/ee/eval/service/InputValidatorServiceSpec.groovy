package com.ee.eval.service

import com.ee.eval.dao.EESCDao
import com.ee.eval.exception.EESCInputException
import com.ee.eval.model.CartItem
import org.slf4j.Logger
import spock.lang.Specification

class InputValidatorServiceSpec extends Specification {
    private InputValidatorService unit

    void setup() {
        unit = new InputValidatorService()
        unit.logger = Mock(Logger)
        unit.eescDao = Mock(EESCDao)
    }

    def "Try to ascertain product presence and availability with no cart item"() {
        when:
        unit.isProductPresentAndAvailable(null)

        then:
        1 * unit.logger.error("No Cart item received to check availability")
        thrown(EESCInputException)
    }

    def "Product item in cart is not present"() {
        given:
        CartItem cartItem = new CartItem()
        cartItem.productName = "invalid product"
        unit.eescDao.getAllAvailableProducts() >> Collections.emptyList()

        expect:
        !unit.isProductPresentAndAvailable(cartItem)
    }

    def "Product item in cart is present but requested quantity more than available"() {
        given:
        CartItem cartItem = new CartItem()
        def validProduct = "valid product"
        cartItem.productName = validProduct
        cartItem.quantity = 50
        unit.eescDao.getAllAvailableProducts() >> Collections.singletonList(validProduct)
        unit.eescDao.getAvailableQuantity(validProduct) >> 0

        expect:
        !unit.isProductPresentAndAvailable(cartItem)
    }

    def "Product item in cart is present but requested quantity less than available"() {
        given:
        CartItem cartItem = new CartItem()
        def validProduct = "valid product"
        cartItem.productName = validProduct
        cartItem.quantity = 5
        unit.eescDao.getAllAvailableProducts() >> Collections.singletonList(validProduct)
        unit.eescDao.getAvailableQuantity(validProduct) >> 50

        expect:
        unit.isProductPresentAndAvailable(cartItem)
    }

}
