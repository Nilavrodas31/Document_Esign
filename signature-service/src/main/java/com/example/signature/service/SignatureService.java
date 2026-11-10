
package com.example.signature.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.example.signature.entity.Signature;
import com.example.signature.repository.SignatureRepository;

@Service
public class SignatureService {

    private final SignatureRepository signatureRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public SignatureService(SignatureRepository signatureRepository) {
        this.signatureRepository = signatureRepository;
    }

    public Signature signDocument(
            Long documentId,
            String email,
            String signatureText,
            String token) throws IOException, InterruptedException {

        String url = "http://localhost:8082/api/documents/"
                + documentId + "/view";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofByteArray()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Could not retrieve document. Status: "
                    + response.statusCode());
        }

        Path signedDirectory = Paths.get("signed-documents")
                .toAbsolutePath().normalize();

        Files.createDirectories(signedDirectory);

        Path signedPath = signedDirectory.resolve(
                "signed-document-" + documentId + ".pdf");

        try (PDDocument document = Loader.loadPDF(response.body())) {

            if (document.getNumberOfPages() == 0) {
                throw new IOException("PDF has no pages!");
            }

            PDPage page = document.getPage(0);

            try (PDPageContentStream contentStream =
                    new PDPageContentStream(
                            document,
                            page,
                            PDPageContentStream.AppendMode.APPEND,
                            true,
                            true)) {

                contentStream.beginText();

                contentStream.setFont(
                        new PDType1Font(
                                Standard14Fonts.FontName.HELVETICA_BOLD),
                        16);

                contentStream.newLineAtOffset(50, 50);

                contentStream.showText("Signed by: " + signatureText);

                contentStream.endText();
            }

            document.save(signedPath.toFile());
        }

        Signature signature = new Signature(
                documentId,
                email,
                signatureText,
                signedPath.toString(),
                LocalDateTime.now()
        );

        return signatureRepository.save(signature);
    }

    public List<Signature> getSignatures(Long documentId) {
        return signatureRepository.findByDocumentId(documentId);
    }

    public List<Signature> getUserSignatures(String email) {
        return signatureRepository.findBySignedBy(email);
    }

    // Get signed PDF using signature ID
    public File getSignedFile(Long signatureId) {

        Signature signature = signatureRepository.findById(signatureId)
                .orElseThrow(() ->
                        new RuntimeException("Signature not found!"));

        File file = new File(signature.getSignedFilePath());

        if (!file.exists()) {
            throw new RuntimeException("Signed PDF file not found!");
        }

        return file;
    }
}
