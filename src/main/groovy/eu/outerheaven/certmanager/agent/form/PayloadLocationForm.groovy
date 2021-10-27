package eu.outerheaven.certmanager.agent.form

import groovy.transform.ToString

@ToString(includeFields = true)
class PayloadLocationForm {

    Long id
    String name
    String location
}
