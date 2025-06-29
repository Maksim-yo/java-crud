package com.crud_api.crud_app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.dto.EmployeeFilterDto;

public interface EmployeeRepositoryCustom {
    Slice<Employee> fetchEmployees(EmployeeFilterDto filterDto, Pageable pageable);
}