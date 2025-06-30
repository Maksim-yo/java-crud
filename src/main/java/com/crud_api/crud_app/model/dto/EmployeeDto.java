package com.crud_api.crud_app.model.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {

    private UUID id;
    private String fullName;
    private List<String> characteristics;
    private String categoryName;
}
