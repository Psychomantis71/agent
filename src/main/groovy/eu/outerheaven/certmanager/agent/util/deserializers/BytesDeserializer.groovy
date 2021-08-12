package eu.outerheaven.certmanager.agent.util.deserializers

import static com.fasterxml.jackson.core.Base64Variants.MODIFIED_FOR_URL;

import java.io.IOException;

import eu.outerheaven.certmanager.agent.util.Bytes

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

 class BytesDeserializer extends JsonDeserializer<Bytes> {

    @Override
    public Bytes deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        return new Bytes(deserializeBytes(parser, MODIFIED_FOR_URL));
    }

    @Override
    public Class<byte[]> handledType() {
        return byte[].class;
    }

    /*
     * Jackson does not seem to honor the call below, somehow...
     * final byte[] data = parser.getBinaryValue(Base64Variants.MIME_NO_LINEFEEDS);
     * Manually read a string and dencode base64
     */
    protected static final byte[] deserializeBytes(JsonParser parser, Base64Variant variant)
            throws IOException {
        return variant.decode(parser.getText());
    }
}
