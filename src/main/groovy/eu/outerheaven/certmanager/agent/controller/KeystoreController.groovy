package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.entity.Keystore
import eu.outerheaven.certmanager.agent.form.KeystoreForm
import eu.outerheaven.certmanager.agent.service.KeystoreService
import javassist.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private final KeystoreService service

    @PostMapping("/add")
    void createKeystore(@RequestBody KeystoreForm form){
        service.create(form)
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
