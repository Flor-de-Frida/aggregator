package br.com.flordefrida.aggregator.products.errors.conflict.impl

import br.com.flordefrida.aggregator.products.errors.conflict.ProductAlreadyExistsException

class ProductWithGtinAlreadyExistsException extends ProductAlreadyExistsException {
    ProductWithGtinAlreadyExistsException(final String gtin, final String organizationSlugName) {
        super('gtin', gtin, organizationSlugName)
    }
}
