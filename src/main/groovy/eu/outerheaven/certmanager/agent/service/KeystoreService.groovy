package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreCertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import eu.outerheaven.certmanager.agent.form.KeystoreForm
import eu.outerheaven.certmanager.agent.repository.CertificateRepository
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import eu.outerheaven.certmanager.agent.util.PreparedRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class KeystoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class)

    @Autowired
    private final KeystoreRepository repository

    @Autowired
    private final CertificateRepository certificateRepository

    @Autowired
    private final CertificateLoader certificateLoader

    //Refactored
    KeystoreDto create(KeystoreForm form){

        Keystore keystore = new Keystore(
                location: form.location,
                description: form.description,
                password: form.password,
        )
        LOG.info("Adding keystore")
        Long id = repository.save(keystore).getId()
        update(id, true)
        keystore = repository.findById(id).get()
        KeystoreDto keystoreDto = new KeystoreDto(
                id: keystore.id,
                location: keystore.location,
                description: keystore.description,
                password: keystore.password,
                keystoreCertificateDtos: toDto(keystore.getKeystoreCertificates())
        )
        return keystoreDto
    }
    //Unchanged
    Keystore get(Long keystoreId){
         Keystore keystore = repository.findById(keystoreId).get()
         LOG.info("Repository ID {} path {}",keystore.getId(), keystore.getLocation())
        return keystore
    }
    //Unchanged
    Keystore removeById(Long id){
        repository.deleteById(id)
    }
    //Refactored
    void update(Long id, Boolean firstUpdate){
        Keystore keystore = repository.findById(id).get()
        List<KeystoreCertificate> current_certificates = certificateLoader.loadCertificatesFromKeystore(keystore.getLocation(),keystore.getPassword(), keystore)
        current_certificates = purgeDuplicateX509(current_certificates)
        keystore.setKeystoreCertificates(current_certificates)
        repository.save(keystore)
        LOG.info("Keystore size after update is: " + keystore.getKeystoreCertificates().size())
        if(!firstUpdate){
            sendUpdateToController(keystore.getId())
        }
    }
    //Refactored
    void sendUpdateToController(Long id){
        LOG.info("1")
        String uri = "api/keystore/update";
        Keystore keystore = repository.findById(id).get()
        LOG.info("2")
        KeystoreDto keystoreDto = new KeystoreDto(
                id: keystore.id,
                location: keystore.location,
                description: keystore.description,
                password: keystore.password,
                keystoreCertificateDtos: toDto(keystore.getKeystoreCertificates())
        )
        LOG.info("3")
        PreparedRequest preparedRequest = new PreparedRequest()
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<KeystoreDto> request = new HttpEntity<>(keystoreDto, preparedRequest.getHeader());
        ResponseEntity<String> response
        LOG.info("4")
        LOG.info("Controller url is: " + preparedRequest.controller_url())
        try{
            response = restTemplate.postForEntity(preparedRequest.controller_url() + uri , request, String.class)
            LOG.info(response.getBody().toString())
        } catch(Exception e){
            LOG.error("Update on controller failed with error: " + e )
        }


    }

    KeystoreCertificateDto toDto(KeystoreCertificate keystoreCertificate){
        CertificateDto certificateDto = new CertificateDto(
                encodedX509Certificate: certificateLoader.encodeX509(keystoreCertificate.certificate.x509Certificate),
                encodedPrivateKey: certificateLoader.encodeKey(keystoreCertificate.certificate.key)
        )
        KeystoreCertificateDto keystoreCertificateDto = new KeystoreCertificateDto(
                id: keystoreCertificate.id,
                alias: keystoreCertificate.alias,
                certificateDto: certificateDto,
                keystoreId: keystoreCertificate.keystoreId
        )
        return keystoreCertificateDto
    }

    List<KeystoreCertificateDto> toDto(List<KeystoreCertificate> keystoreCertificates){
        List<KeystoreCertificateDto> keystoreCertificateDtos = new ArrayList<>()
        keystoreCertificates.forEach(r->keystoreCertificateDtos.add(toDto(r)))
        return keystoreCertificateDtos
    }

    List<KeystoreCertificate> purgeDuplicateX509(List<KeystoreCertificate> keystoreCertificates){
        List<KeystoreCertificate> purgedResults = new ArrayList<>()
        keystoreCertificates.forEach(r->{
            Certificate certificate = certificateRepository.findByX509Certificate(r.certificate.x509Certificate)
            if(certificate != null){
                r.setCertificate(certificate)
                purgedResults.add(r)
            }else{
                Certificate certToSave = r.certificate
                Long certificateId = certificateRepository.save(certToSave).getId()
                certToSave.setId(certificateId)
                r.setCertificate(certToSave)
            }

        })
        return keystoreCertificates
    }
}
