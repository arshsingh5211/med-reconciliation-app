package com.arsh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private int logId;
    private String entityType;
    private String entityId; // Using varchar for both UUID and integers
    private String action;
    private String changedBy;
    private String changeDetails;
    private Date timestamp;
}
