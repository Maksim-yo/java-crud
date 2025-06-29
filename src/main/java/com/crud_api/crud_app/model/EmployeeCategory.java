package com.crud_api.crud_app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_category")
@Getter
@Setter
@NoArgsConstructor
public class EmployeeCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "category")
    private List<Employee> employees = new ArrayList<>();

    public EmployeeCategory(String name) {
        this.name = name;
    }

}