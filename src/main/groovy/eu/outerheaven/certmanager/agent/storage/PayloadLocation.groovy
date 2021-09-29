package eu.outerheaven.certmanager.agent.storage

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class PayloadLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id

    private String name

    private URI location
}
