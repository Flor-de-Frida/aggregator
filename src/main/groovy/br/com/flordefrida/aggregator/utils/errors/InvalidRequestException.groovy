package br.com.flordefrida.aggregator.utils.errors

import br.com.flordefrida.aggregator.utils.MapToString
import org.springframework.web.bind.annotation.ResponseStatus

import static org.springframework.http.HttpStatus.BAD_REQUEST

@ResponseStatus(BAD_REQUEST)
class InvalidRequestException extends BaseException {
    InvalidRequestException(final Map<String, String> params) {
        super(
            "Request with errors in fields ${MapToString.mapToString(params)}" as String,
            BAD_REQUEST.value(),
            'invalid-request',
            params.values().join(',')
        )
    }
}
