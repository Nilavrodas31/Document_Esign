
package com.esign.document.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.esign.document.entity.Document;
import com.esign.document.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Upload PDF
    @PostMapping("/upload")
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            Authentication authentication) throws IOException {

        Document document = documentService.uploadDocument(
                file, title, authentication.getName());

        return ResponseEntity.ok(document);
    }

    // Get user's documents
    @GetMapping
    public ResponseEntity<List<Document>> getUserDocuments(
            Authentication authentication) {

        return ResponseEntity.ok(
                documentService.getUserDocuments(authentication.getName()));
    }

    // View PDF
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        Document document = documentService.getDocumentById(id);

        if (!document.getUploadedBy().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        Resource resource = new UrlResource(
                documentService.getDocumentPath(id).toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // Download PDF
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        Document document = documentService.getDocumentById(id);

        if (!document.getUploadedBy().equals(authentication.getName())) {
            return ResponseEntity.status(403).build();
        }

        Resource resource = new UrlResource(
                documentService.getDocumentPath(id).toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    // Delete document
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long id,
            Authentication authentication) throws IOException {

        documentService.deleteDocument(id, authentication.getName());

        return ResponseEntity.ok("Document deleted successfully!");
    }
}
