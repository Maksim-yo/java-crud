package com.crud_api.crud_app.service;

import com.crud_api.crud_app.exception.NotFoundException;
import com.crud_api.crud_app.mapper.EmployeeMapper;
import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.EmployeeCategory;
import com.crud_api.crud_app.model.dto.*;
import com.crud_api.crud_app.query.EmployeePredicateBuilder;
import com.crud_api.crud_app.repository.EmployeeCategoryRepository;
import com.crud_api.crud_app.repository.EmployeeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeCategoryRepository employeeCategoryRepository;
    private final EmployeeMapper employeeMapper;
    private final EmployeePredicateBuilder predicateBuilder;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public Slice<Employee> getEmployeesSlice(EmployeeFilterDto filterDto, Pageable pageable) {

        return employeeRepository.fetchEmployees(filterDto, pageable);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(UUID id) {

        return employeeRepository.findById(id)
                                 .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
    }

    @Transactional
    public Employee createEmployee(CreateEmployeeDto dto) {

        Employee employee = employeeMapper.toEntity(dto);
        if (dto.getCategoryId() != null) {
            EmployeeCategory category = employeeCategoryRepository.findById(dto.getCategoryId())
                                                                  .orElseThrow(() -> new NotFoundException("Category not found"));
            employee.setCategory(category);
        }
// То что Slice содержит информацию только о текущей страницы и есть ли следующая, Page содержит инфу о всех страницах
// и slice делает 1 запрос в бд а page 2 второй - count
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(UUID id, UpdateEmployeeDto dto) {

        Employee existing = employeeRepository.findById(id)
                                              .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
        if (dto.getCategoryId() != null) {
            EmployeeCategory category = employeeCategoryRepository.findById(dto.getCategoryId())
                                                                  .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        employeeMapper.updateEmployee(existing, dto);
        return employeeRepository.save(existing);
    }

    @Transactional
    public void deleteEmployee(UUID id) {

        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee to delete not found with ID: " + id);
        }

        employeeRepository.deleteById(id);
    }
}
