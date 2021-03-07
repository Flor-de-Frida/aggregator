package br.com.flordefrida.aggregator.products.controllers


import br.com.flordefrida.aggregator.products.responses.ProductImageDeletedResult
import br.com.flordefrida.aggregator.products.responses.ProductImageUploadedResult
import br.com.flordefrida.aggregator.products.services.ProductsImageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping('/products/images')
class ProductsImagesController {
    @Autowired
    private ProductsImageService imageService

    @PutMapping('/{sku}')
    Mono<ProductImageUploadedResult> uploadImage(
        @PathVariable final String sku,
        @RequestPart Mono<FilePart> image,
        @RequestHeader(name = 'x-organization', required = true) final String organizationSlugName
    ) {
        return image.flatMap {
            this.imageService
                .uploadImage(sku, organizationSlugName, it)
                .map(ProductImageUploadedResult.&new)
        }
    }

    @DeleteMapping('/{sku}/image/{imageName}')
    Mono<ProductImageDeletedResult> deleteImage(
        @PathVariable final String sku,
        @PathVariable final String imageName,
        @RequestHeader(name = 'x-organization', required = true) final String organizationSlugName
    ) {
        return this.imageService
            .deleteImageByName(sku, organizationSlugName, imageName)
            .map(ProductImageDeletedResult.&new)
    }
}
