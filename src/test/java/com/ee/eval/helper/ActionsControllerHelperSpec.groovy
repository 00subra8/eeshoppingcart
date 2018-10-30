package com.ee.eval.helper

import com.ee.eval.configuration.ApplicationProperties
import com.ee.eval.dao.EESCDao
import com.ee.eval.model.CartItem
import com.ee.eval.model.CartOrder
import spock.lang.Specification
import spock.lang.Unroll

class ActionsControllerHelperSpec extends Specification {
    private ActionsControllerHelper unit

    void setup() {
        unit = new ActionsControllerHelper()
        unit.eescDao = Mock(EESCDao)
        unit.applicationProperties = Mock(ApplicationProperties)
    }

    def "Return an empty cart order if cart item is null"() {
        given:
        CartOrder expectedCartOrder = new CartOrder()

        when:
        CartOrder actualCartOrder = unit.buildCartOrder(null)

        then:
        expectedCartOrder.orderTimeStamp == actualCartOrder.orderTimeStamp
        expectedCartOrder.itemList == actualCartOrder.itemList
        expectedCartOrder.totalPrice == actualCartOrder.totalPrice
        expectedCartOrder.vat == actualCartOrder.vat
    }

    @Unroll("Calculate expected totalPrice: #totalPrice for given cartItemName: #cartItemName and cartItemQuantity: #cartItemQuantity")
    def "Calculate expected totalPrice: #totalPrice for given cartItemName: #cartItemName and cartItemQuantity: #cartItemQuantity"(
            BigDecimal totalPrice, String cartItemName, int cartItemQuantity, double price) {
        given:
        CartItem cartItem = new CartItem()
        cartItem.productName = cartItemName
        cartItem.quantity = cartItemQuantity
        unit.eescDao.getPrice(cartItem.getProductName()) >> price
        unit.applicationProperties.getVatPercentage() >> 0

        when:
        CartOrder cartOrder = unit.buildCartOrder(cartItem)

        then:
        cartOrder != null
        cartOrder.vat == 0
        cartOrder.totalPrice == totalPrice
        cartOrder.orderTimeStamp != null

        where:
        totalPrice | cartItemName | cartItemQuantity | price
        199.95     | 'Dove Soap'  | 5                | 39.99
        399.90     | 'Dove Soap'  | 10               | 39.99
        390.0      | 'Dove Soap'  | 10               | 39.00

    }


}
