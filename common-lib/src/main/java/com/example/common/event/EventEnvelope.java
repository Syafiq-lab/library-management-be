package com.example.common.event;

import com.example.common.api.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * Generic wrapper used for publishing domain events via messaging (Kafka/Spring Cloud Stream).
 * Keep this class lightweight and safe-to-log (payload excluded from toString).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"payload"})
public class EventEnvelope<T> {

    private String eventName;
    private String aggregateType;
    private String aggregateId;

    private ApiStatus status;   // e.g. SUCCESS / ERROR
    private Integer code;       // typically HTTP-like status code (200/400/500)
    private String message;

    private T payload;

    private Instant createdAt;
    private String sourceService;
    private String traceId;

    public static <T> EventEnvelope<T> success(
            String eventName,
            String aggregateType,
            String aggregateId,
            T payload,
            String sourceService,
            String traceId
    ) {
        return EventEnvelope.<T>builder()
                .eventName(eventName)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .status(ApiStatus.SUCCESS)
                .code(HttpStatus.OK.value())
                .message("OK")
                .payload(payload)
                .createdAt(Instant.now())
                .sourceService(sourceService)
                .traceId(traceId)
                .build();
    }

    public static <T> EventEnvelope<T> error(
            String eventName,
            String aggregateType,
            String aggregateId,
            HttpStatus httpStatus,
            String message,
            T payload,
            String sourceService,
            String traceId
    ) {
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
