package com.truskappka.truskappka_backend.opinion.controller;

import com.truskappka.truskappka_backend.opinion.dto.AverageRatingDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionAddForm;
import com.truskappka.truskappka_backend.opinion.dto.OpinionDto;
import com.truskappka.truskappka_backend.opinion.dto.OpinionEditForm;
import com.truskappka.truskappka_backend.opinion.service.OpinionService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/opinions")
@RequiredArgsConstructor
class OpinionController {

    private final OpinionService opinionService;

    @Operation(summary = "Get all opinions for a stand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved opinions"),
            @ApiResponse(responseCode = "404", description = "Stand not found")
    })
    @GetMapping("/stand/{standUuid}")
    List<OpinionDto> getOpinionsForStand(@PathVariable UUID standUuid) {
        return opinionService.getOpinionsForStand(standUuid);
    }

    @Operation(summary = "Get average ratings for a stand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated average ratings"),
            @ApiResponse(responseCode = "404", description = "Stand not found")
    })
    @GetMapping("/stand/{standUuid}/average")
    AverageRatingDto getAverageRatingsForStand(@PathVariable UUID standUuid) {
        return opinionService.calculateAverageRatings(standUuid);
    }

    @Operation(summary = "Add a new opinion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Opinion created successfully"),
            @ApiResponse(responseCode = "404", description = "Stand or tag not found")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OpinionDto addOpinion(@RequestBody OpinionAddForm opinionAddForm) {
        return opinionService.addOpinion(opinionAddForm);
    }

    @Operation(
            summary = "Update an existing opinion",
            description = "Updates fields of an opinion. Fields 'qualityRating', 'serviceRating', 'priceRating' and 'comment' are optional — if not provided, they will remain unchanged. " +
                    "However, the 'tagNames' list is mandatory and must be provided every time (even if no changes to tags are intended)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opinion updated successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to edit this opinion"),
            @ApiResponse(responseCode = "404", description = "Opinion or tag not found")
    })
    @PatchMapping("/{opinionUuid}")
    OpinionDto updateOpinion(@PathVariable UUID opinionUuid,
                             @RequestBody OpinionEditForm opinionEditForm) {
        return opinionService.updateOpinion(opinionUuid, opinionEditForm);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Opinion deleted successfully"),
            @ApiResponse(responseCode = "403", description = "User not authorized to delete this opinion"),
            @ApiResponse(responseCode = "404", description = "Opinion not found")
    })
    @DeleteMapping("/{opinionUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteOpinion(@PathVariable UUID opinionUuid) {
        opinionService.deleteOpinion(opinionUuid);
    }
}
