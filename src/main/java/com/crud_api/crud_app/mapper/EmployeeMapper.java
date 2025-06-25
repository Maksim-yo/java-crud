package com.crud_api.crud_app.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.EmployeeCategory;
import com.crud_api.crud_app.model.dto.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    // Сущность → DTO
    @Mapping(source = "category.name", target = "categoryName")
    EmployeeDto toDto(Employee employee);

    List<EmployeeDto> toDtoList(List<Employee> employees);

    // DTO → Сущность при создании
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Employee toEntity(CreateEmployeeDto dto);

    // Обновление существующей сущности
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEmployee(@MappingTarget Employee employee, UpdateEmployeeDto dto);
}