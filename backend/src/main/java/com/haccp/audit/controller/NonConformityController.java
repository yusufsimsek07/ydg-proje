package com.haccp.audit.controller;

import com.haccp.audit.dto.NonConformityCreateDTO;
import com.haccp.audit.entity.NonConformity;
import com.haccp.audit.service.AuditService;
import com.haccp.audit.service.NonConformityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auditor")
public class NonConformityController {
    @Autowired
    private NonConformityService nonConformityService;

    @Autowired
    private AuditService auditService;

    @GetMapping("/audits/{auditId}/nonconformities/new")
    public String newNonConformityForm(@PathVariable Long auditId,
                                       @RequestParam Long itemId,
                                       Model model) {
        var audit = auditService.findById(auditId)
            .orElseThrow(() -> new RuntimeException("Audit not found"));
        
        NonConformityCreateDTO dto = new NonConformityCreateDTO();
        dto.setAuditId(auditId);
        dto.setItemId(itemId);
        
        model.addAttribute("ncDTO", dto);
        model.addAttribute("audit", audit);
        model.addAttribute("severities", NonConformity.Severity.values());
        return "auditor/nonconformity-form";
    }

    @PostMapping("/nonconformities")
    public String createNonConformity(@ModelAttribute NonConformityCreateDTO dto, RedirectAttributes redirectAttributes) {
        try {
            nonConformityService.createNonConformity(
                dto.getAuditId(),
                dto.getItemId(),
                dto.getSeverity(),
                dto.getDescription()
            );
            redirectAttributes.addFlashAttribute("message", "NonConformity created successfully");
            return "redirect:/auditor/audits/" + dto.getAuditId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating NonConformity: " + e.getMessage());
            return "redirect:/auditor/audits/" + dto.getAuditId();
        }
    }
}
