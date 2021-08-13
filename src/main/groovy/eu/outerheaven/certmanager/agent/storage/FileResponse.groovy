package eu.outerheaven.certmanager.agent.storage

class FileResponse {
    private String name
    private String uri
    private String type
    private long size

    FileResponse(String name, String uri, String type, long size) {
        this.name = name
        this.uri = uri
        this.type = type
        this.size = size
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getUri() {
        return uri
    }

    void setUri(String uri) {
        this.uri = uri
    }

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    long getSize() {
        return size
    }

    void setSize(long size) {
        this.size = size
    }
}
