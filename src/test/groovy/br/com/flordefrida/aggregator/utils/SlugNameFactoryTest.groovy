package br.com.flordefrida.aggregator.utils

import spock.lang.Specification

class SlugNameFactoryTest extends Specification {
    def 'should make slugName'() {
        given:
            final String nonNormalizedText = 'Teste com espaço e à á acentuação e$pecial & , com (vírgula) e --- traço.'
        when:
            final String slugName = SlugNameFactory.make(nonNormalizedText)
        then:
            slugName == 'teste-com-espaco-e-a-a-acentuacao-especial-e-com-virgula-e-traco'
    }
}
