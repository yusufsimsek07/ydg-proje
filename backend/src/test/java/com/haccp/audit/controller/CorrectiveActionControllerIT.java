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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("integration")
@Transactional
class CorrectiveActionControllerIT {
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

    @Autowired
    private CorrectiveActionRepository caRepository;

    @Autowired
    private UserRepository userRepository;

    private NonConformity nc;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility();
        facility.setName("Test Facility");
        facility = facilityRepository.save(facility);

        User manager = new User();
        manager.setUsername("manager");
        manager.setPasswordHash("password");
        manager.setFullName("Test Manager");
        manager = userRepository.save(manager);

        ChecklistTemplate template = new ChecklistTemplate();
        template.setName("Test Template");
        template.setVersion("1.0");
        template.setActive(true);
        template = templateRepository.save(template);

        ChecklistItem item = new ChecklistItem();
        item.setTemplate(template);
        item.setSection("Test Section");
        item.setQuestionText("Test Question");
        item = itemRepository.save(item);

        Audit audit = new Audit();
        audit.setFacility(facility);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus(Audit.AuditStatus.IN_PROGRESS);
        audit.setCreatedBy(manager);
        audit = auditRepository.save(audit);

        nc = new NonConformity();
        nc.setAudit(audit);
        nc.setItem(item);
        nc.setSeverity(NonConformity.Severity.HIGH);
        nc.setDescription("Test NC");
        nc.setStatus(NonConformity.NCStatus.OPEN);
        nc = ncRepository.save(nc);
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void testCreateCorrectiveAction() throws Exception {
        mockMvc.perform(post("/manager/corrective-actions")
                .param("nonConformityId", nc.getId().toString())
                .param("ownerName", "John Doe")
                .param("dueDate", LocalDate.now().plusDays(30).toString())
                .param("actionText", "Fix the issue")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        var cas = caRepository.findByNonConformityId(nc.getId());
        assertEquals(1, cas.size());
    }

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    void testMarkAsDone_AndCloseNC() throws Exception {
        CorrectiveAction ca = new CorrectiveAction();
        ca.setNonConformity(nc);
        ca.setOwnerName("John Doe");
        ca.setDueDate(LocalDate.now().plusDays(30));
        ca.setActionText("Fix the issue");
        ca.setStatus(CorrectiveAction.CAStatus.OPEN);
        ca = caRepository.save(ca);

        mockMvc.perform(post("/manager/corrective-actions/" + ca.getId() + "/done")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        ca = caRepository.findById(ca.getId()).orElseThrow();
        assertEquals(CorrectiveAction.CAStatus.DONE, ca.getStatus());
        assertNotNull(ca.getClosedAt());

        nc = ncRepository.findById(nc.getId()).orElseThrow();
        assertTrue(nc.getCorrectiveActions().stream()
                .anyMatch(c -> c.getStatus() == CorrectiveAction.CAStatus.DONE));

        nc.setStatus(NonConformity.NCStatus.CLOSED);
        nc = ncRepository.save(nc);
        assertEquals(NonConformity.NCStatus.CLOSED, nc.getStatus());
    }
}
