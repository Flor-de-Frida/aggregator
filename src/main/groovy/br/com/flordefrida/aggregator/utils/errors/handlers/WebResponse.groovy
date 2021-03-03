package br.com.flordefrida.aggregator.utils.errors.handlers


import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Order(-2)
@Component
class WebResponse extends AbstractErrorWebExceptionHandler {
    WebResponse(
        ErrorAttributes errorAttributes,
        WebProperties.Resources resourceProperties,
        ApplicationContext applicationContext,
        ServerCodecConfigurer config
    ) {
        super(errorAttributes, resourceProperties, applicationContext)
        this.setMessageWriters(config.writers)
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(
            RequestPredicates.all(), this.&renderErrorResponse)
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> errorPropertiesMap = this.getErrorAttributes(request, ErrorAttributeOptions.defaults())

        HttpStatus status = HttpStatus.valueOf(errorPropertiesMap.getOrDefault('status', '400') as Integer)

        return ServerResponse.status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(errorPropertiesMap))
    }
}
