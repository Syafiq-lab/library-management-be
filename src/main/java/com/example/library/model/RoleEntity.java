package com.example.library.model;

import com.example.library.model.enums.RoleName;
import lombok.*;
import javax.persistence.*;

/**
 * Entity class representing a user role.
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Name of the role.
	 */
	@Enumerated(EnumType.STRING)
	private RoleName name;
}
