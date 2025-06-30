package com.crud_api.crud_app;

import com.crud_api.crud_app.model.dto.CreateEmployeeDto;
import com.crud_api.crud_app.model.dto.EmployeeDto;
import com.crud_api.crud_app.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DBRider
@AutoConfigureWebTestClient
@Testcontainers
public class EmployeeCreationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);

    }

    private CreateEmployeeDto buildCreateEmployeeDto(String expectedFullName,
                                                     List<String> expectedCharacteristics,
                                                     UUID expectedCategoryID) {
        CreateEmployeeDto dto = new CreateEmployeeDto();
        dto.setFullName(expectedFullName);
        dto.setCharacteristics(expectedCharacteristics);
        dto.setCategoryId(expectedCategoryID);
        return dto;
    }

    @Test
    @ExpectedDataSet(value = "datasets/expected_employee.yml", ignoreCols = {"id", "employee_id"})
    @DataSet(value = "datasets/employee_category.yml", disableConstraints = true)
    @DBUnit(caseSensitiveTableNames = true)
    public void createEmployeeReturn() throws Exception {

        String fullName = "John Doe";
        List<String> characteristics = List.of("Hardworking", "Team player");
        UUID categoryID = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String categoryName = "Менеджмент";
        String apiUrl = "/employee/create";
        CreateEmployeeDto createDto = buildCreateEmployeeDto(fullName, characteristics, categoryID);

        EmployeeDto responseDto = webTestClient.post()
                                               .uri("/employee/create")
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .bodyValue(objectMapper.writeValueAsString(createDto))
                                               .exchange()
                                               .expectStatus().is2xxSuccessful()
                                               .expectBody(EmployeeDto.class)
                                               .returnResult()
                                               .getResponseBody();

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getFullName()).isEqualTo(fullName);
        assertThat(responseDto.getCharacteristics())
                .containsExactlyInAnyOrder("Hardworking", "Team player");
        assertThat(responseDto.getCategoryName()).isEqualTo(categoryName);
        UUID employeeId = responseDto.getId();
        employeeRepository.findById(employeeId)
                          .orElseThrow(() -> new AssertionError("Employee not found in DB"));
        Assertions.assertNotNull(employeeId, "Returned employee ID must not be null");

    }
}
