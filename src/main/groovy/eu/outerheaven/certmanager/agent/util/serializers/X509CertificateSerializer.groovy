package eu.outerheaven.certmanager.agent.util.serializers

import static com.fasterxml.jackson.core.Base64Variants.MIME_NO_LINEFEEDS;
import eu.outerheaven.certmanager.agent.util.serializers.BytesSerializer


import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class X509CertificateSerializer extends JsonSerializer<X509Certificate> {

    @Override
    void serialize(X509Certificate value,
                          JsonGenerator generator,
                          SerializerProvider provider)
            throws IOException, JsonProcessingException {
        try {
            BytesSerializer.serializeBytes(value.getEncoded(), generator, MIME_NO_LINEFEEDS);
        } catch (CertificateEncodingException exception) {
            throw new JsonMappingException("Unable to serialize X509 certificate", exception);
        }
    }

    @Override
    Class<X509Certificate> handledType() {
        return X509Certificate.class;
    }

}
