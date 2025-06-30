package com.crud_api.crud_app.controller;

import com.crud_api.crud_app.model.dto.*;
import com.crud_api.crud_app.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    public EmployeeDto create(@Valid @RequestBody CreateEmployeeDto dto) {
        return employeeService.createEmployee(dto);
    }

    @PostMapping("/{id:[0-9a-fA-F\\-]{36}}/update")
    public EmployeeDto update(@Valid @PathVariable UUID id, @Valid @RequestBody UpdateEmployeeDto dto) {
        return employeeService.updateEmployee(id, dto);
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public EmployeeDto getById(@PathVariable UUID id) {
        return employeeService.getEmployeeById(id);
    }

    @PostMapping("/{id:[0-9a-fA-F\\-]{36}}/delete")
    public void delete(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
    }

    @GetMapping("/page")
    public PageResponseDto<EmployeeDto> getEmployeesPage(Pageable pageable, @ModelAttribute EmployeeFilterDto filter) {
        return employeeService.getEmployeesSlice(filter, pageable);
    }
}
