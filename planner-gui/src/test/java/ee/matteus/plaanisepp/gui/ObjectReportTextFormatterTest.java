package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.CustomObject;
import ee.matteus.plaanisepp.core.model.CustomObjectShape;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectReportTextFormatterTest {

    @Test
    void legendNamesCustomObjectsByTheirActualShape() {
        EventPlan plan = new EventPlan("Kujundid");
        CustomObject rectangle = new CustomObject("rectangle", "Infolaud", new Position(0, 0));
        CustomObject circle = new CustomObject("circle", "Purskkaev", new Position(10, 10));
        circle.setShape(CustomObjectShape.CIRCLE);
        plan.addObject(rectangle);
        plan.addObject(circle);
        StringBuilder text = new StringBuilder();

        new ObjectReportTextFormatter().appendGroups(text, plan, "\n", true);

        assertTrue(text.toString().contains("Infolaud (Ristkülik)"));
        assertTrue(text.toString().contains("Purskkaev (Ring)"));
    }
}
