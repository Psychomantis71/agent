package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.StandaloneCertificate
import org.springframework.data.repository.CrudRepository

interface StandaloneCertificateRepository extends CrudRepository<StandaloneCertificate, Long> {
}
