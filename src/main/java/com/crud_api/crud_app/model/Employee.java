package com.crud_api.crud_app.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Employee {

  
    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @ElementCollection
    private List<String> characteristics;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private EmployeeCategory category;

    public Employee(String fullName, List<String> characteristics, EmployeeCategory category) {
        this.fullName = fullName;
        this.characteristics = characteristics;
        this.category = category;
    }

}
