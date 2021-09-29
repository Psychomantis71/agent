package eu.outerheaven.certmanager.agent.entity

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
class AuthRequest {

    private String username;
    private String password;

    String getUserName() {
        return username
    }

    void setUserName(String username) {
        this.username = username
    }

    String getPassword() {
        return password
    }

    void setPassword(String password) {
        this.password = password
    }
}

