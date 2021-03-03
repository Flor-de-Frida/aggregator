package br.com.flordefrida.aggregator.utils.web.response.impl

import br.com.flordefrida.aggregator.utils.web.response.Result
import org.springframework.http.HttpStatus

abstract class FoundResult<T> extends Result<T> {
    FoundResult(final String message, final T result) {
        super(HttpStatus.OK.value(), message, result)
    }
}
