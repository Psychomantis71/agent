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
    Long id

    String hostname

    String ip

    Long port



}
