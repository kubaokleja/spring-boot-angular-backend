package com.kubaokleja.springbootangular.user;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired RoleRepository roleRepository;


    private UserDTO userDTO;

    @BeforeAll
    void preSetup() {
        saveRoles();
    }

    void saveRoles(){
        Optional<Role> roleUser = roleRepository.findByName("ROLE_USER");
        if(roleUser.isEmpty()) {
            Role role = Role.builder()
                    .name("ROLE_USER")
                    .build();
            roleRepository.save(role);
        }
        Optional<Role> roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        if(roleAdmin.isEmpty()) {
            Role role = Role.builder()
                    .name("ROLE_ADMIN")
                    .build();
            roleRepository.save(role);
        }
    }

    @BeforeEach
    void setup() {
        objectMapper.enable(MapperFeature.USE_ANNOTATIONS);
        prepareDefaultUserDTO();
    }

    private void prepareDefaultUserDTO() {
        userDTO = UserDTO.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("Password1.")
                .username("username")
                .email("test@test.pl")
                .build();
    }

    @Test
    @DisplayName("User registration integration test - positive scenario")
    @WithMockUser(authorities = "user:create")
    public void givenUserDTOObject_whenRegisterUser_thenReturnSavedUser() throws Exception{
        //given
        //when
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        ResultActions response = mockMvc.perform(post("/user-management")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName",
                        is(userDTO.getFirstName())));

    }

    @Test
    @DisplayName("User registration integration test - wrong email format")
    //general test for @Valid annotation
    public void givenUserDTOObject_whenEmailIsWrong_thenThrowException() throws Exception{
        //given
        userDTO.setEmail("wrongemailformat");
        //when
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }


    @Test
    @DisplayName("User registration integration test - wrong password format")
    public void givenUserDTOObject_whenPasswordIsWrong_thenThrowException() throws Exception{
        //given
        userDTO.setPassword("nospecialcharandbigletter");
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        //when
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
