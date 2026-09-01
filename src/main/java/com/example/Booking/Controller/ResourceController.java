package com.example.Booking.Controller;

import com.example.Booking.Dto.ResourceRequestDto;
import com.example.Booking.Dto.ResourceResponseDto;
import com.example.Booking.Service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(
            ResourceService resourceService) {

        this.resourceService = resourceService;
    }

    // USER + ADMIN
    // READ ALL RESOURCES
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<ResourceResponseDto>> getAllResources() {

        return ResponseEntity.ok(
                resourceService.getAllResources()
        );
    }

    // USER + ADMIN
    // READ RESOURCE BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ResourceResponseDto> getResourceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                resourceService.getResourceById(id)
        );
    }

    // ADMIN ONLY
    // CREATE RESOURCE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponseDto> createResource(
            @Valid @RequestBody ResourceRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resourceService.createResource(request));
    }

    // ADMIN ONLY
    // UPDATE RESOURCE
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponseDto> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequestDto request) {

        return ResponseEntity.ok(
                resourceService.updateResource(
                        id,
                        request
                )
        );
    }

    // ADMIN ONLY
    // DELETE RESOURCE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long id) {

        resourceService.deleteResource(id);

        return ResponseEntity.noContent().build();
    }
}