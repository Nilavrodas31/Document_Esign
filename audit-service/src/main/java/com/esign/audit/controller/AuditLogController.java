
package com.esign.audit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.esign.audit.entity.AuditLog;
import com.esign.audit.service.AuditLogService;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // Record a new audit event
    @PostMapping("/record")
    public ResponseEntity<AuditLog> recordEvent(
            @RequestBody AuditLog auditLog,
            jakarta.servlet.http.HttpServletRequest request) {

        AuditLog savedLog = auditLogService.recordEvent(
                auditLog.getDocumentId(),
                auditLog.getEventType(),
                auditLog.getActorName(),
                auditLog.getUserEmail(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent"),
                auditLog.getDetails()
        );

        return ResponseEntity.ok(savedLog);
    }

    // Get complete history of a document
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<AuditLog>> getDocumentHistory(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                auditLogService.getDocumentHistory(documentId));
    }

    // Get activities of a user
    @GetMapping("/user/{email}")
    public ResponseEntity<List<AuditLog>> getUserActivities(
            @PathVariable String email) {

        return ResponseEntity.ok(
                auditLogService.getUserActivities(email));
    }

    // Get all audit records
    @GetMapping("/all")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {

        return ResponseEntity.ok(
                auditLogService.getAllAuditLogs());
    }
}
