package ee.matteus.plaanisepp.gui;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PdfReportExporter {
    private static final Pattern POWER_CAPACITY_PATTERN = Pattern.compile(
            ".*?: (\\d+) W mahutavus, (\\d+) W kasutusel, (?:([0-9]+) W alles|ÜLEKOORMUS ([0-9]+) W).*"
    );
    private static final Pattern POWER_SOURCE_PATTERN = Pattern.compile(
            ".*?: (\\d+) W kasutusel, (?:([0-9]+) W alles|ÜLEKOORMUS ([0-9]+) W).*"
    );
    private PdfReportExporter() {
    }

    static void export(File file, String planName, BufferedImage mapImage, String reportText) throws IOException {
        export(file, planName, mapImage, reportText, "");
    }

    static void export(
            File file,
            String planName,
            BufferedImage mapImage,
            String reportText,
            String objectLegendText
    ) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addMapPage(document, planName, mapImage);
            if (!objectLegendText.isBlank()) {
                addReportPages(document, objectLegendText);
            }
            addReportPages(document, reportText);
            addPageNumbers(document);
            document.save(file);
        }
    }

    static void export(
            File file,
            String planName,
            BufferedImage mapImage,
            String reportText,
            List<PdfObjectLegendItem> objectLegendItems
    ) throws IOException {
        try (PDDocument document = new PDDocument()) {
            addMapPage(document, planName, mapImage);
            if (!objectLegendItems.isEmpty()) {
                addObjectLegendPages(document, objectLegendItems);
            }
            addReportPages(document, reportText);
            addPageNumbers(document);
            document.save(file);
        }
    }

    private static void addMapPage(PDDocument document, String planName, BufferedImage mapImage) throws IOException {
        PDRectangle pageSize = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        PDPage page = new PDPage(pageSize);
        document.addPage(page);

        float margin = 36;
        float titleSize = 16;
        float availableWidth = pageSize.getWidth() - margin * 2;
        float availableHeight = pageSize.getHeight() - margin * 2 - 28;
        float scale = Math.min(availableWidth / mapImage.getWidth(), availableHeight / mapImage.getHeight());
        float imageWidth = mapImage.getWidth() * scale;
        float imageHeight = mapImage.getHeight() * scale;
        float imageX = margin + (availableWidth - imageWidth) / 2;
        float imageY = margin;

        PDImageXObject pdfImage = LosslessFactory.createFromImage(document, mapImage);
        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, titleSize);
            content.newLineAtOffset(margin, pageSize.getHeight() - margin - titleSize);
            content.showText(planName);
            content.endText();
            content.drawImage(pdfImage, imageX, imageY, imageWidth, imageHeight);
        }
    }

    private static void addReportPages(PDDocument document, String reportText) throws IOException {
        float margin = 50;
        float fontSize = 10;
        float leading = 14;
        PDType1Font regularFont = PDType1Font.HELVETICA;
        PDType1Font boldFont = PDType1Font.HELVETICA_BOLD;
        PDRectangle pageSize = PDRectangle.A4;
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        float y = pageSize.getHeight() - margin;
        boolean previousLineBlank = false;
        try {
            for (String originalLine : reportText.split("\\R", -1)) {
                String trimmedLine = originalLine.trim();
                boolean lineBlank = trimmedLine.isBlank();
                if (lineBlank && previousLineBlank) {
                    continue;
                }
                previousLineBlank = lineBlank;
                if (!trimmedLine.isBlank() && trimmedLine.chars().allMatch(character -> character == '=')) {
                    continue;
                }
                PdfReportLineStyle style = pdfReportLineStyle(originalLine, regularFont, boldFont, fontSize);
                PowerLoad powerLoad = powerLoad(originalLine);
                boolean powerSourceRow = powerLoad != null && !originalLine.startsWith(" ");
                y -= style.extraSpaceBefore();
                float maxLineWidth = pageSize.getWidth() - margin * 2 - style.indent();
                boolean firstWrappedLine = true;
                for (String line : wrapLine(originalLine.trim(), style.font(), style.fontSize(), maxLineWidth)) {
                    if (y <= margin + 20) {
                        content.close();
                        page = new PDPage(pageSize);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        y = pageSize.getHeight() - margin;
                    }
                    if (powerSourceRow && firstWrappedLine) {
                        drawPowerSourceBackground(content, margin - 7, y - 4, pageSize.getWidth() - margin * 2 + 14);
                    }
                    content.beginText();
                    content.setFont(style.font(), style.fontSize());
                    content.newLineAtOffset(margin + style.indent(), y);
                    content.showText(line);
                    content.endText();
                    y -= style.lineHeight(leading);
                    firstWrappedLine = false;
                }
                if (powerLoad != null) {
                    drawPowerLoadBar(content, margin + style.indent(), y + 4, 150, 5, powerLoad);
                    y -= powerSourceRow ? 8 : 6;
                }
            }
        } finally {
            content.close();
        }
    }

    private static void addObjectLegendPages(PDDocument document, List<PdfObjectLegendItem> items) throws IOException {
        float margin = 50;
        float fontSize = 10;
        float leading = 14;
        PDRectangle pageSize = PDRectangle.A4;
        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        float y = pageSize.getHeight() - margin;
        String previousGroup = null;
        try {
            for (PdfObjectLegendItem item : items) {
                if (!item.groupName().equals(previousGroup)) {
                    if (previousGroup != null) {
                        y -= 8;
                    }
                    if (y <= margin + 48) {
                        content.close();
                        page = new PDPage(pageSize);
                        document.addPage(page);
                        content = new PDPageContentStream(document, page);
                        y = pageSize.getHeight() - margin;
                    }
                    String heading = previousGroup == null ? "Objektide legend" : "";
                    if (!heading.isBlank()) {
                        drawText(content, heading, PDType1Font.HELVETICA_BOLD, 14, margin, y);
                        y -= 26;
                    }
                    drawText(content, item.groupName(), PDType1Font.HELVETICA_BOLD, 11, margin, y);
                    y -= 17;
                    previousGroup = item.groupName();
                }
                String details = item.details().isBlank() ? "" : " · " + item.details();
                String row = "%s: %s%s".formatted(item.type(), item.name(), details);
                List<String> lines = wrapLine(row, PDType1Font.HELVETICA, fontSize, pageSize.getWidth() - margin * 2 - 20);
                float requiredHeight = lines.size() * leading;
                if (y - requiredHeight <= margin + 20) {
                    content.close();
                    page = new PDPage(pageSize);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);
                    y = pageSize.getHeight() - margin;
                    drawText(content, item.groupName() + " (jätkub)", PDType1Font.HELVETICA_BOLD, 11, margin, y);
                    y -= 17;
                }
                drawColorSwatch(content, item.colorHex(), margin, y - 2);
                for (String line : lines) {
                    drawText(content, line, PDType1Font.HELVETICA, fontSize, margin + 20, y);
                    y -= leading;
                }
            }
        } finally {
            content.close();
        }
    }

    private static void drawText(PDPageContentStream content, String text, PDType1Font font, float size, float x, float y)
            throws IOException {
        content.beginText();
        content.setFont(font, size);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private static void drawColorSwatch(PDPageContentStream content, String colorHex, float x, float y) throws IOException {
        java.awt.Color color;
        try {
            color = java.awt.Color.decode(colorHex);
        } catch (NumberFormatException exception) {
            color = java.awt.Color.GRAY;
        }
        content.setNonStrokingColor(color);
        content.addRect(x, y, 10, 10);
        content.fill();
        content.setStrokingColor(java.awt.Color.DARK_GRAY);
        content.addRect(x, y, 10, 10);
        content.stroke();
        content.setNonStrokingColor(java.awt.Color.BLACK);
        content.setStrokingColor(java.awt.Color.BLACK);
    }

    private static void drawPowerSourceBackground(
            PDPageContentStream content,
            float x,
            float y,
            float width
    ) throws IOException {
        content.setNonStrokingColor(243, 244, 246);
        content.addRect(x, y, width, 16);
        content.fill();
        content.setNonStrokingColor(java.awt.Color.BLACK);
    }

    private static void drawPowerLoadBar(
            PDPageContentStream content,
            float x,
            float y,
            float width,
            float height,
            PowerLoad load
    ) throws IOException {
        double ratio = load.capacityWatts() <= 0
                ? 0
                : (double) load.usedWatts() / load.capacityWatts();
        content.setNonStrokingColor(229, 231, 235);
        content.addRect(x, y, width, height);
        content.fill();
        if (ratio > 0) {
            if (ratio > 1.0) {
                content.setNonStrokingColor(220, 38, 38);
            } else if (ratio >= 0.9) {
                content.setNonStrokingColor(245, 158, 11);
            } else {
                content.setNonStrokingColor(22, 163, 74);
            }
            content.addRect(x, y, (float) (width * Math.min(1.0, ratio)), height);
            content.fill();
        }
        content.setNonStrokingColor(java.awt.Color.BLACK);
    }

    private static PowerLoad powerLoad(String originalLine) {
        String trimmed = originalLine.trim();
        Matcher capacityMatcher = POWER_CAPACITY_PATTERN.matcher(trimmed);
        if (capacityMatcher.matches()) {
            return new PowerLoad(
                    Integer.parseInt(capacityMatcher.group(1)),
                    Integer.parseInt(capacityMatcher.group(2))
            );
        }
        if (originalLine.startsWith(" ")) {
            return null;
        }
        Matcher sourceMatcher = POWER_SOURCE_PATTERN.matcher(trimmed);
        if (!sourceMatcher.matches()) {
            return null;
        }
        int usedWatts = Integer.parseInt(sourceMatcher.group(1));
        int remainingWatts = sourceMatcher.group(2) == null
                ? -Integer.parseInt(sourceMatcher.group(3))
                : Integer.parseInt(sourceMatcher.group(2));
        return new PowerLoad(usedWatts + remainingWatts, usedWatts);
    }

    private static PdfReportLineStyle pdfReportLineStyle(
            String line,
            PDType1Font regularFont,
            PDType1Font boldFont,
            float defaultFontSize
    ) {
        String trimmedLine = line.trim();
        if (trimmedLine.isBlank()) {
            return new PdfReportLineStyle(regularFont, defaultFontSize, 0, 4);
        }
        if (isHeadingLine(line)) {
            return new PdfReportLineStyle(boldFont, defaultFontSize + 1, 0, 10);
        }
        int leadingSpaces = line.length() - line.stripLeading().length();
        return new PdfReportLineStyle(regularFont, defaultFontSize, leadingSpaces * 5.0f, 0);
    }

    private static boolean isHeadingLine(String line) {
        String trimmedLine = line.trim();
        return !trimmedLine.isBlank() && !line.startsWith(" ");
    }

    private static void addPageNumbers(PDDocument document) throws IOException {
        int pageCount = document.getNumberOfPages();
        for (int index = 0; index < pageCount; index++) {
            PDPage page = document.getPage(index);
            PDRectangle pageSize = page.getMediaBox();
            String pageNumber = "lk %d / %d".formatted(index + 1, pageCount);
            float fontSize = 9;
            float textWidth = textWidth(pageNumber, PDType1Font.HELVETICA, fontSize);
            try (PDPageContentStream content = new PDPageContentStream(
                    document,
                    page,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true
            )) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, fontSize);
                content.newLineAtOffset((pageSize.getWidth() - textWidth) / 2, 24);
                content.showText(pageNumber);
                content.endText();
            }
        }
    }

    private static List<String> wrapLine(String line, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        if (line.isBlank()) {
            return List.of("");
        }

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (String word : line.split(" ")) {
            String candidate = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (textWidth(candidate, font, fontSize) <= maxWidth || currentLine.isEmpty()) {
                currentLine.setLength(0);
                currentLine.append(candidate);
            } else {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            }
        }
        lines.add(currentLine.toString());
        return lines;
    }

    private static float textWidth(String text, PDType1Font font, float fontSize) throws IOException {
        return font.getStringWidth(text) / 1000 * fontSize;
    }

    private record PdfReportLineStyle(PDType1Font font, float fontSize, float indent, float extraSpaceBefore) {
        private float lineHeight(float defaultLineHeight) {
            if (fontSize > 10) {
                return defaultLineHeight + 2;
            }
            return defaultLineHeight;
        }
    }

    private record PowerLoad(int capacityWatts, int usedWatts) {
    }
}
