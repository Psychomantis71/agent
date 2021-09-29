package eu.outerheaven.certmanager.agent.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class Instance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    private String hostname

    private String ip

    private Long port

    private Boolean adopted


    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getHostname() {
        return hostname
    }

    void setHostname(String hostname) {
        this.hostname = hostname
    }

    String getIp() {
        return ip
    }

    void setIp(String ip) {
        this.ip = ip
    }

    Long getPort() {
        return port
    }

    void setPort(Long port) {
        this.port = port
    }

    Boolean getAdopted() {
        return adopted
    }

    void setAdopted(Boolean adopted) {
        this.adopted = adopted
    }
}
