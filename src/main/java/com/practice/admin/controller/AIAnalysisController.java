package com.practice.admin.controller;

import com.practice.admin.entity.AIAnalysis;
import com.practice.admin.repository.AIAnalysisRepository;
import com.practice.admin.service.GeminiDiagnosisService;
import com.practice.admin.service.GeminiDiagnosisService.DiagnosisResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class AIAnalysisController {

    @Autowired
    private AIAnalysisRepository aiAnalysisRepository;

    @Autowired
    private GeminiDiagnosisService geminiDiagnosisService;

    @GetMapping("/analysis")
    public String analysis(Model model) {
        model.addAttribute("analysisList", aiAnalysisRepository.findAll());
        return "analysis";
    }

    @PostMapping("/analysis/save")
    public String saveAnalysis(
            @RequestParam("patientName") String patientName,
            @RequestParam("createdDate") String createdDate,
            @RequestParam("aiResult") String aiResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        DiagnosisResult diagnosisResult;

        try {
            diagnosisResult = geminiDiagnosisService.analyzeRetinaImage(imageFile, aiResult);
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Khong the phan tich bang Gemini: " + exception.getMessage());
            return "redirect:/analysis";
        }

        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";
        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        File saveFile = new File(uploadDir + fileName);
        imageFile.transferTo(saveFile);

        AIAnalysis analysis = new AIAnalysis();
        analysis.setPatientName(patientName);
        analysis.setCreatedDate(createdDate);
        analysis.setImageUrl("/uploads/" + fileName);
        analysis.setDisease(diagnosisResult.disease());
        analysis.setRiskLevel(diagnosisResult.riskLevel());
        analysis.setRecommendation(diagnosisResult.recommendation());
        analysis.setAiVersion(diagnosisResult.aiVersion());
        analysis.setAiResult(StringUtils.hasText(aiResult) ? aiResult : diagnosisResult.aiResult());

        aiAnalysisRepository.save(analysis);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Gemini da phan tich anh thanh cong.");

        return "redirect:/analysis";
    }

    @GetMapping("/analysis/delete/{id}")
    public String delete(@PathVariable Long id) {
        aiAnalysisRepository.deleteById(id);
        return "redirect:/analysis";
    }
}
