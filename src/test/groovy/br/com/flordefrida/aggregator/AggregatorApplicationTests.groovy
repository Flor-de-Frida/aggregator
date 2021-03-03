package br.com.flordefrida.aggregator


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@ActiveProfiles(['test'])
@SpringBootTest(webEnvironment = RANDOM_PORT)
class AggregatorApplicationTests extends Specification {

    def 'context loads'() {
        given:
            def four = '4'
            def two = '2'

        when:
            def response = four + two

        then:
            response == '42'
    }

}
