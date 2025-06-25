package com.crud_api.crud_app.specification;

import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

import com.crud_api.crud_app.model.Employee;

public class EmployeeSpecifications {

    public static Specification<Employee> fullNameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Employee> hasCharacteristic(String characteristic) {
        return (root, query, cb) -> {
            // Джойним коллекцию характеристик
            Join<Object, Object> characteristicsJoin = root.join("characteristics");
            return cb.equal(
                        cb.lower(characteristicsJoin.as(String.class)),
                        characteristic.toLowerCase()
                    );        
            };
        }

    public static Specification<Employee> hasCategoryId(UUID categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
    }
}
