package com.anas.StudentRestApis.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RefreshTokenRequestDto - Request to refresh expired access token
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequestDto {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
