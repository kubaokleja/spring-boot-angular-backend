package com.kubaokleja.springbootangular.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {
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
    private EmailSender emailSender;

    @InjectMocks
    private UserManagementService userManagementService;

    private UserDTO userDTO;
    private Role role;
    private User user;

    @BeforeEach
    public void setup(){
        userDTO = UserDTO.builder()
                .username("username")
                .email("test@test.pl")
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_USER")
                .build();

        user = User.builder()
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
    }

    @Test
    @DisplayName("User management - create user by admin (positive)")
    public void givenUserObject_whenCreateUser_thenReturnUserObject() throws EmailExistsException, UsernameExistsException {
        //given
        given(roleRepository.findByName(anyString())).willReturn(role);
        given(userRepository.save(any(User.class))).willReturn(user);

        //when
        User savedUser = userManagementService.createUser(userDTO);

        //then
        assertThat(savedUser).isNotNull();
    }

    @Test
    @DisplayName("User management - create user by admin. Username already exists.")
    public void givenUserObject_whenUpdateUserUsernameExists_thenThrowUsernameExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(UsernameExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());
        //when
        assertThrows(UsernameExistsException.class, () ->{
            userManagementService.createUser(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User management - create user by admin. Email already exists.")
    public void givenUserObject_whenUpdateUserEmailExists_thenThrowEmailExistException() throws EmailExistsException, UsernameExistsException {
        //given
        doThrow(EmailExistsException.class).when(userValidator).validateUsernameAndEmail(any(), any());

        //when
        assertThrows(EmailExistsException.class, () ->{
            userManagementService.createUser(userDTO);
        });

        //then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User management - update user by admin (positive). ")
    public void givenUserObject_whenUpdateUser_thenReturnUpdatedObject() throws UserNotFoundException, EmailExistsException {
        //given
        given(userRepository.findUserByUsername(userDTO.getUsername())).willReturn(user);
        given(userRepository.save(any(User.class))).willReturn(user);
        userDTO.setFirstName("Updated");

        //when
        User updatedUser = userManagementService.updateUser(userDTO);

        //then
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
    }

    @Test
    @DisplayName("User management - delete user by admin (positive). ")
    public void givenUserId_whenDeleteUser_thenNothing() throws UserNotFoundException {
        //given
        String userId = RandomStringUtils.random(10);
        given(userRepository.findUserByUserId(userId)).willReturn(user);
        willDoNothing().given(userRepository).delete(user);

        //when
        userManagementService.deleteUser(userId);

        //then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("User management - delete user by admin. User not found. ")
    public void givenUserId_whenDeleteUserNotFound_thenThrowUserNotFoundException() throws UserNotFoundException {
        //given
        String userId = RandomStringUtils.random(10);
        given(userRepository.findUserByUserId(userId)).willReturn(null);

        //when
        assertThrows(UserNotFoundException.class, () ->{
            userManagementService.deleteUser(userId);
        });

        //then
        verify(userRepository, never()).delete(any(User.class));
    }
*/
}
