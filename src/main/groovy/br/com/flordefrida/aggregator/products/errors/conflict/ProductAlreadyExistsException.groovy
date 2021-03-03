package br.com.flordefrida.aggregator.products.errors.conflict

import br.com.flordefrida.aggregator.utils.errors.BaseException
import org.springframework.web.bind.annotation.ResponseStatus

import static org.springframework.http.HttpStatus.CONFLICT

@ResponseStatus(CONFLICT)
abstract class ProductAlreadyExistsException extends BaseException {
    ProductAlreadyExistsException(final String field, final String value, final String organizationSlugName) {
        super(
            "Product with ${field}=${value} already exists on organization ${organizationSlugName}" as String,
            CONFLICT.value(),
            "product-already-exists-for-${field}",
            value
        )
    }
}
