package com.haccp.audit.controller;

import com.haccp.audit.entity.User;
import com.haccp.audit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        var user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("roles", Set.of("ADMIN", "AUDITOR", "MANAGER"));
        return "admin/user-edit";
    }

    @PostMapping("/users/{id}/roles")
    public String updateUserRoles(@PathVariable Long id,
                                  @RequestParam(required = false) Set<String> roles,
                                  RedirectAttributes redirectAttributes) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        userService.updateUserRoles(id, roles);
        redirectAttributes.addFlashAttribute("message", "User roles updated successfully");
        return "redirect:/admin/users";
    }
}
