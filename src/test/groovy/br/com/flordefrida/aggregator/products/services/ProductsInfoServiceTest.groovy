package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.services.ProductsInfoService
import br.com.flordefrida.aggregator.AggregatorApplicationTests
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

import java.time.LocalDateTime
import java.time.ZoneId

class ProductsInfoServiceTest extends AggregatorApplicationTests {
    @Value('${info.default-author}')
    String defaultAuthor

    @Autowired
    ProductsInfoService service

    def 'should generate update info for default author'() {
        given:
            def author = null
            def time = LocalDateTime.now(ZoneId.of("UTC"))

        when:
            def info = this.service.generateUpdateInfo(author)

        then:
            info.author == this.defaultAuthor
            info.moment.isAfter(time)
    }

    def 'should generate update info for informed author'() {
        given:
            def author = 'test@flordefrida.com.br'
            def time = LocalDateTime.now(ZoneId.of("UTC"))

        when:
            def info = this.service.generateUpdateInfo(author)

        then:
            info.author == author
            info.moment.isAfter(time)
    }

    def 'should generate creation info from update info'() {
        given:
            def updateInfo = this.service.generateUpdateInfo(this.defaultAuthor)

        when:
            def creationInfo = this.service.generateCreationInfo(updateInfo)

        then:
            creationInfo.moment == updateInfo.moment
            creationInfo.author == updateInfo.author
    }

    def 'should generate new creation info from invalid update info'() {
        given:
            def updateInfo = null
            def time = LocalDateTime.now(ZoneId.of("UTC"))

        when:
            def creationInfo = this.service.generateCreationInfo(updateInfo)

        then:
            creationInfo.moment.isAfter(time)
            creationInfo.author == this.defaultAuthor
    }
}
