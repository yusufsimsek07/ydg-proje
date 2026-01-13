package com.haccp.audit.controller;

import com.haccp.audit.entity.Audit;
import com.haccp.audit.entity.AuditResponse;
import com.haccp.audit.entity.Facility;
import com.haccp.audit.entity.User;
import com.haccp.audit.repository.AuditRepository;
import com.haccp.audit.repository.AuditResponseRepository;
import com.haccp.audit.repository.FacilityRepository;
import com.haccp.audit.repository.UserRepository;
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
class AuditControllerIT {
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
    private AuditResponseRepository responseRepository;

    @Autowired
    private UserRepository userRepository;

    private Facility facility;
    private User auditor;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setName("Test Facility");
        facility = facilityRepository.save(facility);

        auditor = new User();
        auditor.setUsername("auditor");
        auditor.setPasswordHash("password");
        auditor.setFullName("Test Auditor");
        auditor = userRepository.save(auditor);
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void testCreateAudit() throws Exception {
        mockMvc.perform(post("/auditor/audits")
                .param("facilityId", facility.getId().toString())
                .param("auditDate", LocalDate.now().toString())
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "auditor", roles = "AUDITOR")
    void testUpdateResponses() throws Exception {
        Audit audit = new Audit();
        audit.setFacility(facility);
        audit.setAuditDate(LocalDate.now());
        audit.setStatus(Audit.AuditStatus.IN_PROGRESS);
        audit.setCreatedBy(auditor);
        audit = auditRepository.save(audit);

        mockMvc.perform(post("/auditor/audits/" + audit.getId() + "/responses")
                .param("itemId", "1")
                .param("result", "PASS")
                .param("comment", "Test comment")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
