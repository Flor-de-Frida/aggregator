package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.errors.conflict.impl.ProductWithGtinAlreadyExistsException
import br.com.flordefrida.aggregator.products.errors.conflict.impl.ProductWithSkuAlreadyExistsException
import br.com.flordefrida.aggregator.products.errors.conflict.impl.ProductWithSlugNameAlreadyExistsException
import br.com.flordefrida.aggregator.products.errors.invalid.InvalidProductException
import br.com.flordefrida.aggregator.products.errors.notfound.impl.ProductWithSkuNotFoundException
import br.com.flordefrida.aggregator.products.errors.notfound.impl.ProductWithSlugNameNotFoundException
import br.com.flordefrida.aggregator.products.errors.notfound.impl.ProductsNotFoundException
import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import br.com.flordefrida.aggregator.utils.SlugNameFactory
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import br.com.flordefrida.aggregator.utils.web.response.impl.FoundResults
import io.github.opensanca.exception.ServiceValidationException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProductsService {
    private final ProductsRepository repository
    private final ProductsValidationService productsValidatorService
    private final ProductsInfoService productsInfoService
    private final ProductsImageService productsImageService

    ProductsService(
        final ProductsRepository repository,
        final ProductsValidationService productsValidatorService,
        final ProductsInfoService productsInfoService,
        final ProductsImageService productsImageService
    ) {
        this.repository = repository
        this.productsValidatorService = productsValidatorService
        this.productsInfoService = productsInfoService
        this.productsImageService = productsImageService
    }

    Mono<Product> createNewProduct(final Product productNotValidated) {
        try {
            makeSlugName(productNotValidated)
            def validatedProduct = this.productsValidatorService.validate(productNotValidated)

            fixAvailability(validatedProduct)

            return this
                .verifyIfProductExists(validatedProduct)
                .switchIfEmpty(this.saveNewProduct(validatedProduct))
        } catch (error) {
            if (error.class.simpleName == 'ServiceValidationException')
                return Mono.error(
                    new InvalidProductException(
                        (error as ServiceValidationException).errors
                    )
                )

            return Mono.error(error)
        }
    }

    Mono<Product> deleteProduct(final String sku, final String organizationSlugName) {
        return this.repository
            .deleteBySkuAndOrganizationSlugName(sku, organizationSlugName)
            .switchIfEmpty(Mono.error(new ProductWithSkuNotFoundException(sku, organizationSlugName)))
            .map(this.productsImageService.&deleteAllProductImages)
    }

    Mono<Product> findBySkuAndOrganizationSlugName(final String sku, final String organizationSlugName) {
        return this.repository
            .findBySkuAndOrganizationSlugName(sku, organizationSlugName)
            .switchIfEmpty(Mono.defer {
                Mono.error(new ProductWithSkuNotFoundException(sku, organizationSlugName))
            } as Mono<Product>)
    }

    Mono<Product> findBySlugNameAndOrganizationSlugName(final String slugName, final String organizationSlugName) {
        return this.repository
            .findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName)
            .switchIfEmpty(Mono.defer {
                Mono.error(new ProductWithSlugNameNotFoundException(slugName, organizationSlugName))
            } as Mono<Product>)
    }

    Mono<FoundResults<Product>> findAllByOrganizationSlugName(final String organizationSlugName, final PageRequest request) {
        return this
            .repository.countAllByOrganizationSlugNameByPageRequest(organizationSlugName, request)
            .flatMap { count ->
                if (!count || count == 0L)
                    return Mono.error(new ProductsNotFoundException(organizationSlugName)) as Mono<FoundResults<Product>>

                return this
                    .repository.findAllByOrganizationSlugNameByPageRequest(organizationSlugName, request)
                    .buffer()
                    .next()
                    .map { products ->
                        return new FoundResults<>(
                            products,
                            request.page,
                            request.size,
                            count.toInteger()
                        )
                    }
            }
    }

    private Mono<Product> verifyIfProductExists(final Product product) {
        return this
            .verifyIfProductExistsWithSku(product)
            .switchIfEmpty(this.verifyIfProductExistsWithGtin(product))
            .switchIfEmpty(this.verifyIfProductExistsWithSlugName(product))
    }

    private Mono<Product> verifyIfProductExistsWithSku(final Product product) {
        return Mono.defer {
            this.repository
                .findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName)
                .flatMap {
                    return Mono.error(new ProductWithSkuAlreadyExistsException(
                        product.sku,
                        product.organizationSlugName
                    ))
                }
        } as Mono<Product>
    }

    private Mono<Product> verifyIfProductExistsWithSlugName(final Product product) {
        return Mono.defer {
            this.repository
                .findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName)
                .flatMap {
                    return Mono.error(new ProductWithSlugNameAlreadyExistsException(
                        product.slugName,
                        product.organizationSlugName
                    ))
                }
        } as Mono<Product>
    }

    private Mono<Product> verifyIfProductExistsWithGtin(final Product product) {
        return Mono.defer {
            this.repository
                .findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName)
                .flatMap {
                    return Mono.error(new ProductWithGtinAlreadyExistsException(
                        product.gtin,
                        product.organizationSlugName
                    ))
                }
        } as Mono<Product>
    }

    private Mono<Product> saveNewProduct(final Product product) {
        product.updateInfo = this.productsInfoService.generateUpdateInfo(product.updateInfo?.author)

        if (!product.creationInfo)
            product.creationInfo = this.productsInfoService.generateCreationInfo(product.updateInfo)

        return Mono.defer { this.repository.save(product) }
    }

    private static void makeSlugName(final Product product) {
        if (product?.name)
            product.slugName = SlugNameFactory.make(product.name)
    }

    private static void fixAvailability(final Product product) {
        if (product?.availableOnDemand)
            product.available = false
    }
}
