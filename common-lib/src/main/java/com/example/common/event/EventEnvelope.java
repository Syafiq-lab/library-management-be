package com.example.common.event;

import com.example.common.api.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {

	// Logical information about the event
	private String eventName;        // USER_CREATED, INVENTORY_CHANGED, AUDIT_HTTP_CALL
	private String aggregateType;    // USER, INVENTORY, QR, GATEWAY
	private String aggregateId;      // user id, item id, etc

	// Status and code just like ApiResponse
	private ApiStatus status;        // SUCCESS or ERROR
	private Integer code;            // 200, 201, 400, 500 and so on
	private String message;          // human readable description

	// Actual event payload
	private T payload;

	// Metadata
	private Instant createdAt;
	private String sourceService;    // api gateway, user service and so on
	private String traceId;

	// Factories for success and error

	public static <T> EventEnvelope<T> success(String eventName,
											   String aggregateType,
											   String aggregateId,
											   String message,
											   HttpStatus httpStatus,
											   T payload,
											   String sourceService,
											   String traceId) {

		return EventEnvelope.<T>builder()
				.eventName(eventName)
				.aggregateType(aggregateType)
				.aggregateId(aggregateId)
				.status(ApiStatus.SUCCESS)
				.code(httpStatus.value())
				.message(message)
				.payload(payload)
				.createdAt(Instant.now())
				.sourceService(sourceService)
				.traceId(traceId)
				.build();
	}

	public static <T> EventEnvelope<T> error(String eventName,
											 String aggregateType,
											 String aggregateId,
											 String message,
											 HttpStatus httpStatus,
											 T payload,
											 String sourceService,
											 String traceId) {

		return EventEnvelope.<T>builder()
				.eventName(eventName)
				.aggregateType(aggregateType)
				.aggregateId(aggregateId)
				.status(ApiStatus.ERROR)
				.code(httpStatus.value())
				.message(message)
				.payload(payload)
				.createdAt(Instant.now())
				.sourceService(sourceService)
				.traceId(traceId)
				.build();
	}
}
