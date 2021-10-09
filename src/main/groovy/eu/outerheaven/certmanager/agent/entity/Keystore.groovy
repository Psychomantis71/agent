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
    private List<Certificate> certificates

    private Boolean managed

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

    List<Certificate> getCertificates() {
        return certificates
    }

    void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates
    }

    Boolean getManaged() {
        return managed
    }

    void setManaged(Boolean managed) {
        this.managed = managed
    }
}
