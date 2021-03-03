package br.com.flordefrida.aggregator.products.errors.notfound.impl

import br.com.flordefrida.aggregator.products.errors.notfound.ProductNotFoundException

class ProductWithSlugNameNotFoundException extends ProductNotFoundException {
    ProductWithSlugNameNotFoundException(final String slugName, final String organizationSlugName) {
        super(['slugName': slugName, 'organizationSlugName': organizationSlugName])
    }
}
