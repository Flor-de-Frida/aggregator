package br.com.flordefrida.aggregator.products.errors.notfound.impl

import br.com.flordefrida.aggregator.products.errors.notfound.ProductNotFoundException

class ProductsNotFoundException extends ProductNotFoundException {
    ProductsNotFoundException(final String organizationSlugName) {
        super(['organizationSlugName': organizationSlugName])
    }
}
