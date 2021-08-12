package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.Certificate
import org.springframework.data.repository.CrudRepository

interface CertificateRepository extends CrudRepository<Certificate, Long>{

}