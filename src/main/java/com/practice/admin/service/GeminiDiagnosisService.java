package com.practice.admin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiDiagnosisService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiDiagnosisService(
            ObjectMapper objectMapper,
            @Value("${gemini.api.key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model
    ) {
        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public DiagnosisResult analyzeRetinaImage(MultipartFile imageFile, String patientNote) throws IOException {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Chua cau hinh GEMINI_API_KEY.");
        }

        String mimeType = StringUtils.hasText(imageFile.getContentType())
                ? imageFile.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String imageBase64 = Base64.getEncoder().encodeToString(imageFile.getBytes());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", buildPrompt(patientNote)),
                                        Map.of("inline_data", Map.of(
                                                "mime_type", mimeType,
                                                "data", imageBase64
                                        ))
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        "responseMimeType", "application/json"
                )
        );

        String url = UriComponentsBuilder
                .fromHttpUrl("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent")
                .queryParam("key", apiKey)
                .toUriString();

        JsonNode response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        return parseResponse(response);
    }

    private String buildPrompt(String patientNote) {
        String note = StringUtils.hasText(patientNote) ? patientNote : "Không có ghi chú bổ sung.";

        return """
                Bạn là trợ lý AI hỗ trợ sàng lọc ảnh võng mạc. Hãy quan sát ảnh và trả về JSON hợp lệ.
                Chỉ trả về JSON, không thêm markdown.

                Yêu cầu:
                - disease: tên tình trạng chính bằng tiếng Việt. Không ghi "Không rõ" nếu trong ảnh hoặc mô tả có dấu hiệu cụ thể.
                - riskLevel: chỉ được là Low, Medium, hoặc High để hệ thống tô màu đúng.
                - recommendation: khuyến nghị ngắn gọn bằng tiếng Việt cho bệnh nhân/bác sĩ.
                - aiResult: tóm tắt kết quả phân tích bằng tiếng Việt trong 1-3 câu, nêu rõ dấu hiệu quan sát được.

                Gợi ý đặt tên disease:
                - Nếu thấy xuất huyết, đốm xuất huyết, hemorrhage, bleeding: dùng "Xuất huyết võng mạc".
                - Nếu thấy vi phình mạch, microaneurysm, xuất tiết hoặc dấu hiệu liên quan tiểu đường: dùng "Bệnh võng mạc tiểu đường".
                - Nếu thấy dấu hiệu tăng nhãn áp/glaucoma: dùng "Tăng nhãn áp".
                - Nếu thấy tổn thương hoàng điểm/macular degeneration: dùng "Thoái hóa hoàng điểm".
                - Nếu ảnh bình thường, không có bất thường rõ: dùng "Võng mạc bình thường".
                - Chỉ dùng "Không rõ" khi ảnh mờ, không đủ chất lượng hoặc không có đủ dữ kiện để nhận diện dấu hiệu.

                Lưu ý an toàn: đây là kết quả hỗ trợ sàng lọc, không thay thế chẩn đoán của bác sĩ chuyên khoa mắt.

                Ghi chú người dùng: %s
                """.formatted(note);
    }

    private DiagnosisResult parseResponse(JsonNode response) throws IOException {
        if (response == null) {
            throw new IllegalStateException("Gemini khong tra ve du lieu.");
        }

        JsonNode textNode = response.at("/candidates/0/content/parts/0/text");
        if (textNode.isMissingNode() || !StringUtils.hasText(textNode.asText())) {
            throw new IllegalStateException("Khong doc duoc ket qua tu Gemini.");
        }

        JsonNode resultJson = objectMapper.readTree(stripMarkdownFence(textNode.asText()));

        String recommendation = textValue(resultJson, "recommendation",
                "Cần bác sĩ chuyên khoa mắt đánh giá thêm để có kết luận chính xác.");
        String aiResult = textValue(resultJson, "aiResult",
                "Gemini đã phân tích ảnh nhưng không trả về tóm tắt chi tiết.");
        String disease = resolveDisease(
                textValue(resultJson, "disease", "Không rõ"),
                aiResult,
                recommendation
        );
        String riskLevel = normalizeRisk(textValue(resultJson, "riskLevel", "Medium"));

        return new DiagnosisResult(disease, riskLevel, recommendation, aiResult, model);
    }

    private String stripMarkdownFence(String text) {
        return text
                .replaceFirst("^```json\\s*", "")
                .replaceFirst("^```\\s*", "")
                .replaceFirst("\\s*```$", "")
                .trim();
    }

    private String textValue(JsonNode node, String fieldName, String fallback) {
        JsonNode value = node.get(fieldName);
        return value != null && StringUtils.hasText(value.asText()) ? value.asText() : fallback;
    }

    private String normalizeRisk(String riskLevel) {
        String normalized = riskLevel.trim().toLowerCase();
        if (normalized.contains("high") || normalized.contains("cao")) {
            return "High";
        }
        if (normalized.contains("low") || normalized.contains("thấp") || normalized.contains("thap")) {
            return "Low";
        }
        return "Medium";
    }

    private String resolveDisease(String disease, String aiResult, String recommendation) {
        String translatedDisease = translateDisease(disease);
        String combinedText = String.join(" ", disease, aiResult, recommendation).toLowerCase();

        if (containsAny(combinedText, "xuất huyết", "xuat huyet", "hemorrhage", "haemorrhage", "bleeding")) {
            return "Xuất huyết võng mạc";
        }
        if (containsAny(combinedText, "vi phình mạch", "vi phinh mach", "microaneurysm", "tiểu đường", "tieu duong", "diabetic retinopathy")) {
            return "Bệnh võng mạc tiểu đường";
        }
        if (containsAny(combinedText, "phù hoàng điểm", "phu hoang diem", "macular edema", "macular oedema")) {
            return "Phù hoàng điểm";
        }
        if (containsAny(combinedText, "thoái hóa hoàng điểm", "thoai hoa hoang diem", "macular degeneration")) {
            return "Thoái hóa hoàng điểm";
        }
        if (containsAny(combinedText, "tăng nhãn áp", "tang nhan ap", "glaucoma")) {
            return "Tăng nhãn áp";
        }
        if (containsAny(combinedText, "đục thủy tinh thể", "duc thuy tinh the", "cataract")) {
            return "Đục thủy tinh thể";
        }

        return translatedDisease;
    }

    private String translateDisease(String disease) {
        String normalized = disease.trim().toLowerCase();

        if (containsAny(normalized, "normal", "bình thường", "binh thuong")) {
            return "Võng mạc bình thường";
        }
        if (containsAny(normalized, "diabetic", "retinopathy", "tiểu đường", "tieu duong")) {
            return "Bệnh võng mạc tiểu đường";
        }
        if (containsAny(normalized, "hemorrhage", "haemorrhage", "xuất huyết", "xuat huyet")) {
            return "Xuất huyết võng mạc";
        }
        if (containsAny(normalized, "glaucoma", "tăng nhãn áp", "tang nhan ap")) {
            return "Tăng nhãn áp";
        }
        if (containsAny(normalized, "macular edema", "macular oedema", "phù hoàng điểm", "phu hoang diem")) {
            return "Phù hoàng điểm";
        }
        if (containsAny(normalized, "macular", "degeneration", "thoái hóa hoàng điểm", "thoai hoa hoang diem")) {
            return "Thoái hóa hoàng điểm";
        }
        if (containsAny(normalized, "cataract", "đục thủy tinh thể", "duc thuy tinh the")) {
            return "Đục thủy tinh thể";
        }
        if (containsAny(normalized, "unclear", "không rõ", "khong ro")) {
            return "Không rõ";
        }

        return disease;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public record DiagnosisResult(
            String disease,
            String riskLevel,
            String recommendation,
            String aiResult,
            String aiVersion
    ) {
    }
}
