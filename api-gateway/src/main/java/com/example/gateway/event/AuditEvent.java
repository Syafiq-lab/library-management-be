package com.example.gateway.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

	private String traceId;
	private String path;
	private String method;
	private String clientIp;
	private Instant timestamp;
}
