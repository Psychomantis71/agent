package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.Certificate
import org.springframework.data.repository.CrudRepository

import java.security.cert.X509Certificate

interface CertificateRepository extends CrudRepository<Certificate, Long>{

   Certificate findByX509Certificate(X509Certificate x509Certificate)

}