package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreCertificateDto
import eu.outerheaven.certmanager.agent.entity.Certificate
import eu.outerheaven.certmanager.agent.entity.KeystoreCertificate
import eu.outerheaven.certmanager.agent.service.KeystoreCertificateService
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/certificate")
class CertificateController {
    //TODO Rename this to keystorecertificatecontroller
    @Autowired
    private final KeystoreCertificateService service

    @GetMapping("/{certificateId}")
    KeystoreCertificate get(@PathVariable Long certificateId) throws NotFoundException{
        service.get(certificateId)
    }

    @PostMapping("/addToKeystore/{keystoreId}")
    Certificate get(@RequestBody List<KeystoreCertificateDto> certificateDtos, @PathVariable Long keystoreId) throws NotFoundException{
        service.addToKeystore(certificateDtos, keystoreId)
    }

    @DeleteMapping("/{certificateId}")
    ResponseEntity delete(@PathVariable Long certificateId) throws NotFoundException{
        ResponseEntity.ok(service.remove(certificateId))
    }

}
