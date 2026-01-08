package com.haccp.audit.controller;

import com.haccp.audit.entity.*;
import com.haccp.audit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration")
@Transactional
class NonConformityControllerIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private ChecklistItemRepository itemRepository;

    @Autowired
    private ChecklistTemplateRepository templateRepository;

    @Autowired
    private NonConformityRepository ncRepository;

    private Audit audit;
    private ChecklistItem item;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility();
        facility.setName("Test Facility");
        facility = facilityRepository.save(facility);

        ChecklistTemplate template = new ChecklistTemplate();
        template.setName("Test Template");
        template.setVersion("1.0");
        template.setActive(true);
        template = templateRepository.save(template);

        item = new ChecklistItem();
        item.setTemplate(template);
        item.setSection("Test Section");
        item.setQuestionText("Test Question");
        item = itemRepository.save(item);

        audit = new Audit();
        audit.setFacility(facility);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus(Audit.AuditStatus.IN_PROGRESS);
        audit = auditRepository.save(audit);
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void testCreateNonConformity() throws Exception {
        mockMvc.perform(post("/auditor/nonconformities")
                .param("auditId", audit.getId().toString())
                .param("itemId", item.getId().toString())
                .param("severity", "HIGH")
                .param("description", "Test NC description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        var ncs = ncRepository.findByAuditId(audit.getId());
        assert ncs.size() > 0;
    }
}
