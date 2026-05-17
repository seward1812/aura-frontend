package com.practice.admin.controller;

import com.practice.admin.entity.AIReport;
import com.practice.admin.repository.AIReportRepository;
import com.practice.admin.service.ReportPdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public class AIReportController {

    @Autowired
    private AIReportRepository aiReportRepository;

    @Autowired
    private ReportPdfService reportPdfService;

    // ===== PAGE =====
    @GetMapping("/reports")
    public String reports(Model model) {

        model.addAttribute(
                "reports",
                aiReportRepository.findAll()
        );

        return "reports";
    }

    // ===== SAVE =====
    @PostMapping("/reports/save")
    public String saveReport(AIReport report) {

        aiReportRepository.save(report);

        return "redirect:/reports";
    }

    // ===== DELETE =====
    @GetMapping("/reports/delete/{id}")
    public String deleteReport(@PathVariable Long id) {

        aiReportRepository.deleteById(id);

        return "redirect:/reports";
    }

    // ===== EXPORT PDF =====
    @GetMapping("/reports/pdf/{id}")
    public ResponseEntity<byte[]> exportReportPdf(@PathVariable Long id) {

        Optional<AIReport> reportOptional = aiReportRepository.findById(id);

        if (reportOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AIReport report = reportOptional.get();
        byte[] pdfBytes = reportPdfService.createReportPdf(report);
        String fileName = "ai-report-" + report.getId() + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"; filename*=UTF-8''" +
                                java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
