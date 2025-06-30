package com.crud_api.crud_app.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEmployeeDto {

    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotNull(message = "Characteristics list cannot be null")
    private List<@NotBlank(message = "Characteristic cannot be blank") String> characteristics;
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
}
