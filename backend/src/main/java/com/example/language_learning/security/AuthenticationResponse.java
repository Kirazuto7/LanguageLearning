package com.example.language_learning.security;

import com.example.language_learning.user.UserDTO;
import lombok.Builder;


@Builder
public record AuthenticationResponse (
    String token,
    String refreshToken,
    UserDTO user
) {}
