package br.com.flordefrida.aggregator.products.responses

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResult

class ProductUpdatedResult extends FoundResult<Product> {
    ProductUpdatedResult(Product result) {
        super('product-deleted', result)
    }
}
