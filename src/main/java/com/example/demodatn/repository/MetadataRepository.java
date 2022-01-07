package com.example.demodatn.repository;

import com.example.demodatn.entity.MetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, Long> {
    MetadataEntity findByIdAndType(Long id, Integer type);
}
