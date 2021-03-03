package br.com.flordefrida.aggregator.products.controllers

import br.com.flordefrida.aggregator.products.ProductsFixturesBaseTests
import br.com.flordefrida.aggregator.products.domain.CreationInfo
import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.domain.UpdateInfo
import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import br.com.flordefrida.aggregator.utils.web.request.PageRequest
import br.com.six2six.fixturefactory.Fixture
import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.CONFLICT
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.MediaType.APPLICATION_JSON

class ProductsControllerTest extends ProductsFixturesBaseTests {
    @Autowired
    ObjectMapper objectMapper

    @Autowired
    WebTestClient client

    @SpringBean
    ProductsRepository repository = Mock(ProductsRepository)

    def 'should create new product'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')
            String json = this.objectMapper.writeValueAsString(product)

        when:
            this.repository.findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName) >> { sku, organizationSlugName ->
                return Mono.empty()
            }
            this.repository.findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName) >> {
                return Mono.empty()
            }
            UpdateInfo updateInfo = null
            CreationInfo creationInfo = null
            this.repository.save(_ as Product) >> { Product p ->
                updateInfo = p.updateInfo
                creationInfo = p.creationInfo

                return Mono.just(p)
            }

        then:
            this.client
                .post()
                .uri('/products')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(json), String)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json("""{
                    "status": ${CREATED.value()},
                    "message": "product-created",
                    "result": {
                        "sku": "${product.sku}",
                        "name": "${product.name}",
                        "slugName": "${product.slugName}",
                        "gtin": "${product.gtin}",
                        "organizationSlugName": "${product.organizationSlugName}",
                        "updateInfo": {
                            "author": "${updateInfo.author}"
                        },
                        "creationInfo": {
                            "author": "${creationInfo.author}"
                        },
                        "brandSlugName": "flor-de-frida",
                        "availableOnDemand": false,
                        "available": true
                    }
                }""")
    }

    def 'should not create new product with existing sku'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')
            String json = this.objectMapper.writeValueAsString(product)

        when:
            this.repository.findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName) >> {
                return Mono.just(product)
            }
            this.repository.findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName) >> {
                return Mono.empty()
            }

        then:
            this.client
                .post()
                .uri('/products')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(json), String)
                .exchange()
                .expectStatus()
                .isEqualTo(CONFLICT)
                .expectBody()
                .json("""{
                    "status": ${CONFLICT.value()},
                    "message": "Product with sku=${product.sku} already exists on organization flor-de-frida",
                    "reason": "product-already-exists-for-sku",
                    "reasonId": "${product.sku}"
                }""")
    }

    def 'should not create new product with existing gtin'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')
            String json = this.objectMapper.writeValueAsString(product)

        when:
            this.repository.findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName) >> {
                return Mono.just(product)
            }
            this.repository.findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName) >> {
                return Mono.empty()
            }

        then:
            this.client
                .post()
                .uri('/products')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(json), String)
                .exchange()
                .expectStatus()
                .isEqualTo(CONFLICT)
                .expectBody()
                .json("""{
                    "status": ${CONFLICT.value()},
                    "message": "Product with gtin=${product.gtin} already exists on organization flor-de-frida",
                    "reason": "product-already-exists-for-gtin",
                    "reasonId": "${product.gtin}"
                }""")
    }

    def 'should not create new product with existing slugName'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')
            String json = this.objectMapper.writeValueAsString(product)

        when:
            this.repository.findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName) >> {
                return Mono.just(product)
            }

        then:
            this.client
                .post()
                .uri('/products')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(json), String)
                .exchange()
                .expectStatus()
                .isEqualTo(CONFLICT)
                .expectBody()
                .json("""{
                    "status": ${CONFLICT.value()},
                    "message": "Product with slugName=${product.slugName} already exists on organization flor-de-frida",
                    "reason": "product-already-exists-for-slugName",
                    "reasonId": "${product.slugName}"
                }""")
    }

    def 'should not create invalid product'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')
            product.name = null
            String json = this.objectMapper.writeValueAsString(product)

        when:
            this.repository.findBySkuAndOrganizationSlugName(product.sku, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findByGtinAndOrganizationSlugName(product.gtin, product.organizationSlugName) >> {
                return Mono.empty()
            }
            this.repository.findBySlugNameAndOrganizationSlugName(product.slugName, product.organizationSlugName) >> {
                return Mono.empty()
            }

        then:
            this.client
                .post()
                .uri('/products')
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(json), String)
                .exchange()
                .expectStatus()
                .isEqualTo(BAD_REQUEST)
                .expectBody()
                .json("""{
                    "status": ${BAD_REQUEST.value()},
                    "message": "Product with Product.name=invalid-name is invalid",
                    "reason": "invalid-name",
                    "reasonId": "invalid-request"
                }""")
    }

    def 'should get product by sku and organization slugName'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'flor-de-frida'
            Product product = Fixture.from(Product).gimme(sku)

        when:
            this.repository.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> { s, o ->
                return Mono.just(product)
            }

        then:
            this.client
                .get()
                .uri('/products/sku/{sku}', sku)
                .accept(APPLICATION_JSON)
                .header('x-organization', 'flor-de-frida')
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("""{
                    "status": ${OK.value()},
                    "message": "product-found",
                    "result": {
                        "sku": "${sku}",
                        "name": "${product.name}",
                        "slugName": "${product.slugName}",
                        "gtin": "${product.gtin}",
                        "organizationSlugName": "${organizationSlugName}",
                        "brandSlugName": "flor-de-frida",
                        "availableOnDemand": false,
                        "available": true
                    }
                }""")
    }

    def 'should not get product by sku when organization slugName header is missing'() {
        given:
            def sku = 'F12345'

        expect:
            this.client
                .get()
                .uri('/products/sku/{sku}', sku)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("""{
                    "status": ${BAD_REQUEST.value()},
                    "message": "400 BAD_REQUEST \\"Missing request header 'x-organization' for method parameter of type String\\""
                }""")
    }

    def 'should not get product by sku for wrong organization slugName'() {
        given:
            def sku = 'F12345'
            def organizationSlugName = 'clube-da-fridinha'

        when:
            this.repository.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> { s, o ->
                return Mono.empty()
            }

        then:
            this.client
                .get()
                .uri('/products/sku/{sku}', sku)
                .accept(APPLICATION_JSON)
                .header('x-organization', organizationSlugName)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with sku=F12345,organizationSlugName=clube-da-fridinha not found",
                    "reason": "product-not-found",
                    "reasonId": "F12345,clube-da-fridinha"
                }""")
    }

    def 'should not get product by sku for not existing sku'() {
        given:
            def sku = 'unknown'
            def organizationSlugName = 'flor-de-frida'

        when:
            this.repository.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> { s, o ->
                return Mono.empty()
            }

        then:
            this.client
                .get()
                .uri('/products/sku/{sku}', sku)
                .accept(APPLICATION_JSON)
                .header('x-organization', organizationSlugName)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with sku=unknown,organizationSlugName=flor-de-frida not found",
                    "reason": "product-not-found",
                    "reasonId": "unknown,flor-de-frida"
                }""")
    }

    def 'should get product by slugName and organization slugName'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'flor-de-frida'
            Product product = Fixture.from(Product).gimme(sku)
            def slugName = product.slugName

        when:
            this.repository.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName) >> { s, o ->
                return Mono.just(product)
            }

        then:
            this.client
                .get()
                .uri('/products/{slugName}', slugName)
                .accept(APPLICATION_JSON)
                .header('x-organization', 'flor-de-frida')
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("""{
                    "status": ${OK.value()},
                    "message": "product-found",
                    "result": {
                        "sku": "${sku}",
                        "name": "${product.name}",
                        "slugName": "${product.slugName}",
                        "gtin": "${product.gtin}",
                        "organizationSlugName": "${organizationSlugName}",
                        "brandSlugName": "flor-de-frida",
                        "availableOnDemand": false,
                        "available": true
                    }
                }""")
    }

    def 'should not get product by slugName when organization slugName header is missing'() {
        given:
            def slugName = 'test-product-f12345'

        expect:
            this.client
                .get()
                .uri('/products/{slugName}', slugName)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .json("""{
                    "status": ${BAD_REQUEST.value()},
                    "message": "400 BAD_REQUEST \\"Missing request header 'x-organization' for method parameter of type String\\""
                }""")
    }

    def 'should not get product by slugName for wrong organization slugName'() {
        given:
            def slugName = 'test-product-f12345'
            def organizationSlugName = 'clube-da-fridinha'

        when:
            this.repository.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName) >> { s, o ->
                return Mono.empty()
            }

        then:
            this.client
                .get()
                .uri('/products/{slugName}', slugName)
                .accept(APPLICATION_JSON)
                .header('x-organization', organizationSlugName)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with slugName=test-product-f12345,organizationSlugName=clube-da-fridinha not found",
                    "reason": "product-not-found",
                    "reasonId": "test-product-f12345,clube-da-fridinha"
                }""")
    }

    def 'should not get product by slugName for not existing slugName'() {
        given:
            def slugName = 'unknown'
            def organizationSlugName = 'flor-de-frida'

        when:
            this.repository.findBySlugNameAndOrganizationSlugName(slugName, organizationSlugName) >> { s, o ->
                return Mono.empty()
            }

        then:
            this.client
                .get()
                .uri('/products/{slugName}', slugName)
                .accept(APPLICATION_JSON)
                .header('x-organization', organizationSlugName)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with slugName=unknown,organizationSlugName=flor-de-frida not found",
                    "reason": "product-not-found",
                    "reasonId": "unknown,flor-de-frida"
                }""")
    }

    def 'should delete product by sku'() {
        given:
            def sku = 'FDF123'
            def organizationSlugName = 'flor-de-frida'
            Product product = Fixture.from(Product).gimme(sku)

        when:
            this.repository.deleteBySkuAndOrganizationSlugName(sku, organizationSlugName) >> { s, o ->
                return Mono.just(product)
            }

        then:
            this.client
                .delete()
                .uri('/products/sku/{sku}', 'FDF123')
                .accept(APPLICATION_JSON)
                .header('x-organization', 'flor-de-frida')
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("""{
                    "status": ${OK.value()},
                    "message": "product-deleted",
                    "result": {
                        "sku": "${product.sku}",
                        "name": "${product.name}",
                        "slugName": "${product.slugName}",
                        "gtin": "${product.gtin}",
                        "organizationSlugName": "${product.organizationSlugName}",
                        "brandSlugName": "flor-de-frida",
                        "availableOnDemand": false,
                        "available": true
                    }
                }""")
    }

    def 'should not delete not existing product by sku'() {
        given:
            def sku = 'FDF234'
            def organizationSlugName = 'clube-da-fridinha'

        when:
            this.repository.deleteBySkuAndOrganizationSlugName(sku, organizationSlugName) >> { s, o ->
                return Mono.empty()
            }

        then:
            this.client
                .delete()
                .uri('/products/sku/{sku}', 'FDF234')
                .accept(APPLICATION_JSON)
                .header('x-organization', 'clube-da-fridinha')
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with sku=FDF234,organizationSlugName=clube-da-fridinha not found",
                    "reason": "product-not-found",
                    "reasonId": "FDF234,clube-da-fridinha"
                }""")
    }

    def 'should get products paginated by organizationSlugName'() {
        given:
            def organizationSlugName = 'flor-de-frida'
            Product product1 = Fixture.from(Product).gimme('FDF123')
            Product product2 = Fixture.from(Product).gimme('FDF234')
            Product product3 = Fixture.from(Product).gimme('FDF345')
            def products = [product1, product2, product3]
            def page = 1
            def size = 3
            def pageRequest = PageRequest.of(page, size)
            def expectedResults = this.objectMapper.writeValueAsString(products)

        when:
            this.repository.countAllByOrganizationSlugNameByPageRequest(organizationSlugName, pageRequest) >> { org, pr ->
                return Mono.just(products.size())
            }
            this.repository.findAllByOrganizationSlugNameByPageRequest(organizationSlugName, pageRequest) >> { org, pr ->
                return Flux.fromIterable(products)
            }

        then:
            this.client
                .get()
                .uri('/products/?page={page}&size={size}', ['page': page, 'size': size])
                .accept(APPLICATION_JSON)
                .header('x-organization', 'flor-de-frida')
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("""{
                    "status": ${OK.value()},
                    "message": "results-found",
                    "results": ${expectedResults},
                    "page": ${page},
                    "size": ${size},
                    "total": ${products.size()}
                }""")
    }

    def 'should not get products when count is zero'() {
        given:
            def organizationSlugName = 'flor-de-frida'
            def page = 1
            def size = 3
            def pageRequest = PageRequest.of(page, size)

        when:
            this.repository.countAllByOrganizationSlugNameByPageRequest(organizationSlugName, pageRequest) >> { org, pr ->
                return Mono.just(0L)
            }

        then:
            this.client
                .get()
                .uri('/products/?page={page}&size={size}', ['page': page, 'size': size])
                .accept(APPLICATION_JSON)
                .header('x-organization', 'flor-de-frida')
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .json("""{
                    "status": ${NOT_FOUND.value()},
                    "message": "Product with organizationSlugName=flor-de-frida not found",
                    "reason": "product-not-found",
                    "reasonId": "flor-de-frida"
                }""")
    }
}
