package eu.outerheaven.certmanager.agent.form

import groovy.transform.ToString

@ToString(includeFields = true)
class KeystoreForm {

    Long id

    String location

    String description

    String password

    Boolean managed
}
