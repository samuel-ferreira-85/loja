package com.samuel.loja.repository;

import com.samuel.loja.entities.Role;
import com.samuel.loja.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
