package br.com.flordefrida.aggregator.products.responses

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.response.impl.CreatedResult

class ProductCreatedResult extends CreatedResult<Product> {
    ProductCreatedResult(Product result) {
        super('product-created', result)
    }
}
