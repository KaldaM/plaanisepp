package ee.matteus.plaanisepp.gui;

/** A single, map-visible object row in the PDF legend. */
record PdfObjectLegendItem(
        String groupName,
        String type,
        String name,
        String colorHex,
        String details
) {
}
