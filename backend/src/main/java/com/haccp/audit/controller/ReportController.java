package com.haccp.audit.controller;

import com.haccp.audit.entity.Audit;
import com.haccp.audit.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/audits/{id}")
    public String viewReport(@PathVariable Long id, Model model) {
        var audit = reportService.getAuditReport(id)
            .orElseThrow(() -> new RuntimeException("Audit not found"));
        model.addAttribute("audit", audit);
        return "reports/audit-report";
    }

    @GetMapping("/audits/{id}/export")
    public void exportCSV(@PathVariable Long id, HttpServletResponse response) throws Exception {
        var audit = reportService.getAuditReport(id)
            .orElseThrow(() -> new RuntimeException("Audit not found"));

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=audit-report-" + id + ".csv");
        
        PrintWriter writer = response.getWriter();
        writer.println("Audit Report");
        writer.println("Facility," + audit.getFacility().getName());
        writer.println("Date," + audit.getAuditDate().format(DateTimeFormatter.ISO_DATE));
        writer.println("Status," + audit.getStatus());
        writer.println();
        writer.println("Checklist Results");
        writer.println("Section,Question,Result,Comment");
        
        for (var resp : audit.getResponses()) {
            writer.println(String.format("%s,\"%s\",%s,\"%s\"",
                resp.getItem().getSection(),
                resp.getItem().getQuestionText().replace("\"", "\"\""),
                resp.getResult(),
                resp.getComment() != null ? resp.getComment().replace("\"", "\"\"") : ""
            ));
        }
        
        writer.println();
        writer.println("Non-Conformities");
        writer.println("Item,Severity,Status,Description");
        for (var nc : audit.getNonConformities()) {
            writer.println(String.format("\"%s\",%s,%s,\"%s\"",
                nc.getItem().getQuestionText().replace("\"", "\"\""),
                nc.getSeverity(),
                nc.getStatus(),
                nc.getDescription().replace("\"", "\"\"")
            ));
        }
        
        writer.println();
        writer.println("Corrective Actions");
        writer.println("NonConformity,Owner,Due Date,Action,Status");
        for (var nc : audit.getNonConformities()) {
            for (var ca : nc.getCorrectiveActions()) {
                writer.println(String.format("\"%s\",%s,%s,\"%s\",%s",
                    nc.getItem().getQuestionText().replace("\"", "\"\""),
                    ca.getOwnerName(),
                    ca.getDueDate().format(DateTimeFormatter.ISO_DATE),
                    ca.getActionText().replace("\"", "\"\""),
                    ca.getStatus()
                ));
            }
        }
        
        writer.flush();
    }
}
