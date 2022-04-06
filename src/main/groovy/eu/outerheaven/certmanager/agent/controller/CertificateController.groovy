package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.dto.RetrieveFromPortDto
import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.service.CertificateService
import eu.outerheaven.certmanager.agent.service.InstanceService
import javassist.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/certificate")
class CertificateController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateController.class)


    @Autowired
    private final CertificateService service

    @PostMapping("/retrieve-from-port")
    ResponseEntity get(@RequestBody RetrieveFromPortDto retrieveFromPortDto) throws NotFoundException{
        LOG.info("Retrieve from port request arrived from controller")
        ResponseEntity.ok().body(service.fetchFromUrl(retrieveFromPortDto))
    }

}
