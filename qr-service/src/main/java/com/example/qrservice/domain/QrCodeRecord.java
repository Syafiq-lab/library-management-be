package com.example.qrservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import lombok.ToString;

@ToString(exclude = "payload")
@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QrCodeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String payload;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private Instant createdAt;
}
