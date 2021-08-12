package eu.outerheaven.certmanager.agent.entity

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id

    private String userName

    private String password

    private String email

    User(int id, String userName, String password, String email) {
        this.id = id
        this.userName = userName
        this.password = password
        this.email = email
    }

    User() {
    }

    int getId() {
        return id
    }

    void setId(int id) {
        this.id = id
    }

    String getUserName() {
        return userName
    }

    void setUserName(String userName) {
        this.userName = userName
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }

    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }
}

