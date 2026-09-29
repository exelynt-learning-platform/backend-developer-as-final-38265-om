package com.example.Booking.Service;

import com.example.Booking.Dto.ResourceRequestDto;
import com.example.Booking.Dto.ResourceResponseDto;
import com.example.Booking.Entity.Resources;
import com.example.Booking.Exception.ResourceInUseException;
import com.example.Booking.Exception.ResourceNotFoundException;
import com.example.Booking.Repository.ReservationRepository;
import com.example.Booking.Repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;

    public ResourceService(ResourceRepository resourceRepository,
                           ReservationRepository reservationRepository) {
        this.resourceRepository = resourceRepository;
        this.reservationRepository = reservationRepository;
    }

    // CREATE
    @Transactional
    public ResourceResponseDto createResource(ResourceRequestDto request) {
        Resources resource = new Resources();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable());
        resource.setPrice(request.getPrice());

        return mapToResponse(resourceRepository.save(resource));
    }

    // READ ALL
    @Transactional(readOnly = true)
    public List<ResourceResponseDto> getAllResources() {
        return resourceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public ResourceResponseDto getResourceById(Long id) {
        return mapToResponse(findOrThrow(id));
    }

    // UPDATE
    @Transactional
    public ResourceResponseDto updateResource(Long id, ResourceRequestDto request) {
        Resources resource = findOrThrow(id);

        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setAvailable(request.getAvailable());
        resource.setPrice(request.getPrice());

        return mapToResponse(resourceRepository.save(resource));
    }

    // DELETE (409 if reservations exist)
    @Transactional
    public void deleteResource(Long id) {
        Resources resource = findOrThrow(id);

        if (reservationRepository.existsByResourceId(id)) {
            throw new ResourceInUseException(
                    "Resource cannot be deleted because it has existing reservations");
        }

        resourceRepository.delete(resource);
    }

    private Resources findOrThrow(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Resource not found with id: " + id));
    }

    // ENTITY -> DTO
    private ResourceResponseDto mapToResponse(Resources resource) {
        return new ResourceResponseDto(
                resource.getId(),
                resource.getName(),
                resource.getDescription(),
                resource.getAvailable(),
                resource.getPrice()
        );
    }
}