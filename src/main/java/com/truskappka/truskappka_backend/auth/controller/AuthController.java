package com.truskappka.truskappka_backend.auth.controller;

import com.truskappka.truskappka_backend.auth.dto.TokenDto;
import com.truskappka.truskappka_backend.auth.service.GoogleTokenVerifierService;
import com.truskappka.truskappka_backend.common.exception.InvalidTokenException;
import com.truskappka.truskappka_backend.common.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final GoogleTokenVerifierService tokenVerifierService;
    private final JwtUtil jwtUtil;


    @PostMapping("/access")
    TokenDto authenticateWithGoogle(@RequestParam String idToken) {
        /*   var payload = tokenVerifierService.verifyToken(idToken);
        String email = payload.getEmail();
        boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());*/
        // TODO replace with real verification for deployment with real google client id
        /* For now any token with "email" claim will go through, for example:
         * this token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6Im15QGVtYWlsIn0.qqei_kFbDs8ALnBaOwqXIDg7n-F7sfp4FT_yXmDYLy0
         */
        String email = tokenVerifierService.extractEmailMockImpl(idToken);
        boolean emailVerified = true;
        if (!emailVerified) {
            throw new InvalidTokenException("Email not verified.");
        }

        /* TODO
            if verified we need to check if the user exists in db and if not create record for him/her
            lets also keep email, picture and name stored in db User model for further usage
         */
        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);
        return new TokenDto(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    TokenDto refresh(@RequestParam String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid token");
        }

        var subject = jwtUtil.getSubject(refreshToken);
        var type = jwtUtil.getClaim(refreshToken, "type");
        if (!"refresh".equals(type)) {
            throw new InvalidTokenException("Not a refresh token");
        }

        String accessToken = jwtUtil.generateAccessToken(subject);
        String newRefreshToken = jwtUtil.generateRefreshToken(subject);
        return new TokenDto(accessToken, newRefreshToken);
    }
}