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

    @Unroll("CartItems: #cartItems will give empty Cart Order")
    def "Return an empty cart order if cart items is null or empty"(List<CartItem> cartItems) {
        given:
        CartOrder expectedCartOrder = new CartOrder()

        when:
        CartOrder actualCartOrder = unit.buildCartOrder(cartItems)

        then:
        expectedCartOrder.orderTimeStamp == actualCartOrder.orderTimeStamp
        expectedCartOrder.itemList == actualCartOrder.itemList
        expectedCartOrder.totalPrice == actualCartOrder.totalPrice
        expectedCartOrder.vat == actualCartOrder.vat

        where:
        cartItems << [null, new ArrayList<>()]
    }

    @Unroll("Calculate expected totalPrice: #totalPrice for given cartItemName1: #cartItemName1 and cartItemQuantity1: #cartItemQuantity1. cartItemName2: #cartItemName2 and cartItemQuantity2: #cartItemQuantity2")
    def "Calculate expected totalPrice: #totalPrice for given cartItemName1: #cartItemName1 and cartItemQuantity1: #cartItemQuantity1"(
            BigDecimal totalPrice, String cartItemName1, int cartItemQuantity1, double price1,
            String cartItemName2, int cartItemQuantity2, double price2, double vat) {
        given:
        CartItem cartItem1 = new CartItem()
        cartItem1.productName = cartItemName1
        cartItem1.quantity = cartItemQuantity1

        CartItem cartItem2 = new CartItem()
        cartItem2.productName = cartItemName2
        cartItem2.quantity = cartItemQuantity2

        List<CartItem> cartItems = new ArrayList<>()
        cartItems.add(cartItem1)
        cartItems.add(cartItem2)

        unit.eescDao.getPrice(cartItem1.getProductName()) >> price1
        unit.eescDao.getPrice(cartItem2.getProductName()) >> price2
        unit.applicationProperties.getVatPercentage() >> 12.5

        when:
        CartOrder cartOrder = unit.buildCartOrder(cartItems)

        then:
        cartOrder != null
        cartOrder.vat.doubleValue() == vat
        cartOrder.totalPrice == totalPrice
        cartOrder.itemList != null
        cartOrder.itemList.size() == 2
        cartOrder.itemList.get(0).productName == cartItemName1
        cartOrder.itemList.get(0).quantity == cartItemQuantity1
        cartOrder.itemList.get(1).productName == cartItemName2
        cartOrder.itemList.get(1).quantity == cartItemQuantity2
        cartOrder.orderTimeStamp != null

        where:
        totalPrice | cartItemName1 | cartItemQuantity1 | price1 | cartItemName2 | cartItemQuantity2 | price2 | vat
        314.96     | 'Dove Soap'   | 2                 | 39.99  | 'Axe Deo'     | 2                 | 99.99  | 35.00
        359.91     | 'Dove Soap'   | 5                 | 39.99  | 'Dove Soap'   | 3                 | 39.99  | 39.99
        449.89     | 'Dove Soap'   | 10                | 39.99  | 'Dove Soap'   | 0                 | 39.99  | 49.99
        702.00     | 'Dove Soap'   | 10                | 39.00  | 'Dove Soap'   | 6                 | 39.00  | 78.00
    }

}
