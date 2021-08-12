package eu.outerheaven.certmanager.agent.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import java.security.cert.X509Certificate

@Entity
class Certificate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    String alias

    @Column(length = 4000)
    X509Certificate x509Certificate


    Boolean managed

    @ManyToOne
    @JoinColumn(name="KEYSTORE_ID")
    Keystore keystore
}
