package dev.vibeafrika.pcm.segment.api.rest;

import dev.vibeafrika.pcm.segment.application.dto.SegmentResponse;
import dev.vibeafrika.pcm.segment.application.usecase.GetUserSegmentsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/segments")
@Tag(name = "Segments", description = "User classification and behavioral segments")
@RequiredArgsConstructor
public class SegmentController {

    private final GetUserSegmentsUseCase getUserSegmentsUseCase;

    @GetMapping("/{profileId}")
    @Operation(summary = "Get current segments for a specific profile")
    public SegmentResponse getSegments(@PathVariable UUID profileId) {
        return getUserSegmentsUseCase.execute(new GetUserSegmentsUseCase.Input(profileId));
    }
}
