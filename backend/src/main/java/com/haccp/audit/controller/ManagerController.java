package com.haccp.audit.controller;

import com.haccp.audit.dto.CorrectiveActionCreateDTO;
import com.haccp.audit.entity.NonConformity;
import com.haccp.audit.service.CorrectiveActionService;
import com.haccp.audit.service.NonConformityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private NonConformityService nonConformityService;

    @Autowired
    private CorrectiveActionService correctiveActionService;

    @GetMapping("/nonconformities")
    public String listNonConformities(Model model) {
        model.addAttribute("nonConformities", nonConformityService.findAll());
        return "manager/nonconformities";
    }

    @GetMapping("/nonconformities/{id}")
    public String viewNonConformity(@PathVariable Long id, Model model) {
        var nc = nonConformityService.findById(id)
            .orElseThrow(() -> new RuntimeException("NonConformity not found"));
        model.addAttribute("nc", nc);
        model.addAttribute("correctiveActions", correctiveActionService.findByNonConformityId(id));
        model.addAttribute("caDTO", new CorrectiveActionCreateDTO());
        model.addAttribute("statuses", NonConformity.NCStatus.values());
        return "manager/nonconformity-detail";
    }

    @PostMapping("/nonconformities/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam NonConformity.NCStatus status,
                               RedirectAttributes redirectAttributes) {
        try {
            nonConformityService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating status: " + e.getMessage());
        }
        return "redirect:/manager/nonconformities/" + id;
    }

    @PostMapping("/corrective-actions")
    public String createCorrectiveAction(@ModelAttribute CorrectiveActionCreateDTO dto, RedirectAttributes redirectAttributes) {
        try {
            correctiveActionService.createCorrectiveAction(
                dto.getNonConformityId(),
                dto.getOwnerName(),
                dto.getDueDate(),
                dto.getActionText()
            );
            redirectAttributes.addFlashAttribute("message", "CorrectiveAction created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating CorrectiveAction: " + e.getMessage());
        }
        return "redirect:/manager/nonconformities/" + dto.getNonConformityId();
    }

    @PostMapping("/corrective-actions/{id}/done")
    public String markAsDone(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var ca = correctiveActionService.markAsDone(id);
            redirectAttributes.addFlashAttribute("message", "CorrectiveAction marked as DONE");
            return "redirect:/manager/nonconformities/" + ca.getNonConformity().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error marking as done: " + e.getMessage());
            return "redirect:/manager/nonconformities";
        }
    }
}
