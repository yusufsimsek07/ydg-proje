package com.haccp.audit.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isAuditor = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_AUDITOR"));
        boolean isManager = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"));
        
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isAuditor", isAuditor);
        model.addAttribute("isManager", isManager);
        
        return "dashboard";
    }
}
