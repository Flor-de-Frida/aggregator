package br.com.flordefrida.aggregator.products.errors.invalid

import br.com.flordefrida.aggregator.utils.errors.BaseException
import org.springframework.web.bind.annotation.ResponseStatus

import static br.com.flordefrida.aggregator.utils.MapToString.mapListToString
import static org.springframework.http.HttpStatus.BAD_REQUEST

@ResponseStatus(BAD_REQUEST)
class InvalidProductException extends BaseException {
    InvalidProductException(final Map<String, List<String>> params) {
        super(
            "Product with ${mapListToString(params)} is invalid" as String,
            BAD_REQUEST.value(),
            params.values().collect { it.join(',') }.join(';'),
            'invalid-request'
        )
    }
}
