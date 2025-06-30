package com.crud_api.crud_app.repository;

import com.crud_api.crud_app.model.EmployeeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeCategoryRepository extends JpaRepository<EmployeeCategory, UUID> {

}
