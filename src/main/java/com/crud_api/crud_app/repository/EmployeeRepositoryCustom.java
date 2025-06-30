package com.crud_api.crud_app.repository;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.dto.EmployeeFilterDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface EmployeeRepositoryCustom {
    Slice<Employee> fetchEmployees(EmployeeFilterDto filterDto, Pageable pageable);

}