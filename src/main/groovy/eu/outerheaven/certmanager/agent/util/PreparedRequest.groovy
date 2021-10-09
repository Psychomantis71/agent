package eu.outerheaven.certmanager.agent.util

import eu.outerheaven.certmanager.agent.entity.Instance
import eu.outerheaven.certmanager.agent.form.AuthRequestForm
import eu.outerheaven.certmanager.agent.form.InstanceForm
import eu.outerheaven.certmanager.agent.repository.InstanceRepository
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

import javax.xml.ws.spi.http.HttpExchange

class PreparedRequest {

    private static final Logger LOG = LoggerFactory.getLogger(PreparedRequest.class)
    public static final Properties defaultProperties = new Properties()
    final String controller_url = "http://192.168.1.23:8091/"


    void getLoginToken(){

        String url = controller_url + "login"

        RestTemplate template = new RestTemplate();
        AuthRequestForm authRequestForm = new AuthRequestForm()
        authRequestForm.setPassword("password")
        authRequestForm.setUsername("admin")
        HttpEntity<AuthRequestForm> request = new HttpEntity<>(authRequestForm);
        HttpEntity<String> response = template.exchange(url, HttpMethod.POST, request, String.class);
        HttpHeaders headers = response.getHeaders();
        String xsrf_token = headers.get(HttpHeaders.SET_COOKIE).get(0)
        String cookie_bearer = headers.get(HttpHeaders.SET_COOKIE).get(1)
        String expires = headers.get(HttpHeaders.EXPIRES)
        expires= StringUtils.substringBetween(expires,"[", "]")
        LOG.info("Expires on: " + expires)
        xsrf_token = StringUtils.substringBetween(xsrf_token,"XSRF-TOKEN=", ";")
        cookie_bearer = StringUtils.substringBetween(cookie_bearer,"COOKIE-BEARER=", ";")
        defaultProperties.put("XSRF-TOKEN",xsrf_token)
        defaultProperties.put("COOKIE-BEARER",cookie_bearer)
        defaultProperties.put("Expires",expires)
    }

    HttpHeaders getHeader(){

        if(defaultProperties.get("Expires") == null){
            LOG.debug("Token not found, requesting new")
            getLoginToken()
        }else if(defaultProperties.get("Expires").toString().toLong() <= System.currentTimeMillis()){
            LOG.debug("Token expired, requesting new")
            getLoginToken()
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON)

        headers.add("X-XSRF-TOKEN", defaultProperties.get("XSRF-TOKEN").toString())
        headers.add("Cookie","COOKIE-BEARER=" + defaultProperties.get("COOKIE-BEARER").toString() + "; XSRF-TOKEN=" + defaultProperties.get("XSRF-TOKEN").toString())

        return headers

    }

}
