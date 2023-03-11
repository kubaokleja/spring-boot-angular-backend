package com.kubaokleja.springbootangular.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Optional<Authority> findByName(String name);
}
