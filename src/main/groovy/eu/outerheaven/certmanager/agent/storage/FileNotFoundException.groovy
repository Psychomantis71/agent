package eu.outerheaven.certmanager.agent.storage

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
class FileNotFoundException extends StorageException {

    FileNotFoundException(String message) {
        super(message)
    }

    FileNotFoundException(String message, Throwable cause) {
        super(message, cause)
    }
}