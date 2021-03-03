package br.com.flordefrida.aggregator.utils.config

import org.springframework.context.annotation.Configuration

import javax.annotation.PostConstruct

@Configuration
class DefaultTimeZone {
    @PostConstruct
    static void setDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone('UTC'))
    }
}
