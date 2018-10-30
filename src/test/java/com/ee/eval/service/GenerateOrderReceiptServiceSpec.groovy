package com.ee.eval.service

import com.ee.eval.exception.EESCInputException
import com.ee.eval.model.CartOrder
import spock.lang.Specification
import spock.lang.Unroll

class GenerateOrderReceiptServiceSpec extends Specification {
    private GenerateOrderReceiptService unit

    void setup() {
        unit = new GenerateOrderReceiptService()
    }

    @Unroll("For invalid cartOrder: #cartOrder an exception is thrown")
    def "Invalid cart order test"(CartOrder cartOrder) {
        when:
        unit.getReceipt(cartOrder)

        then:
        thrown(EESCInputException)

        where:
        cartOrder << [null, new CartOrder()]
    }

}
