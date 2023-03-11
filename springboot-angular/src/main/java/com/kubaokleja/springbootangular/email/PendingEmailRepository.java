package com.kubaokleja.springbootangular.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface PendingEmailRepository extends JpaRepository<PendingEmail, Long> {

    Optional<PendingEmail> findByReceiverAndSubject(String receiver, String subject);
}
