package eu.outerheaven.certmanager.agent.util


final class Bytes {

    private final byte[] bytes

    Bytes(byte[] bytes) {
        if (bytes == null) throw new NullPointerException("Null bytes");
        this.bytes = bytes;
    }

    byte[] getBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    int length() {
        return bytes.length;
    }

    @Override
    int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    boolean equals(Object object) {
        if (object == null) return false;
        if (object == this) return true;
        try {
            return Arrays.equals(bytes, ((Bytes)object).bytes);
        } catch (ClassCastException exception) {
            return false;
        }
    }
}
