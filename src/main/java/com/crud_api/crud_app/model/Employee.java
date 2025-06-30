package com.crud_api.crud_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Employee {

    @Id
    @GeneratedValue
    private UUID id;

    @NonNull
    private String fullName;

    @ElementCollection
    @NonNull
    private List<String> characteristics;

    @ManyToOne
    @NonNull
    private EmployeeCategory category;
}
