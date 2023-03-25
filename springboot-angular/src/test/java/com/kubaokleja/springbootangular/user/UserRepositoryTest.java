package com.kubaokleja.springbootangular.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.domain.PageRequest.of;

/*
    This is an example of repository test. However, in application I am going
     to test only specific cases like my own query or more difficult Derived Query Methods.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setup(){
        user = User.builder()
                .email("test@test.pl")
                .firstName("Kuba")
                .lastName("Test")
                .username("user_test")
                .password("password")
                .build();
    }

    @DisplayName("save user test")
    @Test
    public void givenUserObject_whenSave_thenReturnUser(){
        //given
        //when
        User savedUser = userRepository.save(user);

        //then
        assertThat(savedUser).isNotNull();
    }

    @DisplayName("get users test")
    @Test
    public void givenUsersList_whenFindAll_returnUsersList(){
        //given
        User anotherUser = User.builder()
                .email("test2@test.pl")
                .firstName("Kuba2")
                .lastName("Test2")
                .username("user_test2")
                .password("password")
                .build();

        userRepository.save(user);
        userRepository.save(anotherUser);

        //when
        List<User> users = userRepository.findAll();

        //then
        assertThat(users.size()).isEqualTo(2);
    }

    @DisplayName("get user by id test")
    @Test
    public void givenUser_whenFindById_returnGivenUser(){
        //given
        userRepository.save(user);

        //when
        User savedUser = userRepository.findById(user.getId()).get();

        //then
        assertThat(savedUser.getId()).isEqualTo(user.getId());
    }

    @DisplayName("get user by username - jpa query test")
    @Test
    public void givenUser_whenFindByUsername_returnGivenUser(){
        //given
        userRepository.save(user);

        //when
        Optional<User> savedUser = userRepository.findUserByUsername(user.getUsername());

        //then
        assertTrue(savedUser.isPresent());
    }

    @DisplayName("delete user test")
    @Test
    public void givenUser_whenDelete_returnRemoveUser(){
        //given
        userRepository.save(user);

        //when
        userRepository.deleteById(user.getId());
        Optional<User> userOptional = userRepository.findById(user.getId());

        //then
        assertThat(userOptional).isEmpty();
    }

    @DisplayName("get user by email native query test")
    @Test
    public void givenUser_whenFindByEmail_returnGivenUser(){
        //given
        userRepository.save(user);

        //when
        Optional<User> savedUser = userRepository.findByEmailNativeSQLNamedParam(user.getEmail());

        //then
        assertTrue(savedUser.isPresent());
    }

    @DisplayName("get users test")
    @Test
    public void givenUsersList_whenFindByFilterCriteria_returnFilteredUsersList(){
        //given
        User anotherUser = User.builder()
                .email("test2@test.pl")
                .firstName("Kuba2")
                .lastName("Test2")
                .username("user_test2")
                .password("password")
                .build();

        userRepository.save(user);
        userRepository.save(anotherUser);

        //when
        Page<User> users = userRepository.findUsersByFilterKeyword("Kuba2", of(0, 10));

        //then
        assertThat(users.getTotalElements()).isEqualTo(1);
    }
}