package br.com.flordefrida.aggregator.products.errors.conflict.impl

import br.com.flordefrida.aggregator.products.errors.conflict.ProductAlreadyExistsException

class ProductWithSkuAlreadyExistsException extends ProductAlreadyExistsException {
    ProductWithSkuAlreadyExistsException(final String sku, final String organizationSlugName) {
        super('sku', sku, organizationSlugName)
    }
}
