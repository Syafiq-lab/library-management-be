package com.example.qrservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrGeneratedEvent {
	private Long id;
	private String payload;
	private String type;
	private Instant createdAt;
}
