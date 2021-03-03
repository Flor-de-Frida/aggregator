package br.com.flordefrida.aggregator.products.fixtures

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader

class ClubeDaFridinhaProductsTemplateLoader implements TemplateLoader {
    @Override
    void load() {
        Fixture.of(Product).addTemplate('CDF123', new Rule() {
            {
                add('sku', 'CDF123')
                add('name', 'Clube da Fridinha Test Product CDF123')
                add('slugName', 'clube-da-fridinha-test-product-cdf123')
                add('gtin', 'ABC-DEF-GHI')
                add('organizationSlugName', 'clube-da-fridinha')
                add('brandSlugName', 'clube-da-fridinha')
                add('description', 'A test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })

        Fixture.of(Product).addTemplate('CDF234', new Rule() {
            {
                add('sku', 'CDF234')
                add('name', 'Clube da Fridinha Test Product CDF234')
                add('slugName', 'clube-da-fridinha-test-product-cdf234')
                add('gtin', 'BCD-EFG-HIJ')
                add('organizationSlugName', 'clube-da-fridinha')
                add('brandSlugName', 'clube-da-fridinha-grandinhas')
                add('description', 'Other test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })

        Fixture.of(Product).addTemplate('CDF345', new Rule() {
            {
                add('sku', 'CDF345')
                add('name', 'Clube da Fridinha Test Product CDF345')
                add('slugName', 'clube-da-fridinha-test-product-cdf345')
                add('gtin', 'CDE-FGH-IJK')
                add('organizationSlugName', 'clube-da-fridinha')
                add('brandSlugName', 'clube-da-fridinha')
                add('description', 'Other test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })
    }
}
