package com.example.language_learning.security;

import com.example.language_learning.user.dtos.UserDTO;
import lombok.Builder;


@Builder
public record AuthenticationResponse (
    String token,
    UserDTO user
) {}
