package org.example.hotelreservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDTO {
    @NotBlank(message = "Room number is required")
    private String number;

    @NotBlank(message = "Standard is required")
    private String standard;

    @Min(value = 0, message = "Price must be ≥ 0")
    private double price;
}