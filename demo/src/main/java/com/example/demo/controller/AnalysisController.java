package com.example.demo.controller;

import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.RetinalImage;
import com.example.demo.entity.Statistics;
import com.example.demo.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @PostMapping("/upload")
    public AnalysisResult uploadImage(
            @RequestParam("file") MultipartFile file
    ) {

        RetinalImage image = new RetinalImage();

        image.setImageUrl(file.getOriginalFilename());

        return analysisService.analyzeImage(image);
    }

    @GetMapping("/history")
    public List<AnalysisResult> getHistory() {
        return analysisService.getAllResults();
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportReport() {

        String csv =
                "ID,Result,Risk Level,Confidence\n" +
                        "1,Diabetic Retinopathy Detected,High,96.5";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=report.csv"
                )
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv);
    }

    @PutMapping("/{id}/confirm")
    public AnalysisResult confirmAnalysis(
            @PathVariable Long id,
            @RequestBody AnalysisResult result
    ) {
        return analysisService.confirmResult(id, result);
    }

    @GetMapping("/statistics")
    public Statistics getStatistics() {
        return analysisService.getStatistics();
    }
}