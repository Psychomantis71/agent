package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.Keystore
import org.springframework.data.repository.CrudRepository

interface KeystoreRepository extends CrudRepository<Keystore, Long>{
    //Keystore findByid(Long KeystoreId)
    Keystore findByLocation(String location)

}