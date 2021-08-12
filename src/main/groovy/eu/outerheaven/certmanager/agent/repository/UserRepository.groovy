package eu.outerheaven.certmanager.agent.repository

import eu.outerheaven.certmanager.agent.entity.User
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByUserName(String username);
}
