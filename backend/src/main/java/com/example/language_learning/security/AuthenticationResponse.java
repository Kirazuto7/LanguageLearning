package com.example.language_learning.security;

import com.example.language_learning.dto.user.UserDTO;
import lombok.Builder;


@Builder
public record AuthenticationResponse (
    String token,
    UserDTO user
) {}
