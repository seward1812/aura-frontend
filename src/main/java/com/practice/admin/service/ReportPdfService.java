package com.practice.admin.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.practice.admin.entity.AIReport;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Service
public class ReportPdfService {

    public byte[] createReportPdf(AIReport report) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 36, 36);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = font(18, Font.BOLD);
            Font headingFont = font(12, Font.BOLD);
            Font normalFont = font(11, Font.NORMAL);
            Font smallFont = font(9, Font.ITALIC);

            Paragraph title = new Paragraph("BÁO CÁO CHẨN ĐOÁN VÕNG MẠC AI", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(18);
            document.add(title);

            addReportImage(document, report.getImageUrl());
            addInfoTable(document, report, headingFont, normalFont);

            Paragraph resultTitle = new Paragraph("Kết quả phân tích", headingFont);
            resultTitle.setSpacingBefore(16);
            resultTitle.setSpacingAfter(8);
            document.add(resultTitle);

            Paragraph aiResult = new Paragraph(value(report.getAiResult()), normalFont);
            aiResult.setLeading(16);
            document.add(aiResult);

            Paragraph note = new Paragraph(
                    "Lưu ý: Báo cáo này chỉ hỗ trợ sàng lọc và không thay thế chẩn đoán của bác sĩ chuyên khoa mắt.",
                    smallFont
            );
            note.setSpacingBefore(24);
            document.add(note);

            document.close();
            return outputStream.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("Khong the tao file PDF.", exception);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private void addInfoTable(Document document, AIReport report, Font headingFont, Font normalFont) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(12);

        addRow(table, "Bệnh nhân", report.getPatientName(), headingFont, normalFont);
        addRow(table, "Bác sĩ", report.getDoctorName(), headingFont, normalFont);
        addRow(table, "Bệnh / tình trạng", report.getDisease(), headingFont, normalFont);
        addRow(table, "Mức rủi ro", vietnameseRisk(report.getRiskLevel()), headingFont, normalFont);
        addRow(table, "Ngày tạo", report.getCreatedDate(), headingFont, normalFont);

        document.add(table);
    }

    private void addRow(PdfPTable table, String label, String value, Font headingFont, Font normalFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, headingFont));
        labelCell.setBackgroundColor(new Color(238, 243, 255));
        labelCell.setPadding(8);
        labelCell.setBorderColor(new Color(210, 219, 235));

        PdfPCell valueCell = new PdfPCell(new Phrase(value(value), normalFont));
        valueCell.setPadding(8);
        valueCell.setBorderColor(new Color(210, 219, 235));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addReportImage(Document document, String imageUrl) {
        if (!StringUtils.hasText(imageUrl) || !imageUrl.startsWith("/uploads/")) {
            return;
        }

        try {
            String relativeImageUrl = imageUrl.replaceFirst("^/+", "");
            Path imagePath = Path.of(System.getProperty("user.dir"), "src/main/resources/static", relativeImageUrl);
            File imageFile = imagePath.normalize().toFile();

            if (!imageFile.exists()) {
                return;
            }

            Image image = Image.getInstance(imageFile.getAbsolutePath());
            image.scaleToFit(220, 180);
            image.setAlignment(Element.ALIGN_CENTER);
            image.setSpacingAfter(12);
            document.add(image);
        } catch (Exception ignored) {
            // PDF export should still work when the optional image cannot be loaded.
        }
    }

    private Font font(int size, int style) {
        try {
            BaseFont baseFont = BaseFont.createFont(findVietnameseFont(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style);
        } catch (Exception exception) {
            return new Font(Font.HELVETICA, size, style);
        }
    }

    private String findVietnameseFont() {
        List<String> fontPaths = List.of(
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/tahoma.ttf",
                "C:/Windows/Fonts/calibri.ttf"
        );

        return fontPaths.stream()
                .filter(path -> new File(path).exists())
                .findFirst()
                .orElse(BaseFont.HELVETICA);
    }

    private String vietnameseRisk(String riskLevel) {
        if ("High".equalsIgnoreCase(riskLevel)) {
            return "Cao";
        }
        if ("Medium".equalsIgnoreCase(riskLevel)) {
            return "Trung bình";
        }
        if ("Low".equalsIgnoreCase(riskLevel)) {
            return "Thấp";
        }
        return value(riskLevel);
    }

    private String value(String text) {
        return StringUtils.hasText(text) ? text : "Chưa có dữ liệu";
    }
}
