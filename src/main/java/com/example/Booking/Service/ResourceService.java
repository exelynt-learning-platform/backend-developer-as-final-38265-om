package com.example.Booking.Service;

import com.example.Booking.Dto.ResourceRequestDto;
import com.example.Booking.Dto.ResourceResponseDto;
import com.example.Booking.Entity.Resources;
import com.example.Booking.Repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(
            ResourceRepository resourceRepository) {

        this.resourceRepository = resourceRepository;
    }

    // CREATE
    public ResourceResponseDto createResource(
            ResourceRequestDto request) {

        Resources resource = new Resources();

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable());
        resource.setPrice(request.getPrice());

        Resources saved =
                resourceRepository.save(resource);

        return mapToResponse(saved);
    }

    // READ ALL
    public List<ResourceResponseDto> getAllResources() {

        return resourceRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // READ BY ID
    public ResourceResponseDto getResourceById(
            Long id) {

        Resources resource =
                resourceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        return mapToResponse(resource);
    }

    // UPDATE
    public ResourceResponseDto updateResource(
            Long id,
            ResourceRequestDto request) {

        Resources resource =
                resourceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable());
        resource.setPrice(request.getPrice());

        Resources updated =
                resourceRepository.save(resource);

        return mapToResponse(updated);
    }

    // DELETE
    public void deleteResource(Long id) {

        Resources resource =
                resourceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Resource not found"));

        resourceRepository.delete(resource);
    }

    // ENTITY → DTO
    private ResourceResponseDto mapToResponse(
            Resources resource) {

        return new ResourceResponseDto(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getAvailable(),
                resource.getPrice()
        );
    }
}
