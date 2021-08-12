package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.repository.InstanceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InstanceService {

    @Autowired
    private final InstanceRepository repository

    Instance getData(){
        Long id = 1
        repository.findById(id).get()
    }

    void update(){
        if(repository.count() > 0){
            Long id = 1
            Instance instance = repository.findById(id).get()
            InetAddress inetAddress = InetAddress.getLocalHost()
            instance.setHostname(inetAddress.getHostName())
            instance.setIp(inetAddress.getHostAddress())
            instance.setPort(8080)
            repository.save(instance)
        }else{
            Instance instance = new Instance()
            InetAddress inetAddress = InetAddress.getLocalHost()
            instance.setHostname(inetAddress.getHostName())
            instance.setIp(inetAddress.getHostAddress())
            instance.setPort(8080)
            repository.save(instance)
        }

    }

}
