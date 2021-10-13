package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.dto.CertificateDto
import eu.outerheaven.certmanager.agent.dto.KeystoreDto
import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.form.KeystoreForm
import eu.outerheaven.certmanager.agent.service.KeystoreService
import javassist.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
@RequestMapping("/api/keystore")
class KeystoreController {

    private static final Logger LOG = LoggerFactory.getLogger(KeystoreController.class)

    @Autowired
    private final KeystoreService service

    @PostMapping("/add")
    ResponseEntity<KeystoreDto> createKeystore(@RequestBody KeystoreForm form){
        ResponseEntity.ok(service.create(form))
    }

    @GetMapping("/{keystoreId}")
    Keystore get(@PathVariable Long keystoreId) throws NotFoundException{
        service.get(keystoreId)
    }

    @DeleteMapping("/{keystoreId}")
    Keystore remove(@PathVariable Long keystoreId) throws NotFoundException{
        service.removeById(keystoreId)
    }

    @PostMapping("/{keystoreId}/update")
    void updateKeystore(@PathVariable Long keystoreId){
        service.update(keystoreId)
    }


}
