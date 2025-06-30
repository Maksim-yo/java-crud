package com.crud_api.crud_app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @ElementCollection
    private List<String> characteristics;

    @ManyToOne
    private EmployeeCategory category;

    public Employee(String fullName, List<String> characteristics, EmployeeCategory category) {
        this.fullName = fullName;
        this.characteristics = characteristics;
        this.category = category;
    }
}
