package com.kubaokleja.springbootangular.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.kubaokleja.springbootangular.annotation.Password;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{

    public static final String NUMBER_AND_LETTER_REGEX = "[A-Za-z0-9]+";

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long id;
    private String userId;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "First name is mandatory")
    @Size(max = 255, message = "Max size is 255")
    private String firstName;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 255, message = "Max size is 255")
    private String lastName;
    @Pattern(regexp = NUMBER_AND_LETTER_REGEX, message = "Only letters and numbers are allowed")
    @NotBlank(message = "Username is mandatory")
    @Size(min = 4, max = 20, message = "Valid username size is between 4 and 20")
    private String username;
    @Password
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;
    private Date lastLoginDate;
    private Date lastLoginToDisplay;
    private Date joinDate;
    private Boolean isActive;
    private Boolean isNotLocked;
    private LocalDateTime expirationDate;
    private Collection<RoleDTO> roles;
}
