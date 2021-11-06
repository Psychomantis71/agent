package eu.outerheaven.certmanager.agent.config

import eu.outerheaven.certmanager.agent.security.JWTAuthenticationFilter;
import eu.outerheaven.certmanager.agent.security.JWTLoginFilter
import eu.outerheaven.certmanager.agent.service.UserPrincipalDetailsService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;



@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserPrincipalDetailsService userPrincipalDetailsService

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:8091"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .ignoringAntMatchers("/login")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                .antMatchers("/onlyforadmin/**").hasAuthority("ADMIN")
                .antMatchers("/api/**").hasAnyAuthority("USER", "ADMIN")
                .antMatchers("/**").permitAll()
                .and()
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(authenticationProvider())

        /*auth
                .inMemoryAuthentication()
                .withUser("agent_user").password("{noop}password").authorities("USER", "ADMIN")
                .and()
                .withUser("user").password("{noop}password").authorities("USER");*/
    }

    DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider()
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())
        daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailsService)

        return daoAuthenticationProvider
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder()
    }
}
