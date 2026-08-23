package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;
import ee.matteus.plaanisepp.core.model.Tent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CableRouteEditorTest {
    @Test
    void editsAlternativeConnectionRouteWithoutChangingDefaultRoute() {
        EventPlan plan = new EventPlan("Test");
        PowerSource defaultSource = source("source-1", "outlet-1", new Position(0, 0));
        PowerSource alternativeSource = source("source-2", "outlet-2", new Position(100, 0));
        Tent tent = new Tent("tent", "Telk", new Position(50, 100));
        plan.addObject(defaultSource);
        plan.addObject(alternativeSource);
        plan.addObject(tent);
        PowerConnection defaultConnection = plan.connectToPower(
                defaultSource.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet-1"
        ).orElseThrow();
        PowerConnection alternativeConnection = plan.addAlternativePowerConnection(
                alternativeSource.id(), tent.id(), ConnectorType.SCHUKO_230V, "outlet-2"
        ).orElseThrow();

        assertTrue(CableRouteEditor.insertPointForConnection(
                plan,
                alternativeConnection.id(),
                List.of(alternativeSource.position(), tent.position()),
                new Position(75, 50)
        ));
        assertEquals(List.of(), connection(plan, defaultConnection.id()).routePoints());
        assertEquals(
                List.of(new Position(75, 50)),
                connection(plan, alternativeConnection.id()).routePoints()
        );

        assertTrue(CableRouteEditor.replacePoint(
                plan, alternativeConnection.id(), 0, new Position(80, 55)
        ).isPresent());
        assertEquals(
                List.of(new Position(80, 55)),
                connection(plan, alternativeConnection.id()).routePoints()
        );

        assertTrue(CableRouteEditor.removePoint(plan, alternativeConnection.id(), 0));
        assertEquals(List.of(), connection(plan, alternativeConnection.id()).routePoints());
    }

    @Test
    void marksAlternativeCableLabelAsException() {
        PowerConnection alternativeConnection = new PowerConnection(
                "connection",
                "source",
                "consumer",
                ConnectorType.SCHUKO_230V,
                "outlet",
                "",
                "",
                List.of(),
                false,
                new Position(0, 0),
                false
        );

        assertEquals("Erand · 230V · 12.5 m", CableDisplayHelper.mapLabel(alternativeConnection, 12.5));
    }

    private PowerSource source(String id, String outletId, Position position) {
        PowerSource source = new PowerSource(id, id, position);
        source.addOutlet(new PowerOutlet(outletId, ConnectorType.SCHUKO_230V, 11000));
        return source;
    }

    private PowerConnection connection(EventPlan plan, String connectionId) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst()
                .orElseThrow();
    }
}
