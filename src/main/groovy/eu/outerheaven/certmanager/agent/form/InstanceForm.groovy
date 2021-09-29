package eu.outerheaven.certmanager.agent.form

import groovy.transform.ToString

@ToString(includeFields = true)
class InstanceForm {
    Long id

    String name

    String hostname

    String ip

    Long port

    Boolean adopted



}
