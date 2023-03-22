package com.kubaokleja.springbootangular.user;

import com.kubaokleja.springbootangular.common.dto.HttpResponse;
import com.kubaokleja.springbootangular.common.helper.CSVHelper;
import com.kubaokleja.springbootangular.exception.EmailExistsException;
import com.kubaokleja.springbootangular.exception.UserNotFoundException;
import com.kubaokleja.springbootangular.exception.UsernameExistsException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController()
@RequestMapping("/user-management")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class UserManagementController {

    private static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";
    private final UserManagementService userManagementService;

    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("")
    ResponseEntity<Page<UserDTO>> getUsers(@RequestParam Optional<String> keyword,
                                                  @RequestParam Optional<Integer> page,
                                                  @RequestParam Optional<Integer> size) {
        Page<UserDTO> users = userManagementService.getUsers(keyword.orElse(""), page.orElse(0), size.orElse(10));
        return new ResponseEntity<>(users, OK);
    }

    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping("")
    ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) throws EmailExistsException, UsernameExistsException {
        UserDTO createdUser = userManagementService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, OK);
    }

    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping("")
    ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) throws EmailExistsException, UserNotFoundException {
        UserDTO updatedUser = userManagementService.updateUser(userDTO);
        return new ResponseEntity<>(updatedUser, OK);
    }

    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("/{userId}")
    ResponseEntity<HttpResponse> deleteUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        userManagementService.deleteUser(userId);
        return new ResponseEntity<>(new HttpResponse(OK.value(), OK, OK.getReasonPhrase().toUpperCase(),
                USER_DELETED_SUCCESSFULLY), OK);
    }

    @PreAuthorize("hasAuthority('user:create')")
    @PostMapping("/upload")
    ResponseEntity<List<UploadUserResultDTO>> uploadFiles(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        if(multipartFile == null || multipartFile.getOriginalFilename() == null || !CSVHelper.hasCSVFormat(multipartFile)) {
            return new ResponseEntity<>(new ArrayList<>(), BAD_REQUEST);
        }
        List<UploadUserResultDTO> result = userManagementService.uploadUserFromCSV(multipartFile);
        return new ResponseEntity<>(result, OK);
    }

}
