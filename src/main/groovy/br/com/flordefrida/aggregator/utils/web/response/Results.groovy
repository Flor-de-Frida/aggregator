package br.com.flordefrida.aggregator.utils.web.response

abstract class Results<T> {
    private Integer status
    private String message
    private List<T> results
    private int page
    private int size
    private int total

    Results(
            final Integer status,
            final String message,
            final List<T> results,
            final int page,
            final int size,
            final int total
    ) {
        this.status = status
        this.message = message
        this.results = results
        this.page = page
        this.size = size
        this.total = total
    }

    Integer getStatus() {
        return this.status
    }

    String getMessage() {
        return this.message
    }

    List<T> getResults() {
        return this.results
    }

    int getPage() {
        return this.page
    }

    int getSize() {
        return this.size
    }

    int getTotal() {
        return this.total
    }
}
