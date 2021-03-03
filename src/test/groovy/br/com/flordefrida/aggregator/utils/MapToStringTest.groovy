package br.com.flordefrida.aggregator.utils

import spock.lang.Specification

class MapToStringTest extends Specification {
    def 'should convert map to string'() {
        given:
            def map = ['param1': 'value1', 'param2': 'value2']

        when:
            def text = MapToString.mapToString(map)

        then:
            text == 'param1=value1,param2=value2'
    }

    def 'should convert empty map to empty string'() {
        given:
            def map = [:] as Map<String, String>

        when:
            def text = MapToString.mapToString(map)

        then:
            text == ''
    }

    def 'should convert null map to empty string'() {
        given:
            def map = null

        when:
            def text = MapToString.mapToString(map)

        then:
            text == ''
    }

    def 'should convert map list to string'() {
        given:
            def map = ['param1': ['value1', 'value2'], 'param2': ['value1']] as Map<String, List<String>>

        when:
            def text = MapToString.mapListToString(map)

        then:
            text == 'param1=value1,value2;param2=value1'
    }

    def 'should convert map list with null list to string'() {
        given:
            def map = ['param1': ['value1', 'value2'], 'param2': null] as Map<String, List<String>>

        when:
            def text = MapToString.mapListToString(map)

        then:
            text == 'param1=value1,value2'
    }

    def 'should convert map list with empty list to string'() {
        given:
            def map = ['param1': ['value1', 'value2'], 'param2': []] as Map<String, List<String>>

        when:
            def text = MapToString.mapListToString(map)

        then:
            text == 'param1=value1,value2'
    }

    def 'should convert empty map list to empty string'() {
        given:
            def map = [:] as Map<String, List<String>>

        when:
            def text = MapToString.mapListToString(map)

        then:
            text == ''
    }

    def 'should convert null map list to empty string'() {
        given:
            def map = null

        when:
            def text = MapToString.mapListToString(map)

        then:
            text == ''
    }
}
