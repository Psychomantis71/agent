package eu.outerheaven.certmanager.agent.controller

import eu.outerheaven.certmanager.agent.entity.AuthRequest
import eu.outerheaven.certmanager.agent.util.JwtUtil

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthController {

    @Autowired
    private JwtUtil jwtUtil
    @Autowired
    private AuthenticationManager authenticationManager

    @GetMapping("/helloworld")
    String welcome() {
        return "Welcome to javatechie !!"
    }

    @PostMapping("/authenticate")
    String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            )
        } catch (Exception ex) {
            throw new Exception("inavalid username/password", ex)
        }
        return jwtUtil.generateToken(authRequest.getUserName())
    }

}
