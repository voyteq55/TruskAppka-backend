package com.truskappka.truskappka_backend.stand.controller;

import com.truskappka.truskappka_backend.stand.dto.StandAddForm;
import com.truskappka.truskappka_backend.stand.dto.StandDto;
import com.truskappka.truskappka_backend.stand.dto.StandEditForm;
import com.truskappka.truskappka_backend.stand.dto.StandMapMarkerDto;
import com.truskappka.truskappka_backend.stand.service.StandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stands")
@RequiredArgsConstructor
class StandController {

    private final StandService standService;

    @GetMapping
    @Operation(summary = "Get all stands", description = "Retrieves a list of all stands with limited information for map display")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(schema = @Schema(implementation = StandMapMarkerDto.class, type = "array"))),
    })
    List<StandMapMarkerDto> getAllStands() {
        return standService.getAllStands();
    }


    @GetMapping("/filtered")
    @Operation(summary = "Get stands within radius", description = "Returns stands within given radius (km) from specified coordinates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(schema = @Schema(implementation = StandMapMarkerDto.class, type = "array"))),
    })
    public List<StandMapMarkerDto> getStandsWithinRadius(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radiusKm
    ) {
        return standService.getStandsWithinRadius(lat, lon, radiusKm);
    }

    @GetMapping("/{standUuid}")
    @Operation(summary = "Get a stand by UUID", description = "Retrieves a single stand by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval", content = @Content(schema = @Schema(implementation = StandDto.class))),
            @ApiResponse(responseCode = "404", description = "Stand not found"),
    })
    StandDto getStand(@PathVariable UUID standUuid) {
        return standService.getStand(standUuid);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new stand", description = "Creates a new stand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Stand created successfully", content = @Content(schema = @Schema(implementation = StandDto.class))),
            @ApiResponse(responseCode = "400", description = "User is not a vendor"),
            @ApiResponse(responseCode = "409", description = "Stand name is already taken"),

    })
    StandDto createStand(@RequestBody StandAddForm standAddForm) {
        return standService.createStand(standAddForm);
    }

    @PatchMapping("/{standUuid}")
    @Operation(summary = "Update a stand", description = "Updates an existing stand by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stand updated successfully", content = @Content(schema = @Schema(implementation = StandDto.class))),
            @ApiResponse(responseCode = "404", description = "Stand not found"),
            @ApiResponse(responseCode = "409", description = "Stand name is already taken"),
            @ApiResponse(responseCode = "403", description = "User doesn't have access to modify this stand"),
            @ApiResponse(responseCode = "400", description = "User is not a vendor")
    })
    StandDto updateStand(@PathVariable UUID standUuid, @RequestBody StandEditForm standEditForm) {
        return standService.updateStand(standUuid, standEditForm);
    }

    @DeleteMapping("/{standUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a stand", description = "Deletes a stand by its UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Stand deleted successfully"),
            @ApiResponse(responseCode = "400", description = "User is not a vendor"),
    })
    void deleteStand(@PathVariable UUID standUuid) {
        standService.deleteStand(standUuid);
    }
}
