package eu.outerheaven.certmanager.agent.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import java.security.PrivateKey
import java.security.cert.X509Certificate

@Entity
class Certificate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @Column(length = 8192)
    private PrivateKey key

    @Column(length = 4000)
    private X509Certificate x509Certificate

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    PrivateKey getKey() {
        return key
    }

    void setKey(PrivateKey key) {
        this.key = key
    }

    X509Certificate getX509Certificate() {
        return x509Certificate
    }

    void setX509Certificate(X509Certificate x509Certificate) {
        this.x509Certificate = x509Certificate
    }
}
