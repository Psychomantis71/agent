package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.service.InstanceService
import javassist.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/instance")
class InstanceController {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceController.class)


    @Autowired
    private final InstanceService service

    @GetMapping("/data")
    Instance get() throws NotFoundException{
        LOG.info("Instance data has been requested!")
        service.getData()
    }

    @PostMapping("/update")
    void update() throws NotFoundException{
        LOG.info("Request for instance update received!")
        service.update()
    }

}
