package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.entity.User
import eu.outerheaven.certmanager.agent.form.AuthRequestForm
import eu.outerheaven.certmanager.agent.form.InstanceForm
import eu.outerheaven.certmanager.agent.repository.InstanceRepository
import eu.outerheaven.certmanager.agent.repository.UserRepository
import eu.outerheaven.certmanager.agent.util.CertificateLoader
import eu.outerheaven.certmanager.agent.util.PreparedRequest
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient

@Service
class InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceService.class)

    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    @Autowired
    private final InstanceRepository repository

    @Autowired
    private final UserRepository userRepository

    @Autowired
    Environment environment

    Instance getData(){
        Long id = 1
        repository.findById(id).get()
    }

    void selfupdate(){
        if(repository.count() > 0){
            Long id = 1
            Instance instance = repository.findById(id).get()
            InetAddress inetAddress = InetAddress.getLocalHost()
            instance.setHostname(inetAddress.getHostName())
            instance.setIp(inetAddress.getHostAddress())
            instance.setPort(environment.getProperty("server.port").toLong())
            repository.save(instance)
        }

    }

    boolean amIAdopted(){
        if( repository.count() == 0){
            Long id = 1
            Instance instance = new Instance()
            InetAddress inetAddress = InetAddress.getLocalHost()
            instance.setHostname(inetAddress.getHostName())
            instance.setIp(inetAddress.getHostAddress())
            instance.setPort(environment.getProperty("server.port").toLong())
            instance.setAdopted(false)
            repository.save(instance)
            LOG.info("Repository for instance empty, assuming first start")
            return false
        }else if(repository.count() > 0 && repository.findById(1).get().adopted){
            return true
        }else{
            return false
        }
    }

    void requestAdoption(){
        LOG.info("Requesting adoption from controller")

        PreparedRequest preparedRequest = new PreparedRequest()
        String uri = "api/instance/request-adoption";
        Long id = 1
        Instance instance = repository.findById(id).get()
        InstanceForm instanceForm = new InstanceForm(
                id: instance.id,
                name: instance.hostname,
                hostname: instance.hostname,
                ip: instance.ip,
                port: instance.port,
                adopted: instance.adopted
        )
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<InstanceForm> request = new HttpEntity<>(instanceForm, preparedRequest.getHeader());
        ResponseEntity<String> response
        try{
            response = restTemplate.postForEntity(preparedRequest.controller_url() + uri, request, String.class)
            LOG.info(response.getBody().toString())
            //LOG.info("Adoption pending from administrator")
        } catch(Exception e){
            LOG.error("Adoption request rejected with error message: " + e )
        }

    }

    String update(InstanceForm instanceForm){
        Long id = 1
        Instance instance = repository.findById(id).get()
        instance.setHostname(instanceForm.getHostname())
        instance.setIp(instanceForm.getIp())
        instance.setPort(instanceForm.getPort())
        instance.setAdopted(instanceForm.getAdopted())
        instance.setControllerId(instanceForm.getId())
        repository.save(instance)
        saveNewCredentials(instanceForm.getNewUsername(), instanceForm.getNewPassword())
        CertificateLoader certificateLoader = new CertificateLoader()
        String newPassword = certificateLoader.generateRandomAlphanumeric()

        User user = userRepository.findByUserName("admin")
        user.setPassword(passwordEncoder.encode(newPassword))
        userRepository.save(user)
        PreparedRequest preparedRequest = new PreparedRequest()
        preparedRequest.getLoginToken()
        return newPassword
    }

    void saveNewCredentials(String username, String password){
        try {
            InputStream input = new FileInputStream("controller.properties")
            Properties prop = new Properties();
            prop.load(input);
            // set the properties value
            prop.setProperty("controller.user", username);
            if(password != null){
                prop.setProperty("controller.password", password);
            }
            OutputStream output = new FileOutputStream("controller.properties")
            // save properties to project root folder
            prop.store(output, null);
            output.close()
            input.close()
            System.out.println(prop);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

}
