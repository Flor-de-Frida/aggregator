package br.com.flordefrida.aggregator.products.services


import br.com.flordefrida.aggregator.products.domain.Product
import io.github.opensanca.annotation.ServiceValidation
import org.springframework.stereotype.Service

@Service
class ProductsValidationService {
    @ServiceValidation
    Product validate(final Product product) {
        return product
    }
}
