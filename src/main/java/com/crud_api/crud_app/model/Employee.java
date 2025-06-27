package com.crud_api.crud_app.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;

    @ElementCollection
    @CollectionTable(
        name = "employee_characteristics",
        joinColumns = @JoinColumn(name = "employee_id")
    )
    @Column(name = "characteristics")
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
