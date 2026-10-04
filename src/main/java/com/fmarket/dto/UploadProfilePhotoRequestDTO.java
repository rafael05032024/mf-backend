package com.fmarket.dto;

import org.jboss.resteasy.reactive.multipart.FileUpload;

public record UploadProfilePhotoRequestDTO(FileUpload file) {
}
