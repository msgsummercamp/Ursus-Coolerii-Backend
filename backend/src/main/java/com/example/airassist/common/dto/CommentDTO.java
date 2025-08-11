package com.example.airassist.common.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String userEmail;
    private String userType;
    private String content;
    private LocalDateTime timestamp;
}