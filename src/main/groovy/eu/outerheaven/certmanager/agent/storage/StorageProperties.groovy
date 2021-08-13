package eu.outerheaven.certmanager.agent.storage

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "storage")
class StorageProperties {

    private String location

    String getLocation() {
        return location
    }

    void setLocation(String location) {
        this.location = location
    }

}
