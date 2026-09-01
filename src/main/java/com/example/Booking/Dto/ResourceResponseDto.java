package com.example.Booking.Dto;

import java.math.BigDecimal;

public class ResourceResponseDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BigDecimal price;


    public ResourceResponseDto() {
    }

    public ResourceResponseDto(
            Long id,
            String name,
            String description,
            Boolean available,
            BigDecimal price) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
