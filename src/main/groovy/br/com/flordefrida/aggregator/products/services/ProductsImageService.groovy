package br.com.flordefrida.aggregator.products.services

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.flordefrida.aggregator.products.errors.notfound.impl.ProductWithSkuNotFoundException
import br.com.flordefrida.aggregator.products.repositories.ProductsRepository
import com.cloudinary.Cloudinary
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Slf4j
@Service
class ProductsImageService {
    private static final String IMAGE_EXTENSION = 'jpg'

    private final ProductsRepository productsRepository
    private final Cloudinary cloudinary
    private String imagesBaseLocation

    ProductsImageService(
        final ProductsRepository productsRepository,
        final Cloudinary cloudinary,
        @Value('${products-images-location}') final String imagesBaseLocation
    ) {
        this.productsRepository = productsRepository
        this.cloudinary = cloudinary
        this.imagesBaseLocation = imagesBaseLocation
    }

    Mono<Product> uploadImage(final String sku, final String organizationSlugName, final FilePart image) {
        return this.productsRepository
            .findBySkuAndOrganizationSlugName(sku, organizationSlugName)
            .switchIfEmpty(Mono.defer {
                Mono.error(new ProductWithSkuNotFoundException(sku, organizationSlugName))
            } as Mono<Product>)
            .flatMap { product ->
                return this.sendToImageRepository(sku, organizationSlugName, image)
                    .flatMap { imageUrl ->
                        return updateProductWithImage(imageUrl, product)
                    }
            }
    }

    Mono<Product> deleteImageByName(final String sku, final String organizationSlugName, final String imageName) {
        return this.productsRepository
            .findBySkuAndOrganizationSlugName(sku, organizationSlugName)
            .switchIfEmpty(Mono.defer {
                Mono.error(new ProductWithSkuNotFoundException(sku, organizationSlugName))
            } as Mono<Product>)
            .flatMap { product ->
                product.images = product.images?.findAll { imageUrl -> !imageUrl.contains(imageName) }
                return this.productsRepository.save(product)
            }
            .map { updatedProduct ->
                String filePath = "${this.imagesBaseLocation}/${organizationSlugName}/${sku}/${imageName}"
                this.cloudinary.api().deleteResources([filePath], null)

                return updatedProduct
            }
    }

    Product deleteAllProductImages(final Product product) {
        final String sku = product.sku
        final String organizationSlugName = product.organizationSlugName

        final List<String> images = !product.images ? [] : product.images.collect { imageUrl ->
            String[] imageExplodedLocation = imageUrl.split('/')
            String imageName = imageExplodedLocation[imageExplodedLocation.length - 1]
                .replace(".${IMAGE_EXTENSION}", '')
            return "${this.imagesBaseLocation}/${organizationSlugName}/${sku}/${imageName}" as String
        }

        if (images)
            this.cloudinary.api().deleteResources(images, null)

        return product
    }

    private Mono<Product> updateProductWithImage(String imageUrl, Product product) {
        if (imageUrl) {
            List<String> images = (product.images ?: [])
            images.add(imageUrl)

            product.images = images

            return this.productsRepository.save(product)
        }

        return Mono.just(product)
    }

    private Mono<String> sendToImageRepository(final String sku, final String organizationSlugName, final FilePart image) {
        String fileName = "${System.currentTimeMillis()}"

        String filePath = "${this.imagesBaseLocation}/${organizationSlugName}/${sku}/${fileName}"

        final Map<String, Object> params = [
            "public_id"    : filePath,
            "overwrite"    : true,
            "resource_type": "image"
        ] as Map<String, Object>

        return getFileFromRequest(fileName, image).map { file ->
            def uploadResult = this.cloudinary.uploader().upload(file, params)
            return uploadResult['secure_url'] as String
        }
    }

    private static Mono<File> getFileFromRequest(final String fileName, final FilePart image) {
        try {
            File file = File.createTempFile(fileName, IMAGE_EXTENSION)
            return image.transferTo(file).thenReturn(file)
        } catch (Exception ex) {
            log.error("error trying to get image from request. error={}", ex.getMessage(), ex)

            return Mono.empty()
        }
    }
}
