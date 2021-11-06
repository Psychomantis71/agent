package eu.outerheaven.certmanager.agent.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal implements UserDetails{

    private User user

    public UserPrincipal(User user){
        this.user = user
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>()
        GrantedAuthority authority = new SimpleGrantedAuthority(this.user.getUserRole().toString())
        authorities.add(authority)
        return authorities
    }

    @Override
    String getPassword() {
        return this.user.getPassword()
    }

    @Override
    String getUsername() {
        return this.user.getUserName()
    }

    @Override
    boolean isAccountNonExpired() {
        return true
    }

    @Override
    boolean isAccountNonLocked() {
        return true
    }

    @Override
    boolean isCredentialsNonExpired() {
        return true
    }

    @Override
    boolean isEnabled() {
        return true
    }
}
