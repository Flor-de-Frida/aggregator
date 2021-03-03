package br.com.flordefrida.aggregator.products.repositories

import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import br.com.flordefrida.aggregator.products.ProductsFixturesBaseTests
import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import br.com.six2six.fixturefactory.Fixture
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier

@Slf4j
class ProductsRepositoryTest extends ProductsFixturesBaseTests {
    @Autowired
    ProductsRepository repository

    private List<Product> florDeFridaProducts = []
    private List<Product> clubeDaFridinhaProducts = []

    def setup() {
        this.florDeFridaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('FDF123') as Product)

        this.florDeFridaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('FDF234') as Product)

        this.florDeFridaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('FDF345') as Product)

        this.clubeDaFridinhaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('CDF123') as Product)

        this.clubeDaFridinhaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('CDF234') as Product)

        this.clubeDaFridinhaProducts << (Fixture
            .from(Product)
            .uses(this.templateProcessor)
            .gimme('CDF345') as Product)

        log.info(
            'created {} products on database in memory for tests purposes',
            (this.florDeFridaProducts + this.clubeDaFridinhaProducts).size()
        )
    }

    def cleanup() {
        this.repository.deleteAll(this.florDeFridaProducts).block()
        this.repository.deleteAll(this.clubeDaFridinhaProducts).block()
        log.info(
            'deleted {} products from database in memory',
            (this.florDeFridaProducts + this.clubeDaFridinhaProducts).size()
        )
    }

    def 'should find product by slugName and organizationSlugName'() {
        given:
            def productSlugName = 'flor-de-frida-test-product-fdf123'

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def findBySlugNameAndOrganizationSlugName = this.repository.findBySlugNameAndOrganizationSlugName(
                productSlugName,
                organizationSlugName
            )

        then:
            StepVerifier
                .create(findBySlugNameAndOrganizationSlugName)
                .expectNextMatches { product ->
                    return (product.name == 'Flor de Frida Test Product FDF123')
                }
                .verifyComplete()
    }

    def 'should find product by sku and organizationSlugName'() {
        given:
            def sku = 'FDF123'

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def findBySkuAndOrganizationSlugName = this.repository.findBySkuAndOrganizationSlugName(
                sku,
                organizationSlugName
            )

        then:
            StepVerifier
                .create(findBySkuAndOrganizationSlugName)
                .expectNextMatches { product ->
                    return (product.name == 'Flor de Frida Test Product FDF123')
                }
                .verifyComplete()
    }

    def 'should find product by gtin and organizationSlugName'() {
        given:
            def gtin = 'ABC-DEF-GHI'

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def findByGtinAndOrganizationSlugName = this.repository.findByGtinAndOrganizationSlugName(
                gtin,
                organizationSlugName
            )

        then:
            StepVerifier
                .create(findByGtinAndOrganizationSlugName)
                .expectNextMatches { product ->
                    return (product.name == 'Flor de Frida Test Product FDF123')
                }
                .verifyComplete()
    }

    def 'should delete product by sku and organizationSlugName'() {
        given:
            def sku = 'FDF123'

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def findBySkuAndOrganizationSlugName = this.repository.findBySkuAndOrganizationSlugName(
                sku,
                organizationSlugName
            )

        and:
            def deleteBySkuAndOrganizationSlugName = this.repository.deleteBySkuAndOrganizationSlugName(
                sku,
                organizationSlugName
            )

        then:
            StepVerifier
                .create(findBySkuAndOrganizationSlugName)
                .expectNextCount(1)
                .verifyComplete()

        and:
            StepVerifier
                .create(deleteBySkuAndOrganizationSlugName)
                .expectNextMatches { product ->
                    return (product.name == 'Flor de Frida Test Product FDF123')
                }
                .verifyComplete()

        and:
            StepVerifier
                .create(findBySkuAndOrganizationSlugName)
                .expectNextCount(0)
                .verifyComplete()
    }

    def 'should find all products by organizationSlugName paginated'() {
        given:
            def page1 = PageRequest.of(1, 2)
            def page2 = PageRequest.of(2, 2)

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def findPageOne = this.repository.findAllByOrganizationSlugNameByPageRequest(
                organizationSlugName,
                page1
            )

        and:
            def findPageTwo = this.repository.findAllByOrganizationSlugNameByPageRequest(
                organizationSlugName,
                page2
            )

        then:
            StepVerifier
                .create(findPageOne)
                .expectNextCount(2)
                .verifyComplete()

        and:
            StepVerifier
                .create(findPageTwo)
                .expectNextCount(1)
                .verifyComplete()
    }

    def 'should count all products by organizationSlugName'() {
        given:
            def page1 = PageRequest.of(1, 2)
            def page2 = PageRequest.of(2, 2)

        and:
            def organizationSlugName = 'flor-de-frida'

        when:
            def countPageOne = this.repository.countAllByOrganizationSlugNameByPageRequest(
                organizationSlugName,
                page1
            )

        and:
            def countPageTwo = this.repository.countAllByOrganizationSlugNameByPageRequest(
                organizationSlugName,
                page2
            )

        then:
            StepVerifier
                .create(countPageOne)
                .expectNextMatches { count -> count == 3 }
                .verifyComplete()

        and:
            StepVerifier
                .create(countPageTwo)
                .expectNextMatches { count -> count == 3 }
                .verifyComplete()
    }
}
