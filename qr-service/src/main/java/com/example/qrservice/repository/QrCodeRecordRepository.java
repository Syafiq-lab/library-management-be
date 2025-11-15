package com.example.qrservice.repository;

import com.example.qrservice.domain.QrCodeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrCodeRecordRepository extends JpaRepository<QrCodeRecord, Long> {
}
