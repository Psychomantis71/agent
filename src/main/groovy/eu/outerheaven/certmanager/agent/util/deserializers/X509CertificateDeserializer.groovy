package eu.outerheaven.certmanager.agent.util.deserializers

import static com.fasterxml.jackson.core.Base64Variants.MIME_NO_LINEFEEDS;
import eu.outerheaven.certmanager.agent.util.deserializers.BytesDeserializer


import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

class X509CertificateDeserializer extends JsonDeserializer<X509Certificate> {

    private static final CertificateFactory certificateFactory;
    static {
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException exception) {
            throw new IllegalStateException("Unable to access X.509 certificate factory");
        }
    }

    @Override
    X509Certificate deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        final byte[] data = BytesDeserializer.deserializeBytes(parser, MIME_NO_LINEFEEDS);
        final ByteArrayInputStream input = new ByteArrayInputStream(data);
        try {
            return (X509Certificate) certificateFactory.generateCertificate(input);
        } catch (CertificateException exception) {
            throw new JsonMappingException("Unable to parse X509 certificate", exception);
        }
    }

    @Override
    Class<X509Certificate> handledType() {
        return X509Certificate.class;
    }

}
