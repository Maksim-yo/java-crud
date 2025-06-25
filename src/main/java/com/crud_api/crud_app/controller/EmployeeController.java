package com.crud_api.crud_app.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.crud_api.crud_app.service.EmployeeService;
import com.crud_api.crud_app.model.dto.CreateEmployeeDto;
import com.crud_api.crud_app.model.dto.EmployeeDto;
import com.crud_api.crud_app.model.dto.UpdateEmployeeDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody CreateEmployeeDto dto) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }

    @PostMapping("/{id}/update")
    public ResponseEntity<EmployeeDto> update(@Valid @PathVariable UUID id, @Valid @RequestBody UpdateEmployeeDto dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public ResponseEntity<EmployeeDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/page")
    public Page<EmployeeDto> getEmployeesPage(
            Pageable pageable,
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String characteristic,
            @RequestParam(required = false) UUID categoryId
    ) {
        return employeeService.getEmployeesPage(fullname, characteristic, categoryId, pageable);
    }


  

}
