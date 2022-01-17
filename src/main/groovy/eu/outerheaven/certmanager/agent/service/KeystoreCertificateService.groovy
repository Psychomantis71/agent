package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreCertificateDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import eu.outerheaven.certmanager.agent.repository.CertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreCertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class KeystoreCertificateService {

    @Autowired
    private final KeystoreRepository keystoreRepository

    @Autowired
    private final KeystoreCertificateRepository repository

    @Autowired
    private final KeystoreService keystoreService

    @Autowired
    private final CertificateLoader certificateLoader

    //Refactored
    KeystoreCertificate get(Long certificateId){
        repository.findById(certificateId).get()
    }

    //Refactored
    void addToKeystore(List<KeystoreCertificateDto> certificateDtos, Long keystoreId){
        Keystore keystore = keystoreRepository.findById(keystoreId).get()
        CertificateLoader certificateLoader = new CertificateLoader()


        List<KeystoreCertificate> keystoreCertificates = new ArrayList<>()

        certificateDtos.forEach(r->{
            Certificate certificate = new Certificate(
                key: certificateLoader.decodeKey(r.certificateDto.encodedPrivateKey),
                x509Certificate: certificateLoader.decodeX509(r.certificateDto.encodedX509Certificate)
            )
            KeystoreCertificate keystoreCertificate = new KeystoreCertificate(
                    alias: r.alias,
                    certificate: certificate

            )
            keystoreCertificates.add(keystoreCertificate)
        })

        certificateLoader.addCertificatesToKeystore(keystore.getLocation(), keystore.getPassword(), keystoreCertificates)
        keystoreService.update(keystoreId, false)
    }
    //Refactored
    void remove(Long certId){
        KeystoreCertificate certificate = repository.findById(certId).get()
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
