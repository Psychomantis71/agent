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


        File f = new File("controller.properties");
        if(f.exists() && !f.isDirectory()) {
        }else{

            try (OutputStream output = new FileOutputStream("controller.properties")) {

                Properties prop = new Properties();

                // set the properties value
                prop.setProperty("controller.user", "agent_user");
                prop.setProperty("controller.password", "kuracnabiciklu");
                prop.setProperty("controller.ip", "192.168.1.17");
                prop.setProperty("controller.port", "8091");

                // save properties to project root folder
                prop.store(output, null);
                output.close()
                System.out.println(prop);

            } catch (IOException io) {
                io.printStackTrace();
            }

        }

        LOG.info("Hello world, I have just started up")
        if(service.amIAdopted()){
            LOG.info("Node has been adopted from a controller")
        }else{
            LOG.info("Node is not adopted from controller yet")
        }

        service.requestAdoption()


    }

}
