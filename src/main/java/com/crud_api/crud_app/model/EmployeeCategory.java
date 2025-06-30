package com.crud_api.crud_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class EmployeeCategory {

    @Id
    @GeneratedValue
    private UUID id;

    @NonNull
    private String name;

    @OneToMany(mappedBy = "category")
    @NonNull
    private List<Employee> employees = new ArrayList<>();
}