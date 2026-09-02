package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.AreaObject;
import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.DistributionPanel;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.FenceRow;
import ee.matteus.plaanisepp.core.model.LineObject;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import ee.matteus.plaanisepp.core.model.TextObject;
import ee.matteus.plaanisepp.core.model.TextObjectSourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReportTextExporterTest {
    @Test
    void includesPlanOverview() {
        EventPlan plan = new EventPlan("Sündmus");
        plan.setPixelsPerMeter(6.45);
        plan.setMapImagePath("kaart.png");
        plan.addObject(new Tent("tent", "Telk", new Position(10, 20)));

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.COMPACT, false, false, false
        );

        assertTrue(report.startsWith("Sündmus" + System.lineSeparator() + "======="));
        assertTrue(report.contains("Mõõtkava: 6.45 px/m"));
        assertTrue(report.contains("Kaart: kaart.png"));
        assertTrue(report.contains("Objekte: 1"));
    }

    @Test
    void includesGroupedObjectsWhenRequested() {
        EventPlan plan = new EventPlan("Test");
        Tent tent = new Tent("tent", "Kohvik", new Position(10, 20));
        tent.setGroupName("Toitlustus");
        plan.addObject(tent);

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.COMPACT, false, false, true
        );

        assertTrue(report.contains("Grupid"));
        assertTrue(report.contains("Toitlustus"));
        assertTrue(report.contains("- Kohvik (Telk)"));
    }

    @Test
    void includesTextObjectNotesWithoutGroupReport() {
        EventPlan plan = new EventPlan("Test");
        TextObject text = new TextObject("text", "Meelespea", new Position(10, 20));
        text.setGroupName("Korraldus");
        text.setNotes("Võta võtmed\nHelista valvurile");
        plan.addObject(text);

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.COMPACT, false, false, false
        );

        assertTrue(report.contains("Tekstimärkmed"));
        assertTrue(report.contains("Meelespea (Korraldus)"));
        assertTrue(report.contains("  Võta võtmed"));
        assertTrue(report.contains("  Helista valvurile"));
    }

    @Test
    void organizerReportExcludesPowerSourcesAndTheirGeneratedText() {
        EventPlan plan = new EventPlan("Test");
        PowerSource source = new PowerSource("source", "Põhikilp", new Position(0, 0));
        TextObject powerText = new TextObject("power-text", "Põhikilbi väljundid", new Position(20, 20));
        powerText.setSourceObjectId(source.id());
        powerText.setSourceType(TextObjectSourceType.POWER_OUTLETS);
        powerText.setNotes("63A väljund");
        Tent tent = new Tent("tent", "Korraldajatelk", new Position(40, 40));
        plan.addObject(source);
        plan.addObject(powerText);
        plan.addObject(tent);

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.FULL, false, false, true, false
        );

        assertTrue(report.contains("Korraldajatelk"));
        assertFalse(report.contains("Põhikilp"));
        assertFalse(report.contains("Põhikilbi väljundid"));
        assertFalse(report.contains("63A väljund"));
    }

    @Test
    void includesFenceInventory() {
        EventPlan plan = new EventPlan("Test");
        FenceRow first = new FenceRow("fence-1", "Peasissepääs", new Position(0, 0));
        first.setSegmentCount(4);
        FenceRow second = new FenceRow("fence-2", "Lava külg", new Position(100, 0));
        second.setSegmentCount(3);
        plan.addObject(first);
        plan.addObject(second);

        String report = new ReportTextExporter().export(
                plan,
                ReportExportScope.COMPACT,
                false,
                false,
                false
        );

        assertTrue(report.contains("Peasissepääs: 4 × 3.50 m = 14 m"));
        assertTrue(report.contains("Lava külg: 3 × 3.50 m = 10.50 m"));
        assertTrue(report.contains("Kokku: 7 aeda"));
        assertFalse(report.contains("Pikkuse järgi:"));
        assertTrue(report.contains("Määramata: 7 aeda, 24.50 m"));
    }

    @Test
    void listsConnectedFenceNetworkOnlyOnce() {
        EventPlan plan = new EventPlan("Test");
        FenceRow first = new FenceRow("fence-1", "Aiaring", new Position(0, 0));
        first.setSegmentCount(2);
        FenceRow second = new FenceRow("fence-2", "Aiaring", first.endPosition(plan.pixelsPerMeter()));
        second.setSegmentCount(3);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        plan.setFenceNetworkGardenStoneAdjustment(first.id(), -1);
        plan.setStandaloneGardenStoneCount(2);

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.COMPACT, false, false, false
        );

        assertEquals(1, occurrences(report, "  - Aiaring:"));
        assertTrue(report.contains("Aiaring: 5 × 3.50 m = 17.50 m"), report);
        assertTrue(report.contains("Kokku: 5 aeda, 17.50 m"), report);
        assertTrue(report.contains("Aiakivid: 6 automaatne, -1 parandus, 5 kokku"), report);
        assertTrue(report.contains("Aiakive kokku: 7 tk (lisainventar 2 tk)"), report);
    }

    @Test
    void listsConnectedFenceNetworkOnlyOnceInGroups() {
        EventPlan plan = new EventPlan("Test");
        FenceRow first = new FenceRow("fence-1", "Aiaring", new Position(0, 0));
        FenceRow second = new FenceRow("fence-2", "Aiaring", first.endPosition(plan.pixelsPerMeter()));
        first.setGroupName("Sissepääs");
        second.setGroupName("Sissepääs");
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());

        String report = new ReportTextExporter().export(
                plan, ReportExportScope.COMPACT, false, false, true
        );

        assertEquals(1, occurrences(report, "- Aiaring (Aiarida)"), report);
    }

    @Test
    void includesEquipmentPowerExceptionInPowerAndCableReports() {
        EventPlan plan = new EventPlan("Test");
        PowerSource defaultSource = new PowerSource("source-1", "Põhikapp", new Position(0, 0));
        defaultSource.addOutlet(new PowerOutlet("outlet-1", ConnectorType.SCHUKO_230V, 11000));
        PowerSource alternativeSource = new PowerSource("source-2", "Teine kapp", new Position(100, 0));
        alternativeSource.addOutlet(new PowerOutlet("outlet-2", ConnectorType.SCHUKO_230V, 11000));
        Tent tent = new Tent("tent", "Kohvikutelk", new Position(50, 100));
        Equipment coffeeMachine = new Equipment("equipment-1", "Kohvimasin", 1800);
        Equipment refrigerator = new Equipment("equipment-2", "Külmik", 500);
        tent.addEquipment(coffeeMachine);
        tent.addEquipment(refrigerator);
        plan.addObject(defaultSource);
        plan.addObject(alternativeSource);
        plan.addObject(tent);
        plan.connectToPower(defaultSource.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet-1");
        PowerConnection alternativeConnection = plan.addAlternativePowerConnection(
                alternativeSource.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet-2"
        ).orElseThrow();
        plan.assignEquipmentToPowerConnection(tent.id(), refrigerator.id(), alternativeConnection.id());

        String report = new ReportTextExporter().export(
                plan,
                ReportExportScope.FULL,
                true,
                true,
                false
        );

        assertTrue(report.contains("Kohvikutelk: 1800 W"));
        assertTrue(report.contains("* Kohvimasin: 1800 W"));
        assertTrue(report.contains("Kohvikutelk: 500 W (seadme erand)"));
        assertTrue(report.contains("* Külmik: 500 W"));
        assertTrue(report.contains("Kohvikutelk -> Põhikapp (230V tavapesa)"));
        assertTrue(report.contains("Kohvikutelk -> Teine kapp (230V tavapesa, seadme erand)"));
    }

    @Test
    void includesConnectedAreaAndLineInPowerAndCableReports() {
        EventPlan plan = new EventPlan("Test");
        PowerSource source = new PowerSource("source", "Kapp", new Position(0, 0));
        source.addOutlet(new PowerOutlet("outlet", ConnectorType.SCHUKO_230V, 11000));
        AreaObject area = new AreaObject("area", "Lava", new Position(20, 20));
        area.addEquipment(new Equipment("Valgusti", 500));
        LineObject line = new LineObject("line", "Valguskett", new Position(40, 40));
        line.addEquipment(new Equipment("Lambid", 300));
        plan.addObject(source);
        plan.addObject(area);
        plan.addObject(line);
        plan.connectToPower(source.id(), area.id(), ConnectorType.SCHUKO_230V, "outlet");
        plan.connectToPower(source.id(), line.id(), ConnectorType.SCHUKO_230V, "outlet");

        String report = new ReportTextExporter().export(
                plan,
                ReportExportScope.FULL,
                true,
                true,
                false
        );

        assertTrue(report.contains("Lava: 500 W"));
        assertTrue(report.contains("* Valgusti: 500 W"));
        assertTrue(report.contains("Valguskett: 300 W"));
        assertTrue(report.contains("* Lambid: 300 W"));
        assertTrue(report.contains("Lava -> Kapp"));
        assertTrue(report.contains("Valguskett -> Kapp"));
    }

    @Test
    void includesDistributionPanelDemandInUpstreamOutlet() {
        EventPlan plan = new EventPlan("Test");
        PowerSource mainSource = new PowerSource("source", "Põhikilp", new Position(0, 0));
        mainSource.addOutlet(new PowerOutlet("source-outlet", ConnectorType.INDUSTRIAL_16A, 11000));
        DistributionPanel panel = new DistributionPanel("panel", "Alajaotuskilp", new Position(20, 20));
        panel.addOutlet(new PowerOutlet("panel-outlet", ConnectorType.SCHUKO_230V, 3500));
        AreaObject area = new AreaObject("area", "Telk", new Position(40, 40));
        area.addEquipment(new Equipment("Pliit", 1811));
        plan.addObject(mainSource);
        plan.addObject(panel);
        plan.addObject(area);
        plan.connectToPower(mainSource.id(), panel.id(), ConnectorType.INDUSTRIAL_16A, "source-outlet");
        plan.connectToPower(panel.id(), area.id(), ConnectorType.SCHUKO_230V, "panel-outlet");

        String report = new ReportTextExporter().export(
                plan,
                ReportExportScope.FULL,
                true,
                false,
                false
        );

        assertTrue(report.contains("16A tööstusvool 1: 11000 W mahutavus, 1811 W kasutusel"));
        assertTrue(report.contains("- Alajaotuskilp: 1811 W"));
    }

    private int occurrences(String text, String fragment) {
        return (text.length() - text.replace(fragment, "").length()) / fragment.length();
    }
}
