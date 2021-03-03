package br.com.flordefrida.aggregator.utils

class MapToString {
    private MapToString() {}

    static String mapToString(final Map<String, String> data) {
        return !data ? '' : data.entrySet().collect { entry ->
            return "${entry.key}=${entry.value}"
        }.join(',')
    }

    static String mapListToString(final Map<String, List<String>> data) {
        return !data ? '' : data.entrySet().collect { entry ->
            if (!entry.value)
                return null
            return "${entry.key}=${entry.value.join(',')}"
        }.findAll().join(';')
    }
}
