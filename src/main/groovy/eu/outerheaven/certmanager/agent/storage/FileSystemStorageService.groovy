package eu.outerheaven.certmanager.agent.storage

import eu.outerheaven.certmanager.agent.form.PayloadLocationForm
import eu.outerheaven.certmanager.agent.repository.PayloadLocationRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.FileSystemUtils
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import sun.security.krb5.internal.PAData

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.stream.Stream

@Service
class FileSystemStorageService implements StorageService {

    private final Path rootLocation

    private static final Logger LOG = LoggerFactory.getLogger(FileSystemStorageService.class)

    @Autowired
    private final PayloadLocationRepository payloadLocationRepository

    @Autowired
    FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation())
    }

    @Override
    @PostConstruct
    void init() {
        try {
            Files.createDirectories(rootLocation)
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e)
        }
    }

    @Override
    String store(MultipartFile file, Long payloadLocationId) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename())
        PayloadLocation payloadLocation = payloadLocationRepository.getById(payloadLocationId)
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename)
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename)
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, Paths.get(payloadLocation.getLocation()  + "\\" + filename),StandardCopyOption.REPLACE_EXISTING)

                //Files.copy(inputStream, this.rootLocation.resolve(filename),
                  //      StandardCopyOption.REPLACE_EXISTING)
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e)
        }

        return filename
    }

    @Override
    Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize)
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e)
        }

    }

    @Override
    Path load(String filename) {
        return rootLocation.resolve(filename)
    }

    @Override
    Resource loadAsResource(String filename) {
        try {
            Path file = load(filename)
            Resource resource = new UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable()) {
                return resource
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename)
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e)
        }
    }

    @Override
    void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile())
    }
    @Override
    void addPayloadLocation(PayloadLocationForm payloadLocationForm){
        PayloadLocation payloadLocation = new PayloadLocation(
                name: payloadLocationForm.name,
                location: payloadLocationForm.location
        )
        LOG.info("Payload location data: {} {}",payloadLocation.getLocation(),payloadLocation.getName())
        payloadLocationRepository.save(payloadLocation)
    }

    @Override
    List<PayloadLocation> getAllPayloadLocations(){
        List<PayloadLocation> all = payloadLocationRepository.findAll()
        return all
    }

    @Override
    void removePayloadLocation(Long id){
        payloadLocationRepository.deleteById(id)
    }
}