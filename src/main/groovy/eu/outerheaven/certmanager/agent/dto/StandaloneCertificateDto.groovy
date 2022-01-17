package eu.outerheaven.certmanager.agent.dto

import eu.outerheaven.certmanager.agent.entity.CertificateType

class StandaloneCertificateDto {

    Long id

    CertificateDto certificateDto

    Long agentId

    String alias

    String path

    CertificateType certificateType

    String password

}
