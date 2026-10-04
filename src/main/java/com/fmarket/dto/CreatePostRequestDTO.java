package com.fmarket.dto;

import org.jboss.resteasy.reactive.multipart.FileUpload;

public record CreatePostRequestDTO(FileUpload midia, String isPrivate, String description) {
}
