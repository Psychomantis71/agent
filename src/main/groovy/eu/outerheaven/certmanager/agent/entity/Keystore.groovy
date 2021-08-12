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
    Long id

    String location

    String description

    String password

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "keystore")
    List<Certificate> certificates

    Boolean managed
}
