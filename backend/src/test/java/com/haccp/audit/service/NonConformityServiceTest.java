package com.haccp.audit.service;

import com.haccp.audit.entity.*;
import com.haccp.audit.repository.AuditRepository;
import com.haccp.audit.repository.ChecklistItemRepository;
import com.haccp.audit.repository.NonConformityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NonConformityServiceTest {
    @Mock
    private NonConformityRepository nonConformityRepository;

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private ChecklistItemRepository itemRepository;

    @InjectMocks
    private NonConformityService nonConformityService;

    private Audit audit;
    private ChecklistItem item;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");

        User user = new User();
        user.setId(1L);
        user.setUsername("auditor");

        audit = new Audit(facility, LocalDate.now(), user);
        audit.setId(1L);

        ChecklistTemplate template = new ChecklistTemplate();
        template.setId(1L);

        item = new ChecklistItem();
        item.setId(1L);
        item.setTemplate(template);
        item.setQuestionText("Test Question");
    }

    @Test
    void testCreateNonConformity() {
        when(auditRepository.findById(1L)).thenReturn(Optional.of(audit));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(nonConformityRepository.save(any(NonConformity.class))).thenAnswer(invocation -> {
            NonConformity nc = invocation.getArgument(0);
            nc.setId(1L);
            return nc;
        });

        NonConformity nc = nonConformityService.createNonConformity(
            1L, 1L, NonConformity.Severity.HIGH, "Test description"
        );

        assertNotNull(nc);
        assertEquals(NonConformity.NCStatus.OPEN, nc.getStatus());
        assertEquals(NonConformity.Severity.HIGH, nc.getSeverity());
        assertEquals("Test description", nc.getDescription());
        verify(nonConformityRepository).save(any(NonConformity.class));
    }

    @Test
    void testUpdateStatus_ToClosed_WithoutDoneCA_ThrowsException() {
        NonConformity nc = new NonConformity(audit, item, NonConformity.Severity.HIGH, "Test");
        nc.setId(1L);
        nc.setCorrectiveActions(new ArrayList<>());

        when(nonConformityRepository.findById(1L)).thenReturn(Optional.of(nc));

        assertThrows(IllegalStateException.class, () -> {
            nonConformityService.updateStatus(1L, NonConformity.NCStatus.CLOSED);
        });
    }

    @Test
    void testUpdateStatus_ToClosed_WithDoneCA_Succeeds() {
        NonConformity nc = new NonConformity(audit, item, NonConformity.Severity.HIGH, "Test");
        nc.setId(1L);

        CorrectiveAction ca = new CorrectiveAction();
        ca.setStatus(CorrectiveAction.CAStatus.DONE);
        nc.getCorrectiveActions().add(ca);

        when(nonConformityRepository.findById(1L)).thenReturn(Optional.of(nc));
        when(nonConformityRepository.save(any(NonConformity.class))).thenReturn(nc);

        NonConformity updated = nonConformityService.updateStatus(1L, NonConformity.NCStatus.CLOSED);

        assertEquals(NonConformity.NCStatus.CLOSED, updated.getStatus());
        verify(nonConformityRepository).save(nc);
    }
}
