package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreCertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreDto
import eu.outerheaven.certmanager.agent.dto.RetrieveFromPortDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CertificateService {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateService)

    List<CertificateDto> fetchFromUrl(RetrieveFromPortDto retrieveFromPortDto){
        CertificateLoader certificateLoader = new CertificateLoader()
        List<CertificateDto> certificateDtos = toDto(certificateLoader.loadCertificatesFromHost(retrieveFromPortDto.hostname, retrieveFromPortDto.port))
        return certificateDtos
    }

    CertificateDto toDto(Certificate certificate){
        CertificateLoader certificateLoader = new CertificateLoader()
        CertificateDto certificateDto = new CertificateDto(
                encodedX509Certificate: certificateLoader.encodeX509(certificate.x509Certificate),
                encodedPrivateKey: certificateLoader.encodeKey(certificate.key)
        )
        return certificateDto
    }

    List<CertificateDto> toDto(List<Certificate> certificates){
        List<CertificateDto> certificateDtos = new ArrayList<>()
        certificates.forEach(r->{
            certificateDtos.add(toDto(r))
        })
        return certificateDtos
    }

}
