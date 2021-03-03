package br.com.flordefrida.aggregator.utils.web.response.impl


import br.com.flordefrida.aggregator.utils.web.response.Results

import static org.springframework.http.HttpStatus.OK

class FoundResults<T> extends Results<T> {
    FoundResults(
            final List<T> results,
            final int page,
            final int size,
            final int total
    ) {
        super(OK.value(), 'results-found', results, page, size, total)
    }
}
