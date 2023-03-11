package com.kubaokleja.springbootangular.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
/*
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;
    @Mock
    private EmailConfirmationTokenService emailConfirmationTokenService;
    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private RegistrationService registrationService;

    private UserDTO userDTO;

    @BeforeEach
    public void setup(){
        userDTO = UserDTO.builder()
                .username("username")
                .email("test@test.pl")
                .build();
    }

    @Test
    @DisplayName("User registration - positive scenario")
    public void givenUserObject_whenRegisterUser_thenReturnUserObject() throws EmailExistsException, UsernameExistsException {
        //given
        Role role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        User user = User.builder()
                .id(1L)
                .userId(RandomStringUtils.randomNumeric(10))
                .username("username")
                .password("password")
                .email("test@test.pl")
                .firstName("first_name")
                .lastName("last_name")
                .joinDate(new Date())
                .isActive(true)
                .isNotLocked(true)
                .roles(List.of(role))
                .build();

        given(roleRepository.findByName(anyString())).willReturn(role);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        User savedUser = registrationService.register(userDTO);

        //then
        assertThat(savedUser).isNotNull();

    }

    @Test
    @DisplayName("User registration - username exists")
    public void givenUserObject_whenRegisterUserUsernameExists_thenThrowUsernameExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(UsernameExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(UsernameExistsException.class, () ->{
            registrationService.register(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User registration - email exists")
    public void givenUserObject_whenRegisterUserEmailExists_thenThrowEmailExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(EmailExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(EmailExistsException.class, () ->{
            registrationService.register(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

 */
}