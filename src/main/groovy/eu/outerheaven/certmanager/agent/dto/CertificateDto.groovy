package eu.outerheaven.certmanager.agent.dto

class CertificateDto {
    Long id
    String alias
    String encodedX509
    Boolean managed
    Long keystoreId
}
