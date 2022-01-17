package eu.outerheaven.certmanager.agent.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class StandaloneCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    @ManyToOne(cascade = CascadeType.ALL)
    private Certificate certificate

    private String alias

    private String path

    private CertificateType certificateType

    private String password

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

    String getPath() {
        return path
    }

    void setPath(String path) {
        this.path = path
    }

    CertificateType getCertificateType() {
        return certificateType
    }

    void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }
}
