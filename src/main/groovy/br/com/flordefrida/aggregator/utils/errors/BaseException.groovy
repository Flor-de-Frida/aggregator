package br.com.flordefrida.aggregator.utils.errors

abstract class BaseException extends RuntimeException {
    final String message
    final Integer status
    final String reason
    final String reasonId

    BaseException(final String message, final Integer status, final String reason, final String reasonId) {
        super(message)

        this.message = message
        this.status = status
        this.reason = reason
        this.reasonId = reasonId
    }
}
