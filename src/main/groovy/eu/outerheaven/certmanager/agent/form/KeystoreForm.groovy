package eu.outerheaven.certmanager.agent.form

import groovy.transform.ToString

@ToString(includeFields = true)
class KeystoreForm {

    String location

    String description

    String password

    Boolean managed
}
