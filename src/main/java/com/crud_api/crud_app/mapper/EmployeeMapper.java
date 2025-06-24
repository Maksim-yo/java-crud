package com.crud_api.crud_app.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.dto.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Сущность → DTO
    @Mapping(source = "category.name", target = "categoryName")
    EmployeeDto toDto(Employee employee);

    List<EmployeeDto> toDtoList(List<Employee> employees);

    // DTO → Сущность при создании
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    Employee toEntity(CreateEmployeeDto dto);

    // Обновление существующей сущности
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "categoryId", target = "category.id")
    void updateEmployee(@MappingTarget Employee employee, UpdateEmployeeDto dto);
}