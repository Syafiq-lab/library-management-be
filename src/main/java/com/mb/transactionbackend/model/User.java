
package com.mb.transactionbackend.model;

import com.mb.transactionbackend.enums.RoleEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "users")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false)
	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "user_roles",
			joinColumns = @JoinColumn(name = "user_id")
	)
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Set<RoleEnum> roles = new HashSet<>();

	@Column(nullable = false)
	private boolean deleted = false;

	@Version
	private Integer version = 0;

	@CreatedDate
	@Column(updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	public void addRole(RoleEnum role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		roles.add(role);
	}
}