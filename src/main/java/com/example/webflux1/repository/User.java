package com.example.webflux1.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.event.EventListener;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
//@EventListener(value = A)
public class User {
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
