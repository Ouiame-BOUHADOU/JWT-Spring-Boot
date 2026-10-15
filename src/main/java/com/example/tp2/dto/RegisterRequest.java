package com.example.tp2.dto;

public record RegisterRequest(
        String email,
        String password
) {
}