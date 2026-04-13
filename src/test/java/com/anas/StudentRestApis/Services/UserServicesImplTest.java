package com.anas.StudentRestApis.Services;

import com.anas.StudentRestApis.Dto.RegisterRequestDto;
import com.anas.StudentRestApis.Dto.UserEntityDto;
import com.anas.StudentRestApis.Entity.Role;
import com.anas.StudentRestApis.Exception.DuplicateResourceException;
import com.anas.StudentRestApis.Exception.InvalidCredentialsException;
import com.anas.StudentRestApis.Service.UserServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserServicesImplTest {
    @Autowired
    private UserServices userServices;

    @Test
    void testRegisterNewUser_success(){
        // given
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("test@example@gmail.com")
                .username("testUser")
                .password("123456")
                .role(Role.TEACHER)
                .build();

        // when
        UserEntityDto result = userServices.register(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testUser");
        assertThat(result.getEmail()).isEqualTo("test@example@gmail.com");
    }

    @Test
    void testRegisterDuplicateEmail_ThrowsException() {
        // Given - User already exists
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userServices.register(duplicateEmailRequest);
        });
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        assertThrows(InvalidCredentialsException.class, () -> {
            userServices.login(invalid);
        });
    }

    @Test
    void testChangePassword_Success() {
        // Change password
        userServices.changePassword(userId, "OldPassword123!", "NewPassword456!");

        // Verify new password works
        assertTrue(userService.validatePassword(username, "NewPassword456!"));
    }
}
