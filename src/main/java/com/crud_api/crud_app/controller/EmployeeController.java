package com.crud_api.crud_app.controller;

import com.crud_api.crud_app.mapper.EmployeeMapper;
import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.dto.*;
import com.crud_api.crud_app.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @PostMapping("/create")
    public EmployeeDto create(@Valid @RequestBody CreateEmployeeDto dto) {
        return employeeMapper.toDto(employeeService.createEmployee(dto));
    }

    @PostMapping("/{id}/update")
    public EmployeeDto update(@Valid @PathVariable UUID id, @Valid @RequestBody UpdateEmployeeDto dto) {

        return employeeMapper.toDto(employeeService.updateEmployee(id, dto));
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable UUID id) {

        return employeeMapper.toDto(employeeService.getEmployeeById(id));
    }

    @PostMapping("/{id}/delete")
    public void delete(@PathVariable UUID id) {

        employeeService.deleteEmployee(id);
    }

    @GetMapping("/page")
    public PageResponseDto<EmployeeDto> getEmployeesPage(Pageable pageable, EmployeeFilterDto filter) {

        Slice<Employee> slice = employeeService.getEmployeesSlice(filter, pageable);

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
}
