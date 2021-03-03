package br.com.flordefrida.aggregator.utils.errors.handlers

import br.com.flordefrida.aggregator.utils.errors.BaseException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options)
        Throwable error = getError(request)

        if (!(error instanceof BaseException)) {
            errorAttributes.put('message', error.message)
            return errorAttributes
        }

        errorAttributes.put('message', (error as BaseException).message)
        errorAttributes.put('reason', (error as BaseException).reason)
        errorAttributes.put('reasonId', (error as BaseException).reasonId)

        return errorAttributes
    }

}
