package br.com.flordefrida.aggregator.products.domain


import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@EqualsAndHashCode
@Document(collection = 'products')
@ToString(includePackage = false, includeNames = true)
@CompoundIndexes([
    @CompoundIndex(
        name = 'organization_slug_name_1_sku_1', def = '{ "organization.slugName": 1, sku: 1 }',
        unique = true
    ),
    @CompoundIndex(
        name = 'organization_slug_name_1_slugName_1', def = '{ "organization.slugName": 1, slugName: 1 }',
        unique = true
    ),
    @CompoundIndex(
        name = 'organization_slug_name_1_gtin_1', def = '{ "organization.slugName": 1, gtin: 1 }',
        unique = true
    ),
    @CompoundIndex(
        name = 'organization_slug_name_1_normalized_name_1', def = '{ "organization.slugName": 1, normalizedName: 1 }'
    ),
    @CompoundIndex(
        name = 'organization_slug_name_1_normalized_description_1', def = '{ "organization.slugName": 1, normalizedDescription: 1 }'
    )
])
class Product {
    String _id

    @NotBlank(message = 'invalid-sku')
    String sku

    @NotBlank(message = 'invalid-name')
    @Size(min = 5, max = 200, message = 'invalid-name-size')
    String name

    @NotBlank(message = 'invalid-slug-name')
    @Size(min = 5, max = 200, message = 'invalid-slug-name-size')
    String slugName

    @NotBlank(message = 'invalid-gtin')
    String gtin

    @NotBlank(message = 'invalid-organization-slug-name')
    String organizationSlugName

    UpdateInfo updateInfo

    CreationInfo creationInfo

    String brandSlugName

    @NotBlank(message = 'invalid-description')
    @Size(min = 10, max = 512, message = 'invalid-description-size')
    String description

    @NotNull(message = 'invalid-availability-on-demand')
    Boolean availableOnDemand

    @NotNull(message = 'invalid-availability')
    Boolean available

    List<String> images

    Map<String, Object> properties
}
