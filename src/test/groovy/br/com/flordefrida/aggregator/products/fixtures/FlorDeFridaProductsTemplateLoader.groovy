package br.com.flordefrida.aggregator.products.fixtures

import br.com.flordefrida.aggregator.products.domain.Product
import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.six2six.fixturefactory.loader.TemplateLoader

class FlorDeFridaProductsTemplateLoader implements TemplateLoader {
    @Override
    void load() {
        Fixture.of(Product).addTemplate('FDF123', new Rule() {
            {
                add('sku', 'FDF123')
                add('name', 'Flor de Frida Test Product FDF123')
                add('slugName', 'flor-de-frida-test-product-fdf123')
                add('gtin', 'ABC-DEF-GHI')
                add('organizationSlugName', 'flor-de-frida')
                add('brandSlugName', 'flor-de-frida')
                add('description', 'A test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })

        Fixture.of(Product).addTemplate('FDF234', new Rule() {
            {
                add('sku', 'FDF234')
                add('name', 'Flor de Frida Test Product FDF234')
                add('slugName', 'flor-de-frida-test-product-fdf234')
                add('gtin', 'BCD-EFG-HIJ')
                add('organizationSlugName', 'flor-de-frida')
                add('brandSlugName', 'flor-de-frida-infantil')
                add('description', 'Other test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })

        Fixture.of(Product).addTemplate('FDF345', new Rule() {
            {
                add('sku', 'FDF345')
                add('name', 'Flor de Frida Test Product FDF345')
                add('slugName', 'flor-de-frida-test-product-fdf345')
                add('gtin', 'CDE-FGH-IJK')
                add('organizationSlugName', 'flor-de-frida')
                add('brandSlugName', 'flor-de-frida')
                add('description', 'Other test product')
                add('availableOnDemand', false)
                add('available', true)
            }
        })
    }
}
