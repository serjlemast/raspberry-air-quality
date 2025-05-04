package com.serjlemast.controller;

import com.serjlemast.model.request.UrlRequest;
import com.serjlemast.service.url.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/urls")
@Tag(name = "URL Management", description = "API for managing a dynamic list of URLs")
public class UrlController {

  private final UrlService urlService;

  @PostMapping
  @Operation(
      summary = "Add one or more URLs",
      description =
          "Saves the given URL(s) into the in-memory storage after validating their format and port range.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "URLs successfully added"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input: URL list empty or invalid URL format"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public ResponseEntity<Set<String>> addUrls(@Valid @RequestBody UrlRequest urls) {
    return ResponseEntity.ok(urlService.addUrls(urls.getUrls()));
  }

  @GetMapping
  @Operation(summary = "Get all saved URLs", description = "Retrieves all URLs currently stored.")
  public ResponseEntity<Set<String>> getUrls() {
    return ResponseEntity.ok(urlService.getUrls());
  }

  @DeleteMapping
  @Operation(summary = "Clear all URLs", description = "Deletes all stored URLs.")
  public ResponseEntity<Void> clearUrls() {
    urlService.clearUrls();
    return ResponseEntity.noContent().build();
  }
}
