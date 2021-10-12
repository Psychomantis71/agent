package eu.outerheaven.certmanager.agent.dto


class KeystoreDto {
    Long id
    String location
    String description
    String password
    List<CertificateDto> certificates
}
