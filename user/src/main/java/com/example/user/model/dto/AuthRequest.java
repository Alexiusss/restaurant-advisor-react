package com.example.user.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class AuthRequest {

    @Email
    @NotBlank
    @Size(min = 5, max = 128)
    String email;

    @NotBlank
    @Size(min = 5, max = 128)
    String password;
}