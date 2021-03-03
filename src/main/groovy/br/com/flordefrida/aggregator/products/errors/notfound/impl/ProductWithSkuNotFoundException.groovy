package br.com.flordefrida.aggregator.products.errors.notfound.impl

import br.com.flordefrida.aggregator.products.errors.notfound.ProductNotFoundException

class ProductWithSkuNotFoundException extends ProductNotFoundException {
    ProductWithSkuNotFoundException(final String sku, final String organizationSlugName) {
        super(['sku': sku, 'organizationSlugName': organizationSlugName])
    }
}
