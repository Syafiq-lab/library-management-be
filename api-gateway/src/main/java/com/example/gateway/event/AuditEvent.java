package com.example.gateway.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

	private String path;
	private String method;
	private int statusCode;
	private Instant timestamp;
}
