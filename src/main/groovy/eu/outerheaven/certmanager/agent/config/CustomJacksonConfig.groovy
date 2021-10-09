package eu.outerheaven.certmanager.agent.config

import eu.outerheaven.certmanager.agent.util.deserializers.X509CertificateDeserializer
import eu.outerheaven.certmanager.agent.util.serializers.X509CertificateSerializer
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.security.cert.X509Certificate


@Configuration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
class CustomJacksonConfig {

    @Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperBuilderCustomizer() {
        return (jacksonObjectMapperBuilder) -> {
            jacksonObjectMapperBuilder.serializerByType(X509Certificate.class, new X509CertificateSerializer())
            jacksonObjectMapperBuilder.deserializerByType(X509Certificate.class, new X509CertificateDeserializer())
        }
    }
}