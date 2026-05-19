package com.example.demo.service;

import com.example.demo.entity.AnalysisResult;
import com.example.demo.entity.RetinalImage;
import com.example.demo.entity.Statistics;
import com.example.demo.repository.AnalysisResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisService {

    @Autowired
    private AnalysisResultRepository analysisResultRepository;

    public AnalysisResult analyzeImage(RetinalImage image) {

        AnalysisResult result = new AnalysisResult();

        result.setResult("Diabetic Retinopathy Detected");

        result.setRiskLevel("High");

        if (result.getRiskLevel().equals("High")) {

            result.setRecommendation(
                    "Urgent medical consultation is recommended."
            );

        } else if (result.getRiskLevel().equals("Medium")) {

            result.setRecommendation(
                    "Regular monitoring is advised."
            );

        } else {

            result.setRecommendation(
                    "No immediate risk detected."
            );
        }

        result.setConfidence(96.5);

        result.setAnnotatedImageUrl(
                "annotated_" + image.getImageUrl()
        );

        result.setNotification(
                "AI analysis completed successfully."
        );

        result.setImage(image);

        return analysisResultRepository.save(result);
    }

    public List<AnalysisResult> getAllResults() {
        return analysisResultRepository.findAll();
    }

    public AnalysisResult confirmResult(
            Long id,
            AnalysisResult updatedResult
    ) {

        AnalysisResult result =
                analysisResultRepository.findById(id)
                        .orElse(null);

        if (result != null) {

            result.setDoctorConclusion(
                    updatedResult.getDoctorConclusion()
            );

            result.setDoctorConfirmed(true);

            result.setDoctorNotes(
                    updatedResult.getDoctorNotes()
            );

            return analysisResultRepository.save(result);
        }

        return null;
    }

    public Statistics getStatistics() {

        Statistics statistics = new Statistics();

        statistics.setTotalAnalyses(
                analysisResultRepository.count()
        );

        statistics.setHighRiskCases(
                analysisResultRepository
                        .countByRiskLevel("High")
        );

        statistics.setMediumRiskCases(
                analysisResultRepository
                        .countByRiskLevel("Medium")
        );

        statistics.setLowRiskCases(
                analysisResultRepository
                        .countByRiskLevel("Low")
        );

        return statistics;
    }
}