package br.com.flordefrida.aggregator.products.fixtures.processors

import br.com.six2six.fixturefactory.processor.Processor
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.stereotype.Component

import java.lang.annotation.Annotation

@Component
@Profile('test')
class ReactiveMongoProcessor implements Processor {
    private final ReactiveMongoOperations operations

    ReactiveMongoProcessor(final ReactiveMongoOperations operations) {
        this.operations = operations
    }

    @Override
    void execute(final Object result) {
        if (isAnnotationPresent(result, Document)) {
            operations.save(result).block()
        }
    }

    private static boolean isAnnotationPresent(
        final Object object,
        final Class<? extends Annotation> annotationClass
    ) {
        return object?.getClass()?.isAnnotationPresent(annotationClass)
    }
}
