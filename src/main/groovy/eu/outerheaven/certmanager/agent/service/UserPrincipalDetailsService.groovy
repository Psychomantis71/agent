package eu.outerheaven.certmanager.agent.service

import eu.outerheaven.certmanager.agent.entity.User
import eu.outerheaven.certmanager.agent.entity.UserPrincipal
import eu.outerheaven.certmanager.agent.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserPrincipalDetailsService implements UserDetailsService{

    private UserRepository userRepository

    UserPrincipalDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository
    }

    @Override
    UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(s)
        UserPrincipal userPrincipal = new UserPrincipal(user)
        return userPrincipal
    }
}
