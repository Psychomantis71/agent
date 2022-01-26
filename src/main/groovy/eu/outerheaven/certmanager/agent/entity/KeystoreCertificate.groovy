package eu.outerheaven.certmanager.agent.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class KeystoreCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate certificate

    private String alias

    private Long keystoreId

    private boolean keypair

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    Certificate getCertificate() {
        return certificate
    }

    void setCertificate(Certificate certificate) {
        this.certificate = certificate
    }

    String getAlias() {
        return alias
    }

    void setAlias(String alias) {
        this.alias = alias
    }

    Long getKeystoreId() {
        return keystoreId
    }

    void setKeystoreId(Long keystoreId) {
        this.keystoreId = keystoreId
    }

    boolean getKeypair() {
        return keypair
    }

    void setKeypair(boolean keypair) {
        this.keypair = keypair
    }
}
