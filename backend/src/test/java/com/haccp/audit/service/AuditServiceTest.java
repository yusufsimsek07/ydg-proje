package com.haccp.audit.service;

import com.haccp.audit.entity.*;
import com.haccp.audit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
    @Mock
    private AuditRepository auditRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChecklistTemplateRepository templateRepository;

    @Mock
    private ChecklistItemRepository itemRepository;

    @Mock
    private AuditResponseRepository responseRepository;

    @InjectMocks
    private AuditService auditService;

    private Facility facility;
    private User user;
    private ChecklistTemplate template;
    private List<ChecklistItem> items;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");

        user = new User();
        user.setId(1L);
        user.setUsername("auditor");

        template = new ChecklistTemplate();
        template.setId(1L);
        template.setActive(true);

        items = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ChecklistItem item = new ChecklistItem();
            item.setId((long) i);
            item.setTemplate(template);
            item.setSection("Section " + i);
            item.setQuestionText("Question " + i);
            items.add(item);
        }
    }

    @Test
    void testCreateAudit_InitializesResponses() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(userRepository.findByUsername("auditor")).thenReturn(Optional.of(user));
        when(templateRepository.findByActiveTrue()).thenReturn(Optional.of(template));
        when(itemRepository.findByTemplateId(1L)).thenReturn(items);
        when(auditRepository.save(any(Audit.class))).thenAnswer(invocation -> {
            Audit audit = invocation.getArgument(0);
            audit.setId(1L);
            return audit;
        });

        Audit audit = auditService.createAudit(1L, LocalDate.now(), "auditor");

        assertNotNull(audit);
        assertEquals(Audit.AuditStatus.DRAFT, audit.getStatus());
        assertEquals(3, audit.getResponses().size());
        verify(auditRepository, times(2)).save(any(Audit.class));
    }

    @Test
    void testUpdateResponse_ChangesStatusToInProgress() {
        Audit audit = new Audit(facility, LocalDate.now(), user);
        audit.setId(1L);
        audit.setStatus(Audit.AuditStatus.DRAFT);

        ChecklistItem item = items.get(0);
        AuditResponse response = new AuditResponse(audit, item);
        audit.getResponses().add(response);

        when(auditRepository.findById(1L)).thenReturn(Optional.of(audit));
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);

        Audit updated = auditService.updateResponse(1L, item.getId(), AuditResponse.ResponseResult.PASS, "Comment");

        assertEquals(Audit.AuditStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(AuditResponse.ResponseResult.PASS, response.getResult());
        assertEquals("Comment", response.getComment());
    }

    @Test
    void testCompleteAudit_ChangesStatusToCompleted() {
        Audit audit = new Audit(facility, LocalDate.now(), user);
        audit.setId(1L);
        audit.setStatus(Audit.AuditStatus.IN_PROGRESS);

        when(auditRepository.findById(1L)).thenReturn(Optional.of(audit));
        when(auditRepository.save(any(Audit.class))).thenReturn(audit);

        Audit completed = auditService.completeAudit(1L);

        assertEquals(Audit.AuditStatus.COMPLETED, completed.getStatus());
        verify(auditRepository).save(audit);
    }
}
