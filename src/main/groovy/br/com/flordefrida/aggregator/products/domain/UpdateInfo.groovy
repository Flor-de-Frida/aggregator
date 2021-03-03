package br.com.flordefrida.aggregator.products.domain

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.time.LocalDateTime

@EqualsAndHashCode(includes = ['author'])
@ToString(includePackage = false, includeNames = true)
class UpdateInfo {
    LocalDateTime moment

    String author
}
