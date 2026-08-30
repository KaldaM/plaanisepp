package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CableInventorySummaryServiceTest {
    @Test
    void summarizesMapLengthsNotedPiecesAndAlternatives() {
        PowerConnection primary = connection("primary", true, "10 + 10 + 5", "lava");
        PowerConnection alternative = connection("alternative", false, "7,5 + varu", "");

        CableInventorySummaryService.Summary summary = new CableInventorySummaryService().summarize(List.of(
                new CableInventorySummaryService.Input("Lava", "Kapp", primary, 23),
                new CableInventorySummaryService.Input("Telk", "Kapp", alternative, 8, true)
        ));

        assertEquals(31, summary.totalMapLengthMeters());
        assertEquals(32.5, summary.totalNotedLengthMeters());
        assertTrue(summary.hasNotedLength());
        assertEquals(2, summary.rows().size());
        assertFalse(summary.rows().getFirst().alternativeConnection());
        assertEquals(25, summary.rows().getFirst().notedLengthMeters().orElseThrow());
        assertTrue(summary.rows().get(1).alternativeConnection());
        assertEquals("alternative", summary.rows().get(1).connectionId());
        assertEquals("consumer", summary.rows().get(1).consumerId());
        assertTrue(summary.rows().get(1).consumerHidden());
        assertTrue(CableInventoryTextFormatter.connectionRow(summary.rows().get(1)).contains("Telk (peidetud)"));
        assertTrue(summary.rows().get(1).noteNeedsReview());
        assertEquals(2, summary.byType().get(ConnectorType.SCHUKO_230V).pieceCounts().get(10.0));
        assertEquals(1, summary.byType().get(ConnectorType.SCHUKO_230V).pieceCounts().get(5.0));
    }

    @Test
    void emptyInputProducesEmptySummary() {
        assertTrue(new CableInventorySummaryService().summarize(List.of()).isEmpty());
    }

    private PowerConnection connection(String id, boolean primary, String lengthNotes, String cableNotes) {
        return new PowerConnection(
                id,
                "source",
                "consumer",
                ConnectorType.SCHUKO_230V,
                "outlet",
                cableNotes,
                lengthNotes,
                List.of(),
                false,
                null,
                primary
        );
    }
}
