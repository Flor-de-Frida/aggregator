package br.com.flordefrida.aggregator.products.responses

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResult

class ProductImageUploadedResult extends FoundResult<Product> {
    ProductImageUploadedResult(Product result) {
        super('product-image-uploaded', result)
    }
}
