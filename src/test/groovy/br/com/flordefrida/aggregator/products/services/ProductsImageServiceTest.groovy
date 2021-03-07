package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import com.cloudinary.Api
import com.cloudinary.Cloudinary
import com.cloudinary.Uploader
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class ProductsImageServiceTest extends Specification {
    private ProductsRepository productsRepositoryMock = Mock(ProductsRepository)
    private Cloudinary cloudinaryMock = Mock(Cloudinary)
    private String imagesBaseLocation = 'test/images'

    private ProductsImageService imageService

    void setup() {
        this.imageService = new ProductsImageService(
            this.productsRepositoryMock,
            this.cloudinaryMock,
            this.imagesBaseLocation
        )
    }

    def 'should upload image when find product by sku and organization'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'
            FilePart image = Mock(FilePart)
            Uploader uploader = Mock(Uploader)
            String imageUrl = "${this.imagesBaseLocation}/${sku}/${System.currentTimeMillis()}.jpg"

        and:
            image.transferTo(_ as File) >> Mono.empty()
            this.cloudinaryMock.uploader() >> uploader
            uploader.upload(_ as File, _ as Map<String, Object>) >> [
                'secure_url': imageUrl
            ]
            this.productsRepositoryMock.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> Mono.just(new Product(
                sku: sku,
                organizationSlugName: organizationSlugName
            ))
            this.productsRepositoryMock.save(_ as Product) >> { Product product ->
                return Mono.just(product)
            }

        when:
            def uploadImage = this.imageService.uploadImage(sku, organizationSlugName, image)

        then:
            StepVerifier
                .create(uploadImage)
                .expectNextMatches { product ->
                    return (product.sku == sku) &&
                        (product.organizationSlugName == organizationSlugName) &&
                        (product.images == [imageUrl])
                }
                .verifyComplete()
    }

    def 'should not upload image when product is not found by sku and organization'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'
            FilePart image = Mock(FilePart)

        and:
            this.productsRepositoryMock.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> Mono.empty()

        when:
            def uploadImage = this.imageService.uploadImage(sku, organizationSlugName, image)

        then:
            StepVerifier
                .create(uploadImage)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuNotFoundException'
                    def errorMessageMatches = error.message == 'Product with sku=FDF123456,organizationSlugName=flor-de-frida not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should delete product image by image name when find product by sku and organization'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'
            String image1Url = "${this.imagesBaseLocation}/${sku}/image1.jpg"
            String image2Url = "${this.imagesBaseLocation}/${sku}/image2.jpg"
            Api apiMock = Mock(Api)

        and:
            this.cloudinaryMock.api() >> apiMock
            this.productsRepositoryMock.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> Mono.just(new Product(
                sku: sku,
                organizationSlugName: organizationSlugName,
                images: [image1Url, image2Url]
            ))
            this.productsRepositoryMock.save(_ as Product) >> { Product product ->
                return Mono.just(product)
            }

        when:
            def deleteProductImage = this.imageService.deleteImageByName(sku, organizationSlugName, 'image1')

        then:
            StepVerifier
                .create(deleteProductImage)
                .expectNextMatches { product ->
                    return (product.sku == sku) &&
                        (product.organizationSlugName == organizationSlugName) &&
                        (product.images == [image2Url])
                }
                .verifyComplete()
    }

    def 'should not delete product image when product is not found by sku and organization'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'

        and:
            this.productsRepositoryMock.findBySkuAndOrganizationSlugName(sku, organizationSlugName) >> Mono.empty()

        when:
            def deleteImage = this.imageService.deleteImageByName(sku, organizationSlugName, 'image1')

        then:
            StepVerifier
                .create(deleteImage)
                .expectErrorMatches { error ->
                    def expectedExceptionMatches = error.class.simpleName == 'ProductWithSkuNotFoundException'
                    def errorMessageMatches = error.message == 'Product with sku=FDF123456,organizationSlugName=flor-de-frida not found'

                    return expectedExceptionMatches && errorMessageMatches
                }
                .verify()
    }

    def 'should delete all product images'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'
            String image1Url = "${this.imagesBaseLocation}/${organizationSlugName}/${sku}/image1.jpg"
            String image2Url = "${this.imagesBaseLocation}/${organizationSlugName}/${sku}/image2.jpg"
            List<String> images = [image1Url, image2Url]
            List<String> resources = images.collect {
                return it.replace('.jpg', '')
            }
            Product product = new Product(sku: sku, organizationSlugName: organizationSlugName, images: images)
            Api apiMock = Mock(Api)
            this.cloudinaryMock.api() >> apiMock

        when:
            this.imageService.deleteAllProductImages(product)

        then:
            1 * apiMock.deleteResources(resources, null)
    }

    def 'should not send delete command on product deletion if it has not images'() {
        given:
            def sku = 'FDF123456'
            def organizationSlugName = 'flor-de-frida'
            Product product = new Product(sku: sku, organizationSlugName: organizationSlugName)
            Api apiMock = Mock(Api)
            this.cloudinaryMock.api() >> apiMock

        when:
            this.imageService.deleteAllProductImages(product)

        then:
            0 * apiMock.deleteResources(_ as List<String>, null)
    }
}
