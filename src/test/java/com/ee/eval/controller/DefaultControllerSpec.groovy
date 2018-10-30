package com.ee.eval.controller


import com.ee.eval.configuration.ApplicationProperties
import spock.lang.Specification

class DefaultControllerSpec extends Specification {

    private DefaultController unit
    def welcomeMessage = "Welcome message"

    void setup() {
        unit = new DefaultController()
        unit.applicationProperties = Mock(ApplicationProperties)
        unit.applicationProperties.getWelcome() >> welcomeMessage
    }

    def "display welcome message"() {
        expect:
        unit.displayWelcomeMessage() == welcomeMessage
    }
}
