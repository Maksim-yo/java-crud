package com.crud_api.crud_app.query;

import org.springframework.stereotype.Component;

import com.crud_api.crud_app.model.QEmployee;
import com.crud_api.crud_app.model.dto.EmployeeFilterDto;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeePredicateBuilder {

    private final QEmployee employee = QEmployee.employee;

    public BooleanBuilder build(EmployeeFilterDto filterDto) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (filterDto.getFullname() != null && !filterDto.getFullname().isBlank()) {
            predicate.and(employee.fullName.containsIgnoreCase(filterDto.getFullname()));
        }

        if (filterDto.getCharacteristic() != null && !filterDto.getCharacteristic().isBlank()) {
            predicate.and(employee.characteristics.any().equalsIgnoreCase(filterDto.getCharacteristic()));
        }

        if (filterDto.getCategoryId() != null) {    
            predicate.and(employee.category.id.eq(filterDto.getCategoryId()));
        }

        return predicate;
    }
}
