package eu.outerheaven.certmanager.agent.form

import groovy.transform.ToString

@ToString(includeFields = true)
class AuthRequestForm {

    private String username
    private String password

    String getUsername() {
        return username
    }

    void setUsername(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }
}
