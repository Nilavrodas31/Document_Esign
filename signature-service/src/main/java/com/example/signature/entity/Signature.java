
package com.example.signature.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "signatures")
public class Signature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;
    private String signedBy;
    private String signatureText;
    private String signedFilePath;
    private LocalDateTime signedAt;

    public Signature() {
    }

    public Signature(Long documentId, String signedBy,
            String signatureText, String signedFilePath,
            LocalDateTime signedAt) {
        this.documentId = documentId;
        this.signedBy = signedBy;
        this.signatureText = signatureText;
        this.signedFilePath = signedFilePath;
        this.signedAt = signedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getSignedBy() {
        return signedBy;
    }

    public void setSignedBy(String signedBy) {
        this.signedBy = signedBy;
    }

    public String getSignatureText() {
        return signatureText;
    }

    public void setSignatureText(String signatureText) {
        this.signatureText = signatureText;
    }

    public String getSignedFilePath() {
        return signedFilePath;
    }

    public void setSignedFilePath(String signedFilePath) {
        this.signedFilePath = signedFilePath;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
    }
}
