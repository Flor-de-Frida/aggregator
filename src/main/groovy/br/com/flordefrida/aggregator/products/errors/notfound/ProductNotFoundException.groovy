package br.com.flordefrida.aggregator.products.errors.notfound

import br.com.flordefrida.aggregator.utils.errors.BaseException
import org.springframework.web.bind.annotation.ResponseStatus

import static br.com.flordefrida.aggregator.utils.MapToString.mapToString
import static org.springframework.http.HttpStatus.NOT_FOUND

@ResponseStatus(NOT_FOUND)
abstract class ProductNotFoundException extends BaseException {
    ProductNotFoundException(final Map<String, String> params) {
        super(
            "Product with ${mapToString(params)} not found" as String,
            NOT_FOUND.value(),
            'product-not-found',
            params.values().join(',')
        )
    }
}
