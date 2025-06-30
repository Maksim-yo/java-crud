package com.crud_api.crud_app.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;
import java.util.UUID;

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
