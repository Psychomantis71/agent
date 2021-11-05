package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.form.KeystoreForm
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.security.KeyStore
import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException


@Service
class KeystoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class)

    @Autowired
    private final KeystoreRepository repository




    KeystoreDto create(KeystoreForm form){

        Keystore keystore = new Keystore(
                location: form.location,
                description: form.description,
                password: form.password,
                managed: form.managed,
        )
        LOG.info("Adding keystore")
        Long id = repository.save(keystore).getId()
        update(id)
        update(id)
        keystore = repository.findById(id).get()
        KeystoreDto keystoreDto = new KeystoreDto(
                id: keystore.id,
                location: keystore.location,
                description: keystore.description,
                password: keystore.password,
                certificates: toDto(keystore.getCertificates())
        )
        return keystoreDto
    }

     Keystore get(Long keystoreId){
         Keystore keystore = repository.findById(keystoreId).get()
         LOG.info("Repository ID {} path {}",keystore.getId(), keystore.getLocation())
        return keystore
    }

     Keystore removeById(Long id){
        repository.deleteById(id)
    }


    void update(Long id){
        Keystore keystore = repository.findById(id).get()
        //TODO Optional comparison with previous state of keystore
        //TODO move validity check to function where certificates are loaded to avoid unnecessary for loop
        //List<Certificate> saved_certificates = keystore.getCertificates()
        List<Certificate> current_certificates = CertificateLoader.loadCertificatesFromKeystore(keystore.getLocation(),keystore.getPassword(), keystore)

        for(int i = 0; i < current_certificates.size(); i++){
            try{
                current_certificates.get(i).getX509Certificate().checkValidity()
            }catch(CertificateExpiredException exception){
                //TODO foward to manager for mailing
                LOG.info("Certificate with alias {} is expired!", current_certificates.get(i).getAlias())
                LOG.debug("Exception: ", exception)
            }catch(CertificateNotYetValidException exception){
                LOG.info("Certificate with alias {} is not yet valid!", current_certificates.get(i).getAlias())
                LOG.debug("Exception: ", exception)
            }
            //TODO check validity compared to some set time in config, also foward if needed
        }


        keystore.setCertificates(current_certificates)
        repository.save(keystore)
    }

    CertificateDto toDto(Certificate certificate){
        CertificateLoader certificateLoader = new CertificateLoader()
        CertificateDto certificateDto = new CertificateDto(
                id: certificate.id,
                alias: certificate.alias,
                key: certificateLoader.encodeKey(certificate.getKey()),
                encodedX509: certificateLoader.encodeX509(certificate.getX509Certificate()),
                managed: certificate.managed,
                keystoreId: certificate.keystoreId
        )
        return certificateDto
    }

    List<CertificateDto> toDto(List<Certificate> certificates){
        List<CertificateDto> certificateDtos = new ArrayList<>()
        certificates.forEach(r->certificateDtos.add(toDto(r)))
        return certificateDtos
    }

}
