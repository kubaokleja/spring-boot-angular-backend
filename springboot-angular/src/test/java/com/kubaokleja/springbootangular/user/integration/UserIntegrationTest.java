package com.kubaokleja.springbootangular.user.integration;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserIntegrationTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailConfirmationTokenRepository emailConfirmationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setup() {
        objectMapper.enable(MapperFeature.USE_ANNOTATIONS);
        prepareDefaultUserDTO();
    }

    @BeforeAll
    void preSetup() {
        saveRoles();
    }

    @AfterAll
    void cleanUp() {
        emailConfirmationTokenRepository.deleteAll();
        userRepository.deleteAll();
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

    void saveRoles(){
        Role roleUser = roleRepository.findByName("ROLE_USER");
        if(roleUser == null) {
            roleUser = Role.builder()
                    .name("ROLE_USER")
                    .build();
            roleRepository.save(roleUser);
        }
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN");
        if(roleAdmin == null) {
            roleAdmin = Role.builder()
                    .name("ROLE_ADMIN")
                    .build();
            roleRepository.save(roleAdmin);
        }
    }

    @Test
    @DisplayName("User registration integration test - positive scenario")
    public void givenUserDTOObject_whenRegisterUser_thenReturnSavedUser() throws Exception{
        //given
        //when
        objectMapper.disable(MapperFeature.USE_ANNOTATIONS);
        ResultActions response = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isCreated())
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
        ResultActions response = mockMvc.perform(post("/user/register")
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
        ResultActions response = mockMvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        //then
        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
*/
}
