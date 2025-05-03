package com.truskappka.truskappka_backend.auth.controller;

import com.truskappka.truskappka_backend.auth.dto.TokenDto;
import com.truskappka.truskappka_backend.auth.dto.TokenVerificationForm;
import com.truskappka.truskappka_backend.auth.service.AuthService;
import com.truskappka.truskappka_backend.auth.service.GoogleTokenVerifierService;
import com.truskappka.truskappka_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final GoogleTokenVerifierService tokenVerifierService;
    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/access")
    TokenDto authenticateWithGoogle(@RequestBody TokenVerificationForm body) {
        // TODO replace with real verification for deployment with real google client id

        /*   var payload = tokenVerifierService.verifyToken(idToken);
         *   String email = payload.getEmail();
         */

        /* For now any token with "email" claim will go through, for example:
         * this token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6Im15QGVtYWlsIn0.qqei_kFbDs8ALnBaOwqXIDg7n-F7sfp4FT_yXmDYLy0
         */
        String email = tokenVerifierService.verifyTokenMock(body.token());
        String userId = userService.findOrCreateByEmail(email).toString();
        return authService.generateToken(userId);
    }

    @PostMapping("/refresh")
    TokenDto refresh(@RequestBody TokenVerificationForm body) {
        return authService.getRefreshToken(body.token());
    }
}