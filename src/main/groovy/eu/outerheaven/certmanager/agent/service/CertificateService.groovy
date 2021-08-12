package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.repository.CertificateRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CertificateService {

    @Autowired
    private final CertificateRepository repository

    Certificate get(Long certificateId){
        repository.findById(certificateId).get()
    }
}
