package br.com.flordefrida.aggregator.utils.web.request

import br.com.flordefrida.aggregator.utils.errors.InvalidRequestException
import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
class PageRequest {
    private int page = 1
    private int size = 10
    private Set<String> fl = Set.of('*')

    private PageRequest(final int page, final int size) {
        this.page = page
        this.size = size
    }

    static PageRequest of(final int page, final int size) {
        def errors = [:] as Map<String, String>

        if (page <= 0)
            errors.put('invalid-page-value', page as String)

        if (size <= 0)
            errors.put('invalid-size-value', size as String)

        if (!errors.isEmpty())
            throw new InvalidRequestException(errors)

        return new PageRequest(page, size)
    }

    PageRequest withFieldList(final String fl) {
        if (fl != null) {
            this.fl = fl.split(',') as Set<String>
        }

        return this
    }

    int getPage() {
        return this.page
    }

    int getSize() {
        return this.size
    }

    Set<String> getFieldList() {
        return this.fl
    }

    @Override
    boolean equals(Object obj) {
        if (obj == null) return false
        if (getClass() != obj.getClass()) return false

        final PageRequest other = (PageRequest) obj

        return Objects.equals(this.page, other.page)
            && Objects.equals(this.size, other.size)
            && Objects.equals(this.fieldList, other.fieldList)
    }

    @Override
    int hashCode() {
        return Objects.hash(this.page, this.size, this.fieldList)
    }
}
