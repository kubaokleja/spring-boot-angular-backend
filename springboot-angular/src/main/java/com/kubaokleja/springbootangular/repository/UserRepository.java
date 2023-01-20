package com.kubaokleja.springbootangular.repository;

import com.kubaokleja.springbootangular.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String firstName, String lastName, String username, Pageable pageable);

    default Page<User> findUsersByFilterKeyword(String keyword, Pageable pageable){
        return findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }

    User findUserByUsername(String username);

    //JPA query could be used. I just want to test different ways.
    @Query(value = "select * from users u where u.email =:email", nativeQuery = true)
    User findByEmailNativeSQLNamedParam(@Param("email") String email);

    User findUserByUserId(String userId);
}
