
package com.esign.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.esign.audit.entity.AuditLog;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserEmail(String userEmail);

    List<AuditLog> findByDocumentId(Long documentId);

    List<AuditLog> findByDocumentIdOrderByEventTimeAsc(
            Long documentId);
}
