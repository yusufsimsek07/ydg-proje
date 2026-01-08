package com.haccp.audit.controller;

import com.haccp.audit.entity.Facility;
import com.haccp.audit.service.FacilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auditor")
public class FacilityController {
    @Autowired
    private FacilityService facilityService;

    @GetMapping("/facilities")
    public String listFacilities(Model model) {
        model.addAttribute("facilities", facilityService.findAll());
        return "auditor/facilities";
    }

    @GetMapping("/facilities/new")
    public String newFacilityForm(Model model) {
        model.addAttribute("facility", new Facility());
        return "auditor/facility-form";
    }

    @PostMapping("/facilities")
    public String createFacility(@ModelAttribute Facility facility, RedirectAttributes redirectAttributes) {
        facilityService.save(facility);
        redirectAttributes.addFlashAttribute("message", "Facility created successfully");
        return "redirect:/auditor/facilities";
    }
}
