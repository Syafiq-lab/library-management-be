package com.example.library.repository;

import com.example.library.model.RoleEntity;
import com.example.library.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for RoleEntity.
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

	/**
	 * Finds a role by name.
	 *
	 * @param roleName Name of the role
	 * @return Optional containing RoleEntity if found
	 */
	Optional<RoleEntity> findByName(RoleName roleName);
}
