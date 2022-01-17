package eu.outerheaven.certmanager.agent.entity



import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany


@Entity
class Keystore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id

    private String location

    private String description

    private String password

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "keystoreId")
    private List<KeystoreCertificate> keystoreCertificates

    Long getId() {
        return id
    }

    void setId(Long id) {
        this.id = id
    }

    String getLocation() {
        return location
    }

    void setLocation(String location) {
        this.location = location
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    List<KeystoreCertificate> getKeystoreCertificates() {
        return keystoreCertificates
    }

    void setKeystoreCertificates(List<KeystoreCertificate> keystoreCertificates) {
        this.keystoreCertificates = keystoreCertificates
    }
}
