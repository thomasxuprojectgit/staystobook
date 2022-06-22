package com.laioffer.staybooking.service;

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.laioffer.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


@Service
public class ImageStorageService {

    // get bucket name from application.properties
    @Value("${gcs.bucket}")
    private String bucketName;

    // Access to Google cloud
    private Storage storage;

    // Inject Storage obj from GoogleCloudStorageConfig class
    @Autowired
    public ImageStorageService(Storage storage) {
        this.storage = storage;
    }

    // We will save image (MultipartFile type), name is multipart as the file may be split to several parts while uploading
    public String save(MultipartFile file) throws GCSUploadException {
        // Randomly pick string
        // A universally unique identifier (UUID) is a 128-bit label used for information in computer systems.
        String filename = UUID.randomUUID().toString();
        // Blob is binary large object, file to be uploaded
        BlobInfo blobInfo = null;
        try {
            // Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER)): all user with Reader role(need read) can access
            blobInfo = storage.createFrom(
                    BlobInfo
                            .newBuilder(bucketName, filename)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build(),
                    file.getInputStream());
        } catch (IOException exception) {
            throw new GCSUploadException("Failed to upload file to GCS");
        }

        // get the link of image from Google cloud
        return blobInfo.getMediaLink();
    }



}

