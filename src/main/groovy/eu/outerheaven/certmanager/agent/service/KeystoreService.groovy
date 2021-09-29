package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.form.KeystoreForm
import eu.outerheaven.certmanager.agent.repository.KeystoreRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.security.cert.CertificateExpiredException
import java.security.cert.CertificateNotYetValidException


@Service
class KeystoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreService.class)

    @Autowired
    private final KeystoreRepository repository




    void create(KeystoreForm form){

        Keystore keystore = new Keystore(
                location: form.location,
                description: form.description,
                password: form.password,
                managed: form.managed,
        )
        repository.save(keystore)


    }

     Keystore get(Long keystoreId){
        repository.findById(keystoreId).get()
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

}
