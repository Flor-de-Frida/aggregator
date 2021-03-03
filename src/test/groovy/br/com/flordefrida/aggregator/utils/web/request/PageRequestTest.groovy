package br.com.flordefrida.aggregator.utils.web.request

import br.com.flordefrida.aggregator.utils.errors.InvalidRequestException
import spock.lang.Specification

class PageRequestTest extends Specification {
    def 'should create a PageRequest without sorting'() {
        given:
            int page = 1
            int size = 10

        when:
            PageRequest request = PageRequest.of(page, size)

        then:
            request.page == page
            request.size == size
    }

    def 'should create a PageRequest'() {
        given:
            int page = 1
            int size = 10

        when:
            PageRequest request = PageRequest.of(page, size)

        then:
            request.page == page
            request.size == size
    }

    def 'should not create a page request with invalid page value'() {
        given:
            int page = 0
            int size = 0

        when:
            PageRequest request = PageRequest.of(page, size)

        then:
            !request
            def error = thrown(InvalidRequestException)
            error.message == "Request with errors in fields invalid-page-value=0,invalid-size-value=0"
    }
}
