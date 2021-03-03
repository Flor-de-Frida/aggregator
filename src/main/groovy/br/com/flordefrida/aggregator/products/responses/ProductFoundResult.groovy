package br.com.flordefrida.aggregator.products.responses

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResult

class ProductFoundResult extends FoundResult<Product> {
    ProductFoundResult(Product result) {
        super('product-found', result)
    }
}
