package com.kubaokleja.springbootangular.repository;

import com.kubaokleja.springbootangular.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
