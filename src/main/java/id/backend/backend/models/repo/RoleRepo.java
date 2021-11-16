package id.backend.backend.models.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import id.backend.backend.models.entities.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{
  Role findByName(String name);
}
