package com.example.webflux1.dto;

import lombok.Data;

@Data
public class PostCreatedRequest {
    private Long userId;

    private String title;

    private String content;
}
