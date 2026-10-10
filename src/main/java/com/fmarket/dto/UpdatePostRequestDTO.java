package com.fmarket.dto;

import org.jboss.resteasy.reactive.multipart.FileUpload;

public record UpdatePostRequestDTO(FileUpload midia, String isPrivate, String description) {
}
