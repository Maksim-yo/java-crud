package com.crud_api.crud_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.EmployeeCategory;
import com.crud_api.crud_app.model.QEmployee;
import com.crud_api.crud_app.model.QEmployeeCategory;
import com.crud_api.crud_app.model.dto.CreateEmployeeDto;
import com.crud_api.crud_app.model.dto.EmployeeDto;
import com.crud_api.crud_app.model.dto.EmployeeFilterDto;
import com.crud_api.crud_app.model.dto.PageResponseDto;
import com.crud_api.crud_app.model.dto.UpdateEmployeeDto;
import com.crud_api.crud_app.query.EmployeePredicateBuilder;
import com.crud_api.crud_app.repository.EmployeeCategoryRepository;
import com.crud_api.crud_app.repository.EmployeeRepository;
import com.crud_api.crud_app.specification.EmployeeSpecifications;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.crud_api.crud_app.mapper.*;

import com.crud_api.crud_app.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final EmployeeCategoryRepository employeeCategoryRepository;
    private final EmployeeMapper employeeMapper; 
    private final EmployeePredicateBuilder predicateBuilder;
    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public PageResponseDto<EmployeeDto> getEmployeesSlice(EmployeeFilterDto filterDto, Pageable pageable) {
        Slice<Employee> slice = employeeRepository.fetchEmployees(filterDto, pageable);

        List<EmployeeDto> content = slice.getContent().stream()
                .map(employeeMapper::toDto)
                .toList();

        return PageResponseDto.<EmployeeDto>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .hasNext(slice.hasNext())
                .build();
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(UUID id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
        return employeeMapper.toDto(employee);
    }
    
    @Transactional
    public EmployeeDto createEmployee(CreateEmployeeDto dto) {

        Employee employee = employeeMapper.toEntity(dto);
        if (dto.getCategoryId() != null) {
        EmployeeCategory category = employeeCategoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found"));
        employee.setCategory(category); 
    }

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    @Transactional
    public EmployeeDto updateEmployee(UUID id, UpdateEmployeeDto dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
         if (dto.getCategoryId() != null) {
            EmployeeCategory category = employeeCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategoryId()));
            existing.setCategory(category); 
        }

        employeeMapper.updateEmployee(existing, dto);

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }

    @Transactional
    public void deleteEmployee(UUID id) {

        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee to delete not found with ID: " + id);
        }

        employeeRepository.deleteById(id);
    }
    
}
