package eu.outerheaven.certmanager.agent.storage

import eu.outerheaven.certmanager.agent.dto.PayloadUploadDto
import eu.outerheaven.certmanager.agent.form.PayloadLocationForm
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path
import java.util.stream.Stream

interface StorageService {

    void init()

    String store(PayloadUploadDto payloadUploadDto)

    Stream<Path> loadAll()

    Path load(String filename)

    Resource loadAsResource(String filename)

    void deleteAll()

    void addPayloadLocation(PayloadLocationForm payloadLocationForm)

    void removePayloadLocation(Long id)

    List<PayloadLocationForm> getAllPayloadLocations()
}