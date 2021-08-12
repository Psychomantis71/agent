package eu.outerheaven.certmanager.agent.config.security

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import eu.outerheaven.certmanager.agent.util.serializers.X509CertificateSerializer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

import java.security.cert.X509Certificate;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({JacksonAutoConfiguration.class})
class CustomCertConfig{

    /**
     * Used for parsing temporal data which is received from mpsi endpoints in specific format.
     */
    @Bean
    Jackson2ObjectMapperBuilderCustomizer customizer()
    {
        return new Jackson2ObjectMapperBuilderCustomizer()
                {
                    @Override
                    void customize(Jackson2ObjectMapperBuilder builder)
                    {
                        builder.serializerByType(X509Certificate.class,
                                new X509CertificateSerializer()<X509Certificate>())

                        //affects to all dates in all pojos (I hope :) )

                        //builder.indentOutput(true).dateFormat(new SimpleDateFormat
                        //       ("yyyy-MM-dd'T'HH:mm:ssXXX"));

                    }
                };
    }

}

