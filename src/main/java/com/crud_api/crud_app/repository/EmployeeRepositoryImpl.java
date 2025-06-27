package com.crud_api.crud_app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.crud_api.crud_app.model.Employee;
import com.crud_api.crud_app.model.QEmployee;
import com.crud_api.crud_app.model.QEmployeeCategory;
import com.crud_api.crud_app.model.dto.EmployeeFilterDto;
import com.crud_api.crud_app.query.EmployeePredicateBuilder;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EmployeePredicateBuilder predicateBuilder;

    @Override
    public Slice<Employee> fetchEmployees(EmployeeFilterDto filterDto, Pageable pageable) {
        QEmployee employee = QEmployee.employee;
        QEmployeeCategory category = QEmployeeCategory.employeeCategory;

        BooleanBuilder predicate = predicateBuilder.build(filterDto);

        List<Employee> results = queryFactory
                .selectFrom(employee)
                .leftJoin(employee.category, category).fetchJoin()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();

        if (hasNext) {
            results.remove(results.size() - 1);
        }

        return new SliceImpl<>(results, pageable, hasNext);

    }

}
