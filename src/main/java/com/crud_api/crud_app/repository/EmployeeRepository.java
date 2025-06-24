package com.crud_api.crud_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

import com.crud_api.crud_app.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByFullName(String fullName);

    List<Employee> findByCategoryId(UUID categoryId);
}
