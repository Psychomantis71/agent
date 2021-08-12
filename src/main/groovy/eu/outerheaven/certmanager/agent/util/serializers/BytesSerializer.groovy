package eu.outerheaven.certmanager.agent.util.serializers

import static com.fasterxml.jackson.core.Base64Variants.MODIFIED_FOR_URL;

import java.io.IOException;

import eu.outerheaven.certmanager.agent.util.Bytes

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class BytesSerializer extends JsonSerializer<Bytes> {

    @Override
    void serialize(Bytes value,
                          JsonGenerator generator,
                          SerializerProvider provider)
            throws IOException, JsonProcessingException {
        serializeBytes(value.getBytes(), generator, MODIFIED_FOR_URL);
    }

    @Override
    Class<Bytes> handledType() {
        return Bytes.class;
    }

    /*
     * Jackson does not seem to honor the call below, somehow...
     * generator.writeBinary(Base64Variants.MIME_NO_LINEFEEDS, encoded, 0, encoded.length);
     * Manually encode base64 and write as string!
     */
    protected static final void serializeBytes(byte[] value,
                                               JsonGenerator generator,
                                               Base64Variant variant)
            throws IOException {
        generator.writeString(variant.encode(value));
    }

}