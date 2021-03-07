package br.com.flordefrida.aggregator.products.config

import com.cloudinary.Cloudinary
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CloudinaryConfig {
    private static Cloudinary cloudinary

    @Bean
    Cloudinary cloudinary() {
        if (!cloudinary)
            cloudinary = new Cloudinary()

        return cloudinary
    }
}
