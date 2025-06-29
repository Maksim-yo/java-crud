package com.crud_api.crud_app.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crud_api.crud_app.model.EmployeeCategory;

@Repository
public interface EmployeeCategoryRepository extends JpaRepository<EmployeeCategory, UUID> {

}
