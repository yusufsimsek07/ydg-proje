package com.haccp.audit.controller;

import com.haccp.audit.dto.AuditCreateDTO;
import com.haccp.audit.dto.ResponseUpdateDTO;
import com.haccp.audit.entity.Audit;
import com.haccp.audit.entity.AuditResponse;
import com.haccp.audit.service.AuditService;
import com.haccp.audit.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auditor")
public class AuditController {
    @Autowired
    private AuditService auditService;

    @Autowired
    private FacilityService facilityService;

    @GetMapping("/audits")
    public String listAudits(Authentication authentication, Model model) {
        String username = authentication.getName();
        List<Audit> audits = auditService.findByCreatedBy(username);
        model.addAttribute("audits", audits);
        return "auditor/audits";
    }

    @GetMapping("/audits/new")
    public String newAuditForm(Model model) {
        model.addAttribute("auditDTO", new AuditCreateDTO());
        model.addAttribute("facilities", facilityService.findAll());
        return "auditor/audit-form";
    }

    @PostMapping("/audits")
    public String createAudit(@ModelAttribute AuditCreateDTO dto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Audit audit = auditService.createAudit(dto.getFacilityId(), dto.getAuditDate(), authentication.getName());
            redirectAttributes.addFlashAttribute("message", "Audit created successfully");
            return "redirect:/auditor/audits/" + audit.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating audit: " + e.getMessage());
            return "redirect:/auditor/audits/new";
        }
    }

    @GetMapping("/audits/{id}")
    public String viewAudit(@PathVariable Long id, Model model) {
        var audit = auditService.findById(id)
            .orElseThrow(() -> new RuntimeException("Audit not found"));
        model.addAttribute("audit", audit);
        return "auditor/audit-detail";
    }

    @PostMapping("/audits/{id}/responses")
    public String updateResponses(@PathVariable Long id,
                                  @RequestParam("itemId") List<Long> itemIds,
                                  @RequestParam("result") List<String> results,
                                  @RequestParam(value = "comment", required = false) List<String> comments,
                                  RedirectAttributes redirectAttributes) {
        try {
            for (int i = 0; i < itemIds.size(); i++) {
                Long itemId = itemIds.get(i);
                AuditResponse.ResponseResult result = AuditResponse.ResponseResult.valueOf(results.get(i));
                String comment = (comments != null && i < comments.size()) ? comments.get(i) : null;
                auditService.updateResponse(id, itemId, result, comment);
            }
            redirectAttributes.addFlashAttribute("message", "Responses updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating responses: " + e.getMessage());
        }
        return "redirect:/auditor/audits/" + id;
    }

    @PostMapping("/audits/{id}/complete")
    public String completeAudit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            auditService.completeAudit(id);
            redirectAttributes.addFlashAttribute("message", "Audit completed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error completing audit: " + e.getMessage());
        }
        return "redirect:/auditor/audits/" + id;
    }
}
