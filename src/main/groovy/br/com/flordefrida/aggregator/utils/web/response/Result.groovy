package br.com.flordefrida.aggregator.utils.web.response

abstract class Result<T> {
    private Integer status
    private String message
    private T result

    Result(final Integer status, final String message, final T result) {
        this.status = status
        this.message = message
        this.result = result
    }

    Integer getStatus() {
        return this.status
    }

    String getMessage() {
        return this.message
    }

    T getResult() {
        return this.result
    }
}
