package com.crud_api.crud_app.model.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateEmployeeDto {
    private String fullName;
    private List<@NotBlank(message = "Characteristic cannot be blank") String> characteristics;
    private UUID categoryId;
}
