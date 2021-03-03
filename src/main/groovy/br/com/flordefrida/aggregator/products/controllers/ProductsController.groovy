package br.com.flordefrida.aggregator.products.controllers

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.responses.ProductCreatedResult
import br.com.flordefrida.aggregator.products.responses.ProductFoundResult
import br.com.flordefrida.aggregator.products.responses.ProductUpdatedResult
import br.com.flordefrida.aggregator.products.services.ProductsService
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResults
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK

@RestController
@RequestMapping('/products')
class ProductsController {
    private final ProductsService productsService

    ProductsController(final ProductsService productsService) {
        this.productsService = productsService
    }

    @PostMapping(value = ['', '/'])
    @ResponseStatus(CREATED)
    Mono<ProductCreatedResult> create(@RequestBody Product product) {
        return this.productsService
            .createNewProduct(product)
            .map { createdProduct ->
                return new ProductCreatedResult(createdProduct)
            }
    }

    @GetMapping('/sku/{sku}')
    @ResponseStatus(OK)
    Mono<ProductFoundResult> getBySku(
        @PathVariable String sku,
        @RequestHeader(name = 'x-organization', required = true) String organizationSlugName
    ) {
        return this.productsService
            .findBySkuAndOrganizationSlugName(sku, organizationSlugName)
            .map { found ->
                return new ProductFoundResult(found)
            }
    }

    @GetMapping('/{slugName}')
    @ResponseStatus(OK)
    Mono<ProductFoundResult> getBySlugName(
        @PathVariable String slugName,
        @RequestHeader(name = 'x-organization', required = true) String organizationSlugName
    ) {
        return this.productsService
            .findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName)
            .map { found ->
                return new ProductFoundResult(found)
            }
    }

    @DeleteMapping('/sku/{sku}')
    @ResponseStatus(OK)
    Mono<ProductUpdatedResult> deleteBySku(
        @PathVariable String sku,
        @RequestHeader(name = 'x-organization', required = true) String organizationSlugName
    ) {
        return this.productsService
            .deleteProduct(sku, organizationSlugName)
            .map { deleted ->
                return new ProductUpdatedResult(deleted)
            }
    }

    @GetMapping(value = ['', '/'])
    @ResponseStatus(OK)
    Mono<FoundResults<Product>> getAllByOrganizationSlugNameAndPaginated(
        @RequestHeader(name = 'x-organization', required = true) String organizationSlugName,
        @RequestParam(required = false, defaultValue = '1') int page,
        @RequestParam(required = false, defaultValue = '10') int size,
        @RequestParam(required = false, defaultValue = '*') String fl
    ) {
        def request = PageRequest.of(page, size).withFieldList(fl)
        return this.productsService.findAllByOrganizationSlugName(organizationSlugName, request)
    }
}
