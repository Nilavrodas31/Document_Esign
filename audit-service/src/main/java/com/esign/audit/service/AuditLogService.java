
package com.esign.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esign.audit.entity.AuditLog;
import com.esign.audit.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Record a document activity
    @Transactional
    public AuditLog recordEvent(
            Long documentId,
            String eventType,
            String actorName,
            String userEmail,
            String ipAddress,
            String userAgent,
            String details) {

        AuditLog auditLog = new AuditLog();

        auditLog.setDocumentId(documentId);
        auditLog.setEventType(eventType);
        auditLog.setActorName(actorName);
        auditLog.setUserEmail(userEmail);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);
        auditLog.setDetails(details);

        return auditLogRepository.save(auditLog);
    }

    // Get complete document history
    @Transactional(readOnly = true)
    public List<AuditLog> getDocumentHistory(Long documentId) {

        return auditLogRepository
                .findByDocumentIdOrderByEventTimeAsc(documentId);
    }

    // Get activities performed by a user
    @Transactional(readOnly = true)
    public List<AuditLog> getUserActivities(String userEmail) {

        return auditLogRepository.findByUserEmail(userEmail);
    }

    // Get all audit records
    @Transactional(readOnly = true)
    public List<AuditLog> getAllAuditLogs() {

        return auditLogRepository.findAll();
    }
}

