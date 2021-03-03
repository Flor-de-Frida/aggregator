package br.com.flordefrida.aggregator.products.repositories.impl

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.repositories.CustomProductRepository
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import static org.springframework.data.mongodb.core.query.Criteria.where
import static org.springframework.data.mongodb.core.query.Query.query

class CustomProductRepositoryImpl implements CustomProductRepository {
    private final ReactiveMongoTemplate template

    CustomProductRepositoryImpl(final ReactiveMongoTemplate reactiveMongoTemplate) {
        this.template = reactiveMongoTemplate
    }

    @Override
    Flux<Product> findAllByOrganizationSlugNameByPageRequest(
        final String organizationSlugName,
        final PageRequest pageRequest
    ) {
        Query query = queryForAllByOrganizationSlugNameByPageRequest(organizationSlugName, pageRequest)

        query = query.skip((pageRequest.page - 1) * pageRequest.size).limit(pageRequest.size)

        return this.template.find(query, Product)
    }

    @Override
    Mono<Long> countAllByOrganizationSlugNameByPageRequest(
        final String organizationSlugName,
        final PageRequest pageRequest
    ) {
        Query query = queryForAllByOrganizationSlugNameByPageRequest(organizationSlugName, pageRequest)

        return this.template.count(query, Product)
    }

    private static Query queryForAllByOrganizationSlugNameByPageRequest(
        final String organizationSlugName,
        final PageRequest pageRequest
    ) {
        Criteria criteria = where('organizationSlugName').is(organizationSlugName)

        final List<Criteria> filters = []
        if (!filters.isEmpty()) {
            final Criteria[] filtersAsArray = filters.toArray(new Criteria[0])
            criteria.andOperator(filtersAsArray)
        }

        Query query = query(criteria)

        if (pageRequest.fieldList && pageRequest.fieldList != Set.of('*')) {
            pageRequest.fieldList.each { field ->
                query.fields().include(field)
            }
        }

        return query
    }
}
