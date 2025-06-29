package com.crud_api.crud_app.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
