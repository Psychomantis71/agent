package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.Instance
import org.springframework.data.repository.CrudRepository

interface InstanceRepository  extends CrudRepository<Instance, Long> {


}