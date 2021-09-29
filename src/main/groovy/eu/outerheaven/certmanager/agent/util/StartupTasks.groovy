package eu.outerheaven.certmanager.agent.util

import eu.outerheaven.certmanager.agent.entity.User
import eu.outerheaven.certmanager.agent.service.InstanceService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

import javax.jws.soap.SOAPBinding

@Component
class StartupTasks {
    private static final Logger LOG = LoggerFactory.getLogger(StartupTasks.class)


    @Autowired
    private final InstanceService service

    @EventListener
    public void doSomethingAfterStartup(ApplicationReadyEvent event) {
        LOG.info("Hello world, I have just started up")
        if(service.amIAdopted()){
            LOG.info("Node has been adopted from a controller")
        }else{
            LOG.info("Node is not adopted from controller yet")
        }

        service.requestAdoption()


    }

}
