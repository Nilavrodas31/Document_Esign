
package com.esign.document.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.esign.document.entity.Document;
import com.esign.document.repository.DocumentRepository;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    // Upload PDF document
    public Document uploadDocument(
            MultipartFile file,
            String title,
            String uploadedBy) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select a file!");
        }

        if (!"application/pdf".equalsIgnoreCase(file.getContentType())
                || !file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are allowed!");
        }

        Path uploadPath = Paths.get(uploadDir)
                .toAbsolutePath().normalize();

        Files.createDirectories(uploadPath);

        String storedFileName = java.util.UUID.randomUUID() + ".pdf";

        Path filePath = uploadPath.resolve(storedFileName);

        Files.copy(file.getInputStream(), filePath);

        Document document = new Document();

        document.setTitle(title);
        document.setFileName(file.getOriginalFilename());
        document.setFilePath(filePath.toString());
        document.setContentType("application/pdf");
        document.setFileSize(file.getSize());
        document.setUploadedBy(uploadedBy);
        document.setUploadedAt(LocalDateTime.now());
        document.setStatus("PENDING");

        return documentRepository.save(document);
    }

    // Get all documents uploaded by a user
    public List<Document> getUserDocuments(String email) {
        return documentRepository.findByUploadedBy(email);
    }

    // Get document by ID
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() ->
                    new RuntimeException("Document not found!"));
    }

    // Get stored PDF path
    public Path getDocumentPath(Long id) {
        Document document = getDocumentById(id);
        Path path = Paths.get(document.getFilePath());

        if (!Files.exists(path)) {
            throw new RuntimeException("PDF file not found!");
        }

        return path;
    }

    // Delete document
    public void deleteDocument(Long id, String email) throws IOException {
        Document document = getDocumentById(id);

        if (!document.getUploadedBy().equals(email)) {
            throw new RuntimeException("You cannot delete this document!");
        }

        Files.deleteIfExists(Paths.get(document.getFilePath()));
        documentRepository.delete(document);
    }
}
