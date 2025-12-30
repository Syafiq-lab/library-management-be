package com.example.userservice.messaging;

import com.example.userservice.domain.User;
import com.example.userservice.event.UserCreatedEvent;
import com.example.userservice.event.UserUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserEventPublisher {

    private final StreamBridge streamBridge;

    public UserEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishUserCreated(User user) {
        log.debug("Publishing UserCreatedEvent: id={}, username={}, emailPresent={}, active={}",
                user.getId(), user.getUsername(), user.getEmail() != null, user.isActive());

        UserCreatedEvent event = new UserCreatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.isActive()
        );

        boolean sent = streamBridge.send("userCreated-out-0", event);
        if (sent) {
            log.debug("UserCreatedEvent published successfully: destination=userCreated-out-0 id={}", user.getId());
        } else {
            log.warn("UserCreatedEvent publish returned false: destination=userCreated-out-0 id={}", user.getId());
        }
    }

    public void publishUserUpdated(User user) {
        log.debug("Publishing UserUpdatedEvent: id={}, username={}, emailPresent={}, active={}",
                user.getId(), user.getUsername(), user.getEmail() != null, user.isActive());

        UserUpdatedEvent event = new UserUpdatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.isActive()
        );

        boolean sent = streamBridge.send("userUpdated-out-0", event);
        if (sent) {
            log.debug("UserUpdatedEvent published successfully: destination=userUpdated-out-0 id={}", user.getId());
        } else {
            log.warn("UserUpdatedEvent publish returned false: destination=userUpdated-out-0 id={}", user.getId());
        }
    }
}
