package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.services.ProductsService
import br.com.flordefrida.aggregator.products.ProductsFixturesBaseTests
import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.errors.invalid.InvalidProductException
import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import br.com.six2six.fixturefactory.Fixture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import reactor.test.StepVerifier

import java.time.LocalDateTime
import java.time.ZoneId

class ProductsServiceTest extends ProductsFixturesBaseTests {
    @Autowired
    ProductsService service

    @Autowired
    ProductsRepository repository

    @Value('${info.default-author}')
    String defaultAuthor

    private Product existingProduct

    def setup() {
        this.existingProduct = Fixture.from(Product).uses(this.templateProcessor).gimme('FDF123') as Product
    }

    def cleanup() {
        this.repository.deleteAll().block()
    }

    def 'should not create new product if it is invalid'() {
        given:
            def newProduct = new Product()

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            StepVerifier
                .create(create)
                .expectErrorMatches { error ->
                    def errorMatches = error.class.simpleName == 'InvalidProductException'
                    def messageMatches = error.message.contains('Product.name=invalid-name') &&
                        error.message.contains('Product.organizationSlugName=invalid-organization-slug-name') &&
                        error.message.contains('Product.gtin=invalid-gtin') &&
                        error.message.contains('Product.slugName=invalid-slug-name') &&
                        error.message.contains('Product.sku=invalid-sku') &&
                        error.message.contains('Product.description=invalid-description') &&
                        error.message.contains('Product.availableOnDemand=invalid-availability-on-demand') &&
                        error.message.contains('Product.available=invalid-availability')
                    def reasonIdMatches = (error as InvalidProductException).reasonId == 'invalid-request'
                    def exception = (error as InvalidProductException)
                    def reasonMatches = exception.reason.contains('invalid-name') &&
                        exception.reason.contains('invalid-organization-slug-name') &&
                        exception.reason.contains('invalid-gtin') &&
                        exception.reason.contains('invalid-slug-name') &&
                        exception.reason.contains('invalid-sku') &&
                        exception.reason.contains('invalid-description') &&
                        exception.reason.contains('invalid-availability-on-demand') &&
                        exception.reason.contains('invalid-availability')

                    return errorMatches && messageMatches && reasonIdMatches && reasonMatches
                }
                .verify()
    }

    def 'should not create new product if sku already exists'() {
        given:
            def newProduct = Fixture.from(Product).gimme('FDF234') as Product

        and:
            newProduct.sku = 'FDF123'

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            this.existingProduct.sku == newProduct.sku

        and:
            StepVerifier
                .create(create)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuAlreadyExistsException'
                    def errorMessageMatches = error.message == 'Product with sku=FDF123 already exists on organization flor-de-frida'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should not create new product if slugName already exists'() {
        given:
            def newProduct = Fixture.from(Product).gimme('FDF123') as Product

        and:
            newProduct.sku = 'FDF234'
            newProduct.gtin = 'BCD-EFG-HIJ'

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            this.existingProduct.slugName == newProduct.slugName

        and:
            StepVerifier
                .create(create)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSlugNameAlreadyExistsException'
                    def errorMessageMatches = error.message == 'Product with slugName=flor-de-frida-test-product-fdf123 already exists on organization flor-de-frida'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should not create new product if gtin already exists'() {
        given:
            def newProduct = Fixture.from(Product).gimme('FDF234') as Product

        and:
            newProduct.gtin = 'ABC-DEF-GHI'

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            this.existingProduct.gtin == newProduct.gtin

        and:
            StepVerifier
                .create(create)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithGtinAlreadyExistsException'
                    def errorMessageMatches = error.message == 'Product with gtin=ABC-DEF-GHI already exists on organization flor-de-frida'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should create a valid non existing product'() {
        given:
            Product newProduct = Fixture.from(Product).gimme('FDF234')

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            StepVerifier
                .create(create)
                .expectNextMatches { created ->
                    def skuMatches = created.sku == 'FDF234'
                    def organizationMatches = created.organizationSlugName == 'flor-de-frida'

                    def creationAuthorMatches = created.creationInfo.author == this.defaultAuthor
                    def creationMomentIsBeforeNow = created.creationInfo.moment.isBefore(LocalDateTime.now(ZoneId.of("UTC")))
                    def creationInfoMatches = creationAuthorMatches && creationMomentIsBeforeNow

                    def updateAuthorMatches = created.updateInfo.author == this.defaultAuthor
                    def updateMomentMatchesCreationMoment = created.updateInfo.moment == created.creationInfo.moment
                    def updateInfoMatches = updateAuthorMatches && updateMomentMatchesCreationMoment

                    return skuMatches && organizationMatches && creationInfoMatches && updateInfoMatches
                }
                .verifyComplete()
    }

    def 'should fix product availability on creation'() {
        given:
            Product newProduct = Fixture.from(Product).gimme('FDF234')

        and:
            newProduct.available = true
            newProduct.availableOnDemand = true

        when:
            def create = this.service.createNewProduct(newProduct)

        then:
            StepVerifier
                .create(create)
                .expectNextMatches { created ->
                    def skuMatches = created.sku == 'FDF234'
                    def organizationMatches = created.organizationSlugName == 'flor-de-frida'

                    def availabilityIsFalse = !created.available
                    def availabilityOnDemandIsTrue = created.availableOnDemand

                    return skuMatches && organizationMatches && availabilityIsFalse && availabilityOnDemandIsTrue
                }
                .verifyComplete()
    }

    def 'should not delete not existing product'() {
        given:
            def sku = 'FDF234'
            def organizationSlugName = 'flor-de-frida'

        when:
            def delete = this.service.deleteProduct(sku, organizationSlugName)

        then:
            StepVerifier
                .create(delete)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuNotFoundException'
                    def errorMessageMatches = error.message == 'Product with sku=FDF234,organizationSlugName=flor-de-frida not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should delete existing product'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'flor-de-frida'

        when:
            def delete = this.service.deleteProduct(sku, organizationSlugName)

        then:
            StepVerifier
                .create(delete)
                .expectNextMatches { deleted ->
                    deleted.sku == sku && deleted.organizationSlugName == organizationSlugName
                }
                .verifyComplete()
    }

    def 'should get product by sku and organizationSlugName'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'flor-de-frida'

        when:
            def find = this.service.findBySkuAndOrganizationSlugName(sku, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectNextMatches { found ->
                    found.sku == sku && found.organizationSlugName == organizationSlugName
                }
                .verifyComplete()
    }

    def 'should throw ProductNotFoundWithSkuException for unknown sku and organizationSlugName'() {
        given:
            def sku = 'unknown'
            def organizationSlugName = 'flor-de-frida'

        when:
            def find = this.service.findBySkuAndOrganizationSlugName(sku, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuNotFoundException'
                    def errorMessageMatches = error.message == 'Product with sku=unknown,organizationSlugName=flor-de-frida not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should throw ProductNotFoundWithSkuException for sku and unknown organizationSlugName'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'unknown'

        when:
            def find = this.service.findBySkuAndOrganizationSlugName(sku, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuNotFoundException'
                    def errorMessageMatches = error.message == 'Product with sku=FDF123,organizationSlugName=unknown not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should get product by slugName and organizationSlugName'() {
        given:
            def slugName = 'flor-de-frida-test-product-fdf123'
            def organizationSlugName = 'flor-de-frida'

        when:
            def find = this.service.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectNextMatches { found ->
                    found.sku == 'FDF123' &&
                        found.organizationSlugName == organizationSlugName &&
                        found.slugName == slugName
                }
                .verifyComplete()
    }

    def 'should throw ProductNotFoundWithSkuException for unknown slugName and organizationSlugName'() {
        given:
            def slugName = 'unknown'
            def organizationSlugName = 'flor-de-frida'

        when:
            def find = this.service.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSlugNameNotFoundException'
                    def errorMessageMatches = error.message == 'Product with slugName=unknown,organizationSlugName=flor-de-frida not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should throw ProductNotFoundWithSkuException for slugName and unknown organizationSlugName'() {
        given:
            def slugName = 'flor-de-frida-test-product-fdf123'
            def organizationSlugName = 'unknown'

        when:
            def find = this.service.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName)

        then:
            StepVerifier
                .create(find)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSlugNameNotFoundException'
                    def errorMessageMatches = error.message == 'Product with slugName=flor-de-frida-test-product-fdf123,organizationSlugName=unknown not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should get all products paginated by organizationSlugName'() {
        given:
            def organization1SlugName = 'flor-de-frida'
            def organization2SlugName = 'clube-da-fridinha'
            def requestPage1 = PageRequest.of(1, 2)
            def requestPage2 = PageRequest.of(2, 2)

        and:
            def product1 = Fixture.from(Product).uses(this.templateProcessor).gimme('FDF234') as Product
            def product2 = Fixture.from(Product).uses(this.templateProcessor).gimme('FDF345') as Product
            def product3 = Fixture.from(Product).uses(this.templateProcessor).gimme('CDF123') as Product
            def product4 = Fixture.from(Product).uses(this.templateProcessor).gimme('CDF234') as Product

        when:
            def getAllByOrg1Page1 = this.service.findAllByOrganizationSlugName(organization1SlugName, requestPage1)
            def getAllByOrg1Page2 = this.service.findAllByOrganizationSlugName(organization1SlugName, requestPage2)
            def getAllByOrg2 = this.service.findAllByOrganizationSlugName(organization2SlugName, requestPage1)

        then:
            StepVerifier
                .create(getAllByOrg1Page1)
                .expectNextMatches { results ->
                    def foundData = results.results.collect {
                        it._id = null
                        return it
                    }
                    def expectedData = [this.existingProduct, product1].collect {
                        it._id = null
                        return it
                    }

                    def dataMatches = foundData == expectedData
                    def pageMatches = results.page == 1
                    def sizeMatches = results.size == 2
                    def totalMatches = results.total == 3

                    return dataMatches && pageMatches && sizeMatches && totalMatches
                }
                .verifyComplete()

        and:
            StepVerifier
                .create(getAllByOrg1Page2)
                .expectNextMatches { results ->
                    def foundData = results.results.collect {
                        it._id = null
                        return it
                    }
                    def expectedData = [product2].collect {
                        it._id = null
                        return it
                    }

                    def dataMatches = foundData == expectedData
                    def pageMatches = results.page == 2
                    def sizeMatches = results.size == 2
                    def totalMatches = results.total == 3

                    return dataMatches && pageMatches && sizeMatches && totalMatches
                }
                .verifyComplete()

        and:
            StepVerifier
                .create(getAllByOrg2)
                .expectNextMatches { results ->
                    def foundData = results.results.collect {
                        it._id = null
                        return it
                    }
                    def expectedData = [product3, product4].collect {
                        it._id = null
                        return it
                    }

                    def dataMatches = foundData == expectedData
                    def pageMatches = results.page == 1
                    def sizeMatches = results.size == 2
                    def totalMatches = results.total == 2

                    return dataMatches && pageMatches && sizeMatches && totalMatches
                }
                .verifyComplete()
    }

    def 'should not get all products for unknown organizationSlugName'() {
        given:
            def organizationSlugName = 'unknown'
            def request = PageRequest.of(1, 10)

        when:
            def getAllByOrg = this.service.findAllByOrganizationSlugName(organizationSlugName, request)

        then:
            StepVerifier
                .create(getAllByOrg)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductsNotFoundException'
                    def errorMessageMatches = error.message == 'Product with organizationSlugName=unknown not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }
}
