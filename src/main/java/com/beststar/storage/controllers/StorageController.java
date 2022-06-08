package com.beststar.storage.controllers;

import java.util.concurrent.ExecutionException;

import com.beststar.storage.Constants;
import com.beststar.storage.entity.FileInfoResponse;
import com.beststar.storage.entity.MessageResponse;
import com.beststar.storage.exceptions.StorageException;
import com.beststar.storage.services.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

@RestController
@RequestMapping("/api/storage")
@Tags(value = {@Tag(name = "Storage", description = "Web service for working with files for storage")})
public class StorageController {
    
    private final StorageService storageService;
    
    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(path = "/file-info/{fileName}", 
                produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})    
    public FileInfoResponse fileInfo(
        @Parameter(required = true, name = "fileName", description = "The filename that needs to be fetched")
        @PathVariable String fileName, 
        @Parameter(required = false, name = "accept", hidden = true)
        @RequestHeader("accept") String accept) {

        checkPermissibleAccept(accept);
        FileInfoResponse infoFileResponse = null;
        if (storageService.fileExists(fileName)) { 
            String storeFilePath = storageService.getStoreFilePath(fileName).toString(); 
            //infoFileResponse = storageService.fileInfoRandomLine(storeFilePath);
            try {
                infoFileResponse = storageService.fileInfoRandomLineAsync(storeFilePath).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new StorageException(e.getMessage());
            }
        } else {
            throw new StorageException("File hasn't been saved in the storage!");
        }
        return infoFileResponse;
    }

    @GetMapping(path = "/file-info/{fileName}", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get a file info details in the response")
    public String fileInfoText(
        @Parameter(required = true, name = "fileName", description = "The filename that needs to be fetched")
        @PathVariable String fileName,
        @Parameter(required = false, name = "accept", hidden = true) 
        @RequestHeader("accept") String accept) {

        checkPermissibleAccept(accept);
        FileInfoResponse infoFileResponse = null;
        if (storageService.fileExists(fileName)) { 
            String storeFilePath = storageService.getStoreFilePath(fileName).toString(); 
            //infoFileResponse = storageService.fileInfoRandomLine(storeFilePath);
            try {
                infoFileResponse = storageService.fileInfoRandomLineAsync(storeFilePath).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new StorageException(e.getMessage());
            }
        } else {
            throw new StorageException("File hasn't been saved in the storage!");
        }
        return infoFileResponse.toText();
    }
    
    @PostMapping(path = "/file-upload", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public MessageResponse fileUpload(
        @RequestBody(required = true, description = "Choose a file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
        @RequestParam(name = "file") MultipartFile file,
        @Parameter(required = false, name = "accept", hidden = true)
        @RequestHeader(name = "accept") String accept) {

        checkPermissibleAccept(accept);
        storageService.storeFile(file); 

		return new MessageResponse("Storaged file name: " + file.getOriginalFilename());
	}
    
    @PostMapping(path = "/file-upload", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.TEXT_PLAIN_VALUE)           
    @Operation(summary = "Upload a text file and store it")            
	public String fileUploadText(
        @RequestBody(required = true, description = "Choose a file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
        @RequestParam(name = "file") MultipartFile file,
        @Parameter(required = false, name = "accept", hidden = true)
        @RequestHeader(name = "accept")
        String accept) {

        checkPermissibleAccept(accept);
        storageService.storeFile(file); 

		return "Storaged file name: " + file.getOriginalFilename();
	}    

    @PostMapping(path = "/file-upload-and-info", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})                  
	public FileInfoResponse fileUploadAndInfo(
        @RequestBody(required = true, description = "Choose a file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
        @RequestParam(name = "file") MultipartFile file,
        @Parameter(required = false, name = "accept", hidden = true)
        @RequestHeader(name = "accept") String accept) {

        checkPermissibleAccept(accept);
        String storePathFile = storageService.storeFile(file);
        return storageService.fileInfoRandomLine(storePathFile);
	}

    @PostMapping(path = "/file-upload-and-info", 
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "File upload and get a file info details in the response")              
	public String fileUploadAndInfoText(
        @RequestBody(required = true, description = "Choose a file", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(type = "string", format = "binary")))
        @RequestParam(name = "file") MultipartFile file,
        @Parameter(required = false, name = "accept", hidden = true)
        @RequestHeader(name = "accept") String accept) {

        checkPermissibleAccept(accept);
        String storePathFile = storageService.storeFile(file);
        FileInfoResponse fileInfoResponse = storageService.fileInfoRandomLine(storePathFile);
        return fileInfoResponse.toText();
	}
    
    private void checkPermissibleAccept(String accept) {
        if (!Constants.permissibleAccept.contains(accept)) {
            throw new StorageException("Accept not supported!");
        }
    }
}


