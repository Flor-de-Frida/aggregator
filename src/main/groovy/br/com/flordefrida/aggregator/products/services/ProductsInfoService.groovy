package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.domain.CreationInfo
import br.com.flordefrida.aggregator.products.domain.UpdateInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import java.time.LocalDateTime
import java.time.ZoneId

@Service
class ProductsInfoService {
    private final String DEFAULT_AUTHOR

    ProductsInfoService(@Value('${info.default-author}') final String defaultAuthor) {
        this.DEFAULT_AUTHOR = defaultAuthor
    }

    CreationInfo generateCreationInfo(UpdateInfo updateInfo) {
        return new CreationInfo(
            moment: updateInfo?.moment ?: LocalDateTime.now(ZoneId.of("UTC")),
            author: updateInfo?.author ?: DEFAULT_AUTHOR
        )
    }

    UpdateInfo generateUpdateInfo(final String author) {
        final String infoAuthor = author ?: DEFAULT_AUTHOR

        return new UpdateInfo(
            moment: LocalDateTime.now(ZoneId.of("UTC")),
            author: infoAuthor
        )
    }
}
