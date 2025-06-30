package com.crud_api.crud_app.model.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeFilterDto {

    private String fullname;
    private String characteristic;
    private UUID categoryId;
}
