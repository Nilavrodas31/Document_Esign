
package com.example.signature.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.signature.entity.Signature;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {

    List<Signature> findByDocumentId(Long documentId);

    List<Signature> findBySignedBy(String signedBy);
}
