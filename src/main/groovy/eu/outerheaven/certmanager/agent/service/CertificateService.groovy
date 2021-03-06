package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.repository.CertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CertificateService {

    @Autowired
    private final KeystoreRepository keystoreRepository

    @Autowired
    private final CertificateRepository repository

    @Autowired
    private final KeystoreService keystoreService

    @Autowired
    private final CertificateLoader certificateLoader


    Certificate get(Long certificateId){
        repository.findById(certificateId).get()
    }

    void addToKeystore(List<CertificateDto> certificateDtos, Long keystoreId){
        Keystore keystore = keystoreRepository.findById(keystoreId).get()
        CertificateLoader certificateLoader = new CertificateLoader()

        List<Certificate> certificates = new ArrayList<>()

        certificateDtos.forEach(r->{
            Certificate certificate = new Certificate(
                    alias: r.alias,
                    key: certificateLoader.decodeKey(r.key),
                    x509Certificate: certificateLoader.decodeX509(r.encodedX509)
            )
            certificates.add(certificate)
        })

        certificateLoader.addCertificatesToKeystore(keystore.getLocation(), keystore.getPassword(), certificates)
        keystoreService.update(keystoreId, false)
    }

    void remove(Long certId){
        Certificate certificate = repository.findById(certId).get()
        Keystore keystore = keystoreRepository.findById(certificate.getKeystoreId()).get()

        certificateLoader.removeCertFromKeystore(keystore.location, keystore.password, certificate.getAlias())
        keystoreService.update(keystore.getId(),false)

        /*
        List<Certificate> certificates = keystore.getCertificates()
        certificates.remove(certificate)
        keystore.setCertificates(certificates)
        keystoreRepository.save(keystore)
        repository.delete(certificate)

         */
    }
}
