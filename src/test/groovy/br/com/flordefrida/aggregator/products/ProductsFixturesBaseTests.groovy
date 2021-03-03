package br.com.flordefrida.aggregator.products

import br.com.flordefrida.aggregator.AggregatorApplicationTests
import br.com.flordefrida.aggregator.products.fixtures.processors.ReactiveMongoProcessor
import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader
import org.springframework.beans.factory.annotation.Autowired

class ProductsFixturesBaseTests extends AggregatorApplicationTests {
    @Autowired
    protected ReactiveMongoProcessor templateProcessor

    def setupSpec() {
        FixtureFactoryLoader.loadTemplates("br.com.flordefrida.aggregator.products")
    }
}
