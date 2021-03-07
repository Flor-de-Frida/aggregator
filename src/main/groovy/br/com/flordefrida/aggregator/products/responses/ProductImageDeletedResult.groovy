package br.com.flordefrida.aggregator.products.responses

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResult

class ProductImageDeletedResult extends FoundResult<Product> {
    ProductImageDeletedResult(Product result) {
        super('product-image-deleted', result)
    }
}
