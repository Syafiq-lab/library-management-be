package com.example.userservice.messaging;

import com.example.userservice.domain.User;
import com.example.userservice.event.UserCreatedEvent;
import com.example.userservice.event.UserUpdatedEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

	private final StreamBridge streamBridge;

	public UserEventPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	public void publishUserCreated(User user) {
		UserCreatedEvent event = new UserCreatedEvent(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getFullName(),
				user.isActive()
		);
		streamBridge.send("userCreated-out-0", event);
	}

	public void publishUserUpdated(User user) {
		UserUpdatedEvent event = new UserUpdatedEvent(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getFullName(),
				user.isActive()
		);
		streamBridge.send("userUpdated-out-0", event);
	}
}
