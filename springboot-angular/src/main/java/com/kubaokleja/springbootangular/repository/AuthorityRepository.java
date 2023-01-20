package com.kubaokleja.springbootangular.repository;

import com.kubaokleja.springbootangular.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
