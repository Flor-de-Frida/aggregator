package br.com.flordefrida.aggregator.utils.web.response.impl

import br.com.flordefrida.aggregator.utils.web.response.Result
import org.springframework.http.HttpStatus

abstract class CreatedResult<T> extends Result<T> {
    CreatedResult(final String message, final T result) {
        super(HttpStatus.CREATED.value(), message, result)
    }
}
