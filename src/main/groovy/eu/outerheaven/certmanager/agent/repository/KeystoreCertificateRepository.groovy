package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import org.springframework.data.repository.CrudRepository

interface KeystoreCertificateRepository extends CrudRepository<KeystoreCertificate, Long> {

}