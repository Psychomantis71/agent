package eu.outerheaven.certmanager.agent.storage

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class PayloadLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    private String name

    private String location

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getLocation() {
        return location
    }

    void setLocation(String location) {
        this.location = location
    }
}
