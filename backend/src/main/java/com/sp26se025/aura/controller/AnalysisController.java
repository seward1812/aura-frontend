package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.Requests;
import com.sp26se025.aura.model.AnalysisReport;
import com.sp26se025.aura.service.AnalysisService;
import com.sp26se025.aura.service.InMemoryStore;
import com.sp26se025.aura.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final ReportExportService exportService;
    private final InMemoryStore store;

    public AnalysisController(AnalysisService analysisService, ReportExportService exportService, InMemoryStore store) {
        this.analysisService = analysisService;
        this.exportService = exportService;
        this.store = store;
    }

    @GetMapping
    public List<AnalysisReport> history(@RequestParam(required = false) String patientId,
                                        @RequestParam(required = false) String riskLevel) {
        return store.reports().stream()
                .filter(r -> patientId == null || patientId.equals(r.getPatientId()))
                .filter(r -> riskLevel == null || riskLevel.equalsIgnoreCase(r.getRiskLevel().name()))
                .sorted(Comparator.comparing(AnalysisReport::getCreatedAt).reversed())
                .toList();
    }

    @PostMapping(value = "/predict", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisReport predict(@RequestPart("file") MultipartFile file,
                                  @RequestParam(required = false) String patientId,
                                  @RequestParam(required = false) String clinicId) throws IOException {
        return analysisService.analyze(file, patientId, clinicId);
    }

    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<AnalysisReport> batch(@RequestPart("files") List<MultipartFile> files,
                                      @RequestParam(required = false) String patientId,
                                      @RequestParam(required = false) String clinicId) throws IOException {
        return analysisService.analyzeBatch(files, patientId, clinicId);
    }

    @PatchMapping("/{id}/doctor-review")
    public AnalysisReport review(@PathVariable String id,
                                 @RequestParam(defaultValue = "d-demo") String doctorId,
                                 @RequestBody Requests.DoctorReviewRequest request) {
        return analysisService.review(id, doctorId, request.notes(), request.diagnosis(), request.correctedFinding());
    }

    @PostMapping("/feedback")
    public Map<String, String> feedback(@RequestBody Requests.FeedbackRequest request) {
        return Map.of("message", "Feedback recorded for retraining queue", "reportId", request.reportId());
    }

    @GetMapping("/{id}/export.csv")
    public ResponseEntity<byte[]> csv(@PathVariable String id) {
        AnalysisReport report = store.report(id).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aura-report-" + id + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(exportService.csv(report));
    }

    @GetMapping("/{id}/export.pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable String id) {
        AnalysisReport report = store.report(id).orElseThrow();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aura-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(exportService.pdf(report));
    }
}
