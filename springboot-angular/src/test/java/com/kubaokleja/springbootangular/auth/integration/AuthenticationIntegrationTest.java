package com.kubaokleja.springbootangular.auth.integration;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubaokleja.springbootangular.user.RoleDTO;
import com.kubaokleja.springbootangular.user.UserDTO;
import com.kubaokleja.springbootangular.user.UserServiceFacade;
import com.kubaokleja.springbootangular.user.enumeration.RoleEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserServiceFacade userServiceFacade;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private UserDTO userDTO;

    @BeforeAll
    void preSetup() {
        userServiceFacade.saveUserRole();
        userDTO = UserDTO.builder()
                .firstName("login")
                .lastName("login")
                .password(passwordEncoder.encode("Password1."))
                .username("loginTest")
                .email("login@login.pl")
                .isActive(true)
                .isNotLocked(true)
                .joinDate(new Date())
                .roles(List.of(RoleDTO.builder()
                        .id(1L)
                        .name(RoleEnum.ROLE_USER.name())
                        .authorities(new ArrayList<>())
                        .build()))
                .build();
        userServiceFacade.save(userDTO);
    }

    @Test
    @DisplayName("User login integration test - positive scenario")
    public void givenUserDTOObject_whenLogin_thenStatusIsOK() throws Exception{
        //given
        userDTO.setPassword("Password1.");
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        //when
        ResultActions response = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Jwt-Token"));
    }

}