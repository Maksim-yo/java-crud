package com.crud_api.crud_app.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.crud_api.crud_app.model.Employee;

@Repository
public interface EmployeeRepository
        extends JpaRepository<Employee, UUID>, JpaSpecificationExecutor<Employee>, EmployeeRepositoryCustom {

}
