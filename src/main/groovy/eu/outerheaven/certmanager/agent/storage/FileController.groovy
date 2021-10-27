package eu.outerheaven.certmanager.agent.storage

import eu.outerheaven.certmanager.agent.form.PayloadLocationForm
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/files")
class FileController {

    private StorageService storageService

    FileController(StorageService storageService) {
        this.storageService = storageService
    }

    @GetMapping("/")
    String listAllFiles(Model model) {

        model.addAttribute("files", storageService.loadAll().map(
                path -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/download/")
                        .path(path.getFileName().toString())
                        .toUriString())
                .collect(Collectors.toList()))

        return "listFiles"
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsResource(filename)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource)
    }

    @PostMapping("/upload-file")
    @ResponseBody
    FileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long payloadLocationId){
        String name = storageService.store(file, payloadLocationId)

        String uri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(name)
                .toUriString()

        return new FileResponse(name, uri, file.getContentType(), file.getSize())
    }

    @PostMapping("/upload-multiple-files")
    @ResponseBody
    List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> uploadFile(file))
                .collect(Collectors.toList())
    }

    @PostMapping("/add-location")
    ResponseEntity addLocation(@RequestBody PayloadLocationForm payloadLocationForm){
        storageService.addPayloadLocation(payloadLocationForm)
    }

    @DeleteMapping("/remove-location")
    ResponseEntity removeLocation(@RequestBody Long id){
        storageService.removePayloadLocation(id)
    }

    @GetMapping("/all-locations")
    ResponseEntity getAllLocations(){
        ResponseEntity.ok(storageService.getAllPayloadLocations())

    }
}
