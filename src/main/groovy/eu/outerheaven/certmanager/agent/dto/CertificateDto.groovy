package eu.outerheaven.certmanager.agent.dto

import java.security.Key

class CertificateDto {
    Long id

    String encodedX509Certificate

    String encodedPrivateKey
}
