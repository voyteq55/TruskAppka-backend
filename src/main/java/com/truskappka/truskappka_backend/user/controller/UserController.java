package com.truskappka.truskappka_backend.user.controller;

import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.user.dto.IsVendorDto;
import com.truskappka.truskappka_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping("/promote-to-vendor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Promote user to vendor", description = "Promotes a user to vendor role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User promoted successfully"),
    })
    void promoteUserToVendor() {
        userService.setUserAsVendor();
    }

    @GetMapping("/is-vendor")
    IsVendorDto isUserVendor() {
        return userService.isVendor();
    }

    @GetMapping("/opinions")
    List<OpinionDto> getUserOpinions() {
        return userService.getUserOpinions();
    }
}
