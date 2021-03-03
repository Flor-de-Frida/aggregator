package br.com.flordefrida.aggregator.utils


import static java.text.Normalizer.Form.NFD
import static java.text.Normalizer.normalize

class SlugNameFactory {
    private SlugNameFactory() {}

    static String make(final String value) {

        if (value == null || value.trim() == "")
            return ""

        final String slugValue = value
            .toLowerCase()
            .replaceAll('\\.', '')
            .replaceAll('\\+', '')
            .replaceAll(',', '-')
            .replaceAll('&', 'e')
            .replaceAll('\\$', 's')
            .replaceAll('(\\s+|_+)', '-')
            .replaceAll('(^-+|-+$)', '')
            .replaceAll('[^a-z\\d\\-]i', '')
            .replaceAll('-+', '-')
            .replaceAll('[áàâã]', 'a')
            .replaceAll('[éèêẽ]', 'e')
            .replaceAll('[ìíîĩ]', 'i')
            .replaceAll('[òóôõ]', 'o')
            .replaceAll('[ùúûũ]', 'u')
            .replaceAll('[ḉçć]', 'c')
            .replaceAll('[^\\p{ASCII}]', '')
            .replaceAll('[()]', '')

        final String normalizedValue = normalize(slugValue, NFD)

        return normalizedValue
    }
}
