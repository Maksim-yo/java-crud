package com.crud_api.crud_app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.EmployeeCategory;
import com.crud_api.crud_app.model.dto.CreateEmployeeDto;
import com.crud_api.crud_app.model.dto.EmployeeDto;
import com.crud_api.crud_app.model.dto.UpdateEmployeeDto;
import com.crud_api.crud_app.repository.EmployeeCategoryRepository;
import com.crud_api.crud_app.repository.EmployeeRepository;
import com.crud_api.crud_app.specification.EmployeeSpecifications;
import com.crud_api.crud_app.mapper.*;

import com.crud_api.crud_app.exception.NotFoundException;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeCategoryRepository employeeCategoryRepository;

    private final EmployeeMapper employeeMapper; // если используешь маппер DTO

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeCategoryRepository employeeCategoryRepository, EmployeeMapper employeeMapper) {

        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeCategoryRepository = employeeCategoryRepository;
    }

    public Page<EmployeeDto> getEmployeesPage(String fullname, String characteristic, UUID categoryId, Pageable pageable) {
        Specification<Employee> spec = null;

        if (fullname != null && !fullname.isBlank()) {
            spec = EmployeeSpecifications.fullNameContains(fullname);
        }

        if (characteristic != null && !characteristic.isBlank()) {
            spec = spec == null
                    ? EmployeeSpecifications.hasCharacteristic(characteristic)
                    : spec.and(EmployeeSpecifications.hasCharacteristic(characteristic));
        }

        if (categoryId != null) {
            spec = spec == null
                    ? EmployeeSpecifications.hasCategoryId(categoryId)
                    : spec.and(EmployeeSpecifications.hasCategoryId(categoryId));
        }
        return employeeRepository.findAll(spec, pageable)
                .map(employeeMapper::toDto);
    }

    public EmployeeDto getEmployeeById(UUID id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
        return employeeMapper.toDto(employee);
    }
    
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

    
    public EmployeeDto updateEmployee(UUID id, UpdateEmployeeDto dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The employee not found: " + id));
        EmployeeCategory category = null;
        if (dto.getCategoryId() != null) {
            category = employeeCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.getCategoryId()));
        }

        existing.setCategory(category);
        employeeMapper.updateEmployee(existing, dto);

        Employee updated = employeeRepository.save(existing);
        return employeeMapper.toDto(updated);
    }

    
    public void deleteEmployee(UUID id) {

        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee to delete not found with ID: " + id);
        }

        employeeRepository.deleteById(id);
    }
    
}
