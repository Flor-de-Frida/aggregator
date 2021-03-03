package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.services.ProductsValidationService
import br.com.flordefrida.aggregator.products.ProductsFixturesBaseTests
import br.com.flordefrida.aggregator.products.domain.Product
import br.com.six2six.fixturefactory.Fixture
import io.github.opensanca.exception.ServiceValidationException
import org.springframework.beans.factory.annotation.Autowired

class ProductsValidationServiceTest extends ProductsFixturesBaseTests {
    @Autowired
    ProductsValidationService validationService

    def 'should not throw error if product is valid'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            validatedProduct
    }

    def 'should throw error if product is invalid'() {
        given:
            Product product = new Product()

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 8
            exception.errors.get('Product.name') == ['invalid-name']
            exception.errors.get('Product.organizationSlugName') == ['invalid-organization-slug-name']
            exception.errors.get('Product.gtin') == ['invalid-gtin']
            exception.errors.get('Product.slugName') == ['invalid-slug-name']
            exception.errors.get('Product.sku') == ['invalid-sku']
            exception.errors.get('Product.description') == ['invalid-description']
            exception.errors.get('Product.available') == ['invalid-availability']
            exception.errors.get('Product.availableOnDemand') == ['invalid-availability-on-demand']
    }

    def 'should throw error if product name is smaller than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.name = generateRandomString(4)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.name') == ['invalid-name-size']
    }

    def 'should throw error if product name is larger than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.name = generateRandomString(201)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.name') == ['invalid-name-size']
    }

    def 'should throw error if product slugName is smaller than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.slugName = generateRandomString(4)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.slugName') == ['invalid-slug-name-size']
    }

    def 'should throw error if product slugName is larger than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.slugName = generateRandomString(201)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.slugName') == ['invalid-slug-name-size']
    }

    def 'should throw error if product description is smaller than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.description = generateRandomString(9)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.description') == ['invalid-description-size']
    }

    def 'should throw error if product description is larger than limit'() {
        given:
            Product product = Fixture.from(Product).gimme('FDF123')

        and:
            product.description = generateRandomString(513)

        when:
            def validatedProduct = this.validationService.validate(product)

        then:
            !validatedProduct
            ServiceValidationException exception = thrown()
            exception
            exception.errors.size() == 1
            exception.errors.get('Product.description') == ['invalid-description-size']
    }

    private static String generateRandomString(int length) {
        StringBuilder builder = new StringBuilder()
        length.times {
            builder.append('a')
        }

        return builder.toString()
    }
}
