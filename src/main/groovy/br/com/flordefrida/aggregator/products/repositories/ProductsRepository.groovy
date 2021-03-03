package br.com.flordefrida.aggregator.products.repositories

import br.com.flordefrida.aggregator.products.domain.Product
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ProductsRepository extends ReactiveMongoRepository<Product, String>, CustomProductRepository {
    Mono<Product> findBySlugNameAndOrganizationSlugName(final String slugName, final String organizationSlugName)

    Mono<Product> findBySkuAndOrganizationSlugName(final String sku, final String organizationSlugName)

    Mono<Product> findByGtinAndOrganizationSlugName(final String gtin, final String organizationSlugName)

    Mono<Product> deleteBySkuAndOrganizationSlugName(final String sku, final String organizationSlugName)
}
