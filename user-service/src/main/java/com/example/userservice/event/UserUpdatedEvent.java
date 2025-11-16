package com.example.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatedEvent {

	private Long id;
	private String username;
	private String email;
	private String fullName;
	private boolean active;
}
