package ee.matteus.plaanisepp.gui;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfReportExporterTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void includesFenceInventoryFromReportText() throws Exception {
        File output = temporaryDirectory.resolve("report.pdf").toFile();
        String reportText = """
                Testplaan
                =========

                Aiad
                  - Aiaring: 12 × 3.50 m = 42 m
                Kokku: 12 aeda, 42 m
                Pikkuse järgi:
                  - 3.50 m: 12 aeda, 42 m
                Gruppide järgi:
                  - Sissepääs: 12 aeda, 42 m
                """;

        PdfReportExporter.export(
                output,
                "Testplaan",
                new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB),
                reportText
        );

        try (PDDocument document = PDDocument.load(output)) {
            String pdfText = new PDFTextStripper().getText(document);
            assertTrue(pdfText.contains("Aiaring: 12 × 3.50 m = 42 m"));
            assertTrue(pdfText.contains("Kokku: 12 aeda, 42 m"));
            assertTrue(pdfText.contains("Sissepääs: 12 aeda, 42 m"));
        }
    }

    @Test
    void includesObjectLegendOnItsOwnPdfPage() throws Exception {
        File output = temporaryDirectory.resolve("legend.pdf").toFile();

        PdfReportExporter.export(
                output,
                "Testplaan",
                new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB),
                "Raport",
                java.util.List.of(new PdfObjectLegendItem(
                        "Pudruala", "Telk", "Pudrutelk", "#99cccc", "6.0 × 4.0 m"
                ))
        );

        try (PDDocument document = PDDocument.load(output)) {
            String pdfText = new PDFTextStripper().getText(document);
            assertTrue(pdfText.contains("Objektide legend"));
            assertTrue(pdfText.contains("Telk: Pudrutelk"));
            assertTrue(pdfText.contains("Pudruala"));
            assertFalse(pdfText.contains("#99cccc"));
            assertTrue(pdfText.contains("6.0 × 4.0 m"));
        }
    }

    @Test
    void repeatedBlankLinesDoNotCreateMostlyEmptyPages() throws Exception {
        File output = temporaryDirectory.resolve("blank-lines.pdf").toFile();

        PdfReportExporter.export(
                output,
                "Testplaan",
                new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB),
                "Raport\n" + "\n".repeat(100) + "Lõpp"
        );

        try (PDDocument document = PDDocument.load(output)) {
            assertEquals(2, document.getNumberOfPages());
        }
    }
}
