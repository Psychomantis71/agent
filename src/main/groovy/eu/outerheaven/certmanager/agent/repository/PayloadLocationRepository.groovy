package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.storage.PayloadLocation
import org.springframework.data.jpa.repository.JpaRepository

interface PayloadLocationRepository extends JpaRepository<PayloadLocation,Long> {

}