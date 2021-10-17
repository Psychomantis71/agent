package eu.outerheaven.certmanager.agent.dto

import java.security.Key

class CertificateDto {
    Long id
    String alias
    String key
    String encodedX509
    Boolean managed
    Long keystoreId
}
