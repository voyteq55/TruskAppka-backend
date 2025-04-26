package com.truskappka.truskappka_backend.user.controller;

import com.truskappka.truskappka_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping("/promote-to-vendor/{userUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Promote user to vendor", description = "Promotes a user to vendor role by their UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User promoted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    void promoteUserToVendor(@PathVariable UUID userUuid) {
        userService.setUserAsVendor(userUuid);
    }
}
