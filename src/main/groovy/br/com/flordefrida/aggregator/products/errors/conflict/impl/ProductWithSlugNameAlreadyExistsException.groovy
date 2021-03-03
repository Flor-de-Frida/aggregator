package br.com.flordefrida.aggregator.products.errors.conflict.impl

import br.com.flordefrida.aggregator.products.errors.conflict.ProductAlreadyExistsException

class ProductWithSlugNameAlreadyExistsException extends ProductAlreadyExistsException {
    ProductWithSlugNameAlreadyExistsException(final String slugName, final String organizationSlugName) {
        super('slugName', slugName, organizationSlugName)
    }
}
