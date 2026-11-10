
package com.example.signature.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.signature.entity.Signature;
import com.example.signature.service.SignatureService;

@RestController
@RequestMapping("/api/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/{documentId}/sign")
    public ResponseEntity<Signature> signDocument(
            @PathVariable Long documentId,
            @RequestParam String signatureText,
            @RequestHeader("Authorization") String token,
            Authentication authentication)
            throws IOException, InterruptedException {

        Signature signature = signatureService.signDocument(
                documentId,
                authentication.getName(),
                signatureText,
                token
        );

        return ResponseEntity.ok(signature);
    }

    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<Signature>> getSignatures(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                signatureService.getSignatures(documentId)
        );
    }

    @GetMapping("/my-signatures")
    public ResponseEntity<List<Signature>> getMySignatures(
            Authentication authentication) {

        return ResponseEntity.ok(
                signatureService.getUserSignatures(
                        authentication.getName())
        );
    }

    // Download signed PDF using signature ID
    @GetMapping("/{signatureId}/download")
    public ResponseEntity<Resource> downloadSignedDocument(
            @PathVariable Long signatureId) {

        File file = signatureService.getSignedFile(signatureId);

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"signed-document.pdf\""
                )
                .body(resource);
    }
}
