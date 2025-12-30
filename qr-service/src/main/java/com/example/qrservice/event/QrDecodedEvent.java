package com.example.qrservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import lombok.ToString;

@ToString(exclude = "payload")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrDecodedEvent {
	private Long recordId;   // may be null if not linked to DB row
	private String payload;
	private String type;
	private Instant decodedAt;
}
