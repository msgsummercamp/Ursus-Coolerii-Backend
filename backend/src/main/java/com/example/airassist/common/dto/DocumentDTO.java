package com.example.airassist.common.dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class DocumentDTO {
    private String filename;
    private Timestamp uploadTimestamp;
}