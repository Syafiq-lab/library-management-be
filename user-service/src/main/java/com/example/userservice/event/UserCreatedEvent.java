package com.example.userservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"email"})
public class UserCreatedEvent {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private boolean active;
}
