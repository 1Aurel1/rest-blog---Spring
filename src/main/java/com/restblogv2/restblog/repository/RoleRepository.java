package com.restblogv2.restblog.repository;

import com.restblogv2.restblog.model.role.Role;
import com.restblogv2.restblog.model.role.RoleName;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}

