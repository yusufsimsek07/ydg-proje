package com.haccp.audit.service;

import com.haccp.audit.entity.*;
import com.haccp.audit.repository.CorrectiveActionRepository;
import com.haccp.audit.repository.NonConformityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorrectiveActionServiceTest {
    @Mock
    private CorrectiveActionRepository correctiveActionRepository;

    @Mock
    private NonConformityRepository nonConformityRepository;

    @InjectMocks
    private CorrectiveActionService correctiveActionService;

    private NonConformity nc;

    @BeforeEach
    void setUp() {
        Facility facility = new Facility();
        facility.setId(1L);

        User user = new User();
        user.setId(1L);

        Audit audit = new Audit(facility, LocalDate.now(), user);
        audit.setId(1L);

        ChecklistItem item = new ChecklistItem();
        item.setId(1L);

        nc = new NonConformity(audit, item, NonConformity.Severity.HIGH, "Test");
        nc.setId(1L);
    }

    @Test
    void testCreateCorrectiveAction() {
        when(nonConformityRepository.findById(1L)).thenReturn(Optional.of(nc));
        when(correctiveActionRepository.save(any(CorrectiveAction.class))).thenAnswer(invocation -> {
            CorrectiveAction ca = invocation.getArgument(0);
            ca.setId(1L);
            return ca;
        });

        CorrectiveAction ca = correctiveActionService.createCorrectiveAction(
            1L, "John Doe", LocalDate.now().plusDays(30), "Fix the issue"
        );

        assertNotNull(ca);
        assertEquals(CorrectiveAction.CAStatus.OPEN, ca.getStatus());
        assertEquals("John Doe", ca.getOwnerName());
        verify(correctiveActionRepository).save(any(CorrectiveAction.class));
    }

    @Test
    void testMarkAsDone_SetsStatusAndClosedAt() {
        CorrectiveAction ca = new CorrectiveAction();
        ca.setId(1L);
        ca.setNonConformity(nc);
        ca.setStatus(CorrectiveAction.CAStatus.OPEN);

        when(correctiveActionRepository.findById(1L)).thenReturn(Optional.of(ca));
        when(correctiveActionRepository.save(any(CorrectiveAction.class))).thenReturn(ca);

        CorrectiveAction done = correctiveActionService.markAsDone(1L);

        assertEquals(CorrectiveAction.CAStatus.DONE, done.getStatus());
        assertNotNull(done.getClosedAt());
        verify(correctiveActionRepository).save(ca);
    }
}
