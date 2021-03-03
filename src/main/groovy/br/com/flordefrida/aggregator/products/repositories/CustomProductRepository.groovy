package br.com.flordefrida.aggregator.products.repositories

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomProductRepository {
    Flux<Product> findAllByOrganizationSlugNameByPageRequest(
        final String organizationSlugName,
        final PageRequest pageRequest
    )

    Mono<Long> countAllByOrganizationSlugNameByPageRequest(
        final String organizationSlugName,
        final PageRequest pageRequest
    )
}
