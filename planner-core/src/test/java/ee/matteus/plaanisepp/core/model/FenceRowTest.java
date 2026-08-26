package ee.matteus.plaanisepp.core.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FenceRowTest {
    @Test
    void calculatesPhysicalLengthAndEndPosition() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(10, 20));
        row.setSegmentCount(4);
        row.setRotationDegrees(90);

        Position end = row.endPosition(10);

        assertEquals(14, row.totalLengthMeters());
        assertEquals(10, end.x(), 0.0001);
        assertEquals(160, end.y(), 0.0001);
    }

    @Test
    void refusesInvalidPhysicalDimensions() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(0, 0));

        assertThrows(IllegalArgumentException.class, () -> row.setSegmentCount(0));
        assertThrows(IllegalArgumentException.class, () -> row.setSegmentLengthMeters(0));
    }

    @Test
    void rotatesEndWithoutChangingFenceLength() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(10, 20));
        row.setSegmentCount(2);

        row.rotateEndToward(new Position(10, 200));

        assertEquals(90, row.rotationDegrees(), 0.0001);
        assertEquals(7, row.totalLengthMeters(), 0.0001);
        assertEquals(10, row.endPosition(10).x(), 0.0001);
        assertEquals(90, row.endPosition(10).y(), 0.0001);
    }

    @Test
    void movesStartWhileKeepingEndAndLengthFixed() {
        FenceRow row = new FenceRow("fence", "Aiarida", new Position(0, 0));
        row.setSegmentCount(2);
        Position originalEnd = row.endPosition(10);

        row.moveStartTowardKeepingEnd(new Position(originalEnd.x(), -100), 10);

        assertEquals(originalEnd.x(), row.endPosition(10).x(), 0.0001);
        assertEquals(originalEnd.y(), row.endPosition(10).y(), 0.0001);
        assertEquals(7, row.totalLengthMeters(), 0.0001);
    }

    @Test
    void connectedContinuationFollowsParentEnd() {
        EventPlan plan = new EventPlan("Aiad");
        plan.setPixelsPerMeter(10);
        FenceRow parent = new FenceRow("parent", "Esimene", new Position(10, 20));
        parent.setSegmentCount(2);
        FenceRow child = new FenceRow("child", "Jätk", new Position(0, 0));
        child.setLocked(true);
        plan.addObject(parent);
        plan.addObject(child);
        plan.setFenceRowJoints(child, parent.endJointId(), child.endJointId());

        plan.synchronizeFenceRows(plan.pixelsPerMeter());

        assertEquals(parent.endPosition(10), child.position());
        parent.setRotationDegrees(90);
        plan.synchronizeFenceRows(10);
        assertEquals(parent.endPosition(10), child.position());
    }

    @Test
    void removingParentKeepsContinuationInPlaceAndDisconnectsIt() {
        EventPlan plan = new EventPlan("Aiad");
        plan.setPixelsPerMeter(10);
        FenceRow parent = new FenceRow("parent", "Esimene", new Position(10, 20));
        FenceRow child = new FenceRow("child", "Jätk", new Position(0, 0));
        plan.addObject(parent);
        plan.addObject(child);
        plan.setFenceRowJoints(child, parent.endJointId(), child.endJointId());
        plan.synchronizeFenceRows(plan.pixelsPerMeter());
        Position connectedPosition = child.position();

        plan.removeObject(parent.id());

        assertEquals(connectedPosition, child.position());
        assertEquals(1, plan.fenceJointDegree(child.startJointId()));
    }

    @Test
    void migratesLegacyParentConnectionToSharedJoint() {
        EventPlan plan = new EventPlan("Vanad aiad");
        FenceRow parent = new FenceRow("parent", "Esimene", new Position(0, 0));
        FenceRow child = new FenceRow("child", "Jätk", parent.endPosition(plan.pixelsPerMeter()));
        child.connectStartTo(parent.id());
        plan.addObject(parent);
        plan.addObject(child);

        plan.migrateLegacyFenceConnections();

        assertEquals(parent.endJointId(), child.startJointId());
        assertEquals(2, plan.fenceJointDegree(parent.endJointId()));
        assertEquals(false, child.connectedAtStart());
    }

    @Test
    void closedFenceNetworkHasNoFirstRowAndMovesAsOneComponent() {
        EventPlan plan = new EventPlan("Ristkülik");
        plan.setPixelsPerMeter(10);
        FenceRow top = fenceRow("top", new Position(0, 0), 0);
        FenceRow right = fenceRow("right", new Position(70, 0), 90);
        FenceRow bottom = fenceRow("bottom", new Position(70, 70), 180);
        FenceRow left = fenceRow("left", new Position(0, 70), -90);
        plan.addObject(top);
        plan.addObject(right);
        plan.addObject(bottom);
        plan.addObject(left);
        plan.setFenceRowJoints(right, top.endJointId(), right.endJointId());
        plan.setFenceRowJoints(bottom, right.endJointId(), bottom.endJointId());
        plan.setFenceRowJoints(left, bottom.endJointId(), top.startJointId());
        assertEquals(4, plan.fenceJoints().size());

        plan.translateFenceNetwork(bottom.id(), 25, -15);

        assertPositionEquals(new Position(25, -15), top.position());
        assertPositionEquals(top.endPosition(10), right.position());
        assertPositionEquals(right.endPosition(10), bottom.position());
        assertPositionEquals(bottom.endPosition(10), left.position());
        assertPositionEquals(top.position(), left.endPosition(10));
    }

    @Test
    void disconnectingClosedCornerCreatesSeparateCoincidentJoint() {
        EventPlan plan = new EventPlan("Nurk");
        FenceRow first = fenceRow("first", new Position(0, 0), 0);
        FenceRow second = fenceRow("second", first.endPosition(plan.pixelsPerMeter()), 90);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        String sharedJointId = second.startJointId();

        plan.disconnectFenceEndpoint(second, true);

        assertNotEquals(sharedJointId, second.startJointId());
        assertEquals(
                plan.findFenceJoint(sharedJointId).orElseThrow().position(),
                plan.findFenceJoint(second.startJointId()).orElseThrow().position()
        );
        assertEquals(1, plan.fenceJointDegree(sharedJointId));
        assertEquals(1, plan.fenceJointDegree(second.startJointId()));
    }

    @Test
    void lockedRowPreventsMovingItsFenceNetworkFromAnotherRow() {
        EventPlan plan = new EventPlan("Lukustatud aiad");
        FenceRow first = fenceRow("first", new Position(0, 0), 0);
        FenceRow second = fenceRow("second", first.endPosition(plan.pixelsPerMeter()), 90);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());
        second.setLocked(true);
        Position originalPosition = first.position();

        assertEquals(false, plan.translateFenceNetwork(first.id(), 50, 50));
        assertEquals(originalPosition, first.position());
    }

    @Test
    void movingSharedJointReshapesOpenNetworkWithoutChangingFenceLengths() {
        EventPlan plan = new EventPlan("Murtud aiarida");
        plan.setPixelsPerMeter(10);
        FenceRow first = fenceRow("first", new Position(0, 0), 0);
        FenceRow second = fenceRow("second", first.endPosition(plan.pixelsPerMeter()), 0);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());

        assertEquals(true, plan.moveFenceEndpoint(first, false, new Position(70, 35)));

        assertPositionEquals(new Position(70, 35), first.endPosition(plan.pixelsPerMeter()));
        assertPositionEquals(first.endPosition(plan.pixelsPerMeter()), second.position());
        assertEquals(70, distance(first.position(), first.endPosition(plan.pixelsPerMeter())), 0.001);
        assertEquals(70, distance(second.position(), second.endPosition(plan.pixelsPerMeter())), 0.001);
    }

    @Test
    void movingClosedCornerKeepsEveryFenceLengthAndSharedConnections() {
        EventPlan plan = new EventPlan("Muudetav ristkülik");
        plan.setPixelsPerMeter(10);
        FenceRow top = fenceRow("top", new Position(0, 0), 0);
        FenceRow right = fenceRow("right", new Position(70, 0), 90);
        FenceRow bottom = fenceRow("bottom", new Position(70, 70), 180);
        FenceRow left = fenceRow("left", new Position(0, 70), -90);
        plan.addObject(top);
        plan.addObject(right);
        plan.addObject(bottom);
        plan.addObject(left);
        plan.setFenceRowJoints(right, top.endJointId(), right.endJointId());
        plan.setFenceRowJoints(bottom, right.endJointId(), bottom.endJointId());
        plan.setFenceRowJoints(left, bottom.endJointId(), top.startJointId());

        assertEquals(true, plan.moveFenceEndpoint(top, false, new Position(90, 20)));

        assertPositionEquals(new Position(90, 20), top.endPosition(plan.pixelsPerMeter()));
        assertPositionEquals(top.endPosition(plan.pixelsPerMeter()), right.position());
        assertPositionEquals(right.endPosition(plan.pixelsPerMeter()), bottom.position());
        assertPositionEquals(bottom.endPosition(plan.pixelsPerMeter()), left.position());
        assertPositionEquals(left.endPosition(plan.pixelsPerMeter()), top.position());
        for (FenceRow row : List.of(top, right, bottom, left)) {
            assertEquals(70, distance(row.position(), row.endPosition(plan.pixelsPerMeter())), 0.01);
        }
    }

    @Test
    void splitsAndRejoinsStraightFenceRowAtPhysicalSegmentBoundary() {
        EventPlan plan = new EventPlan("Jagatav aiarida");
        plan.setPixelsPerMeter(10);
        FenceRow row = new FenceRow("first", "Peasissepääs", new Position(10, 20));
        row.setSegmentCount(5);
        row.setRotationDegrees(30);
        plan.addObject(row);
        String originalStartJointId = row.startJointId();
        String originalEndJointId = row.endJointId();

        FenceRow continuation = plan.splitFenceRow(row, 2, "second");

        assertEquals(2, row.segmentCount());
        assertEquals(3, continuation.segmentCount());
        assertEquals(row.endJointId(), continuation.startJointId());
        assertEquals(originalStartJointId, row.startJointId());
        assertEquals(originalEndJointId, continuation.endJointId());
        assertEquals(2, plan.fenceNetworkRows(row.id()).size());
        assertEquals(true, plan.canRemoveFenceJoint(row.endJointId()));

        FenceRow joined = plan.removeFenceJoint(row.endJointId());

        assertEquals(5, joined.segmentCount());
        assertEquals(1, plan.fenceNetworkRows(joined.id()).size());
        assertEquals(2, plan.fenceJoints().size());
        assertEquals(17.5, joined.totalLengthMeters(), 0.0001);
    }

    @Test
    void removingCornerJointConnectsItsTwoNeighbouringPoints() {
        EventPlan plan = new EventPlan("Nurgaga aiarida");
        plan.setPixelsPerMeter(10);
        FenceRow first = fenceRow("first", new Position(0, 0), 0);
        FenceRow second = fenceRow("second", first.endPosition(plan.pixelsPerMeter()), 90);
        plan.addObject(first);
        plan.addObject(second);
        plan.setFenceRowJoints(second, first.endJointId(), second.endJointId());

        String sharedJointId = first.endJointId();
        Position firstOuter = first.position();
        Position secondOuter = second.endPosition(plan.pixelsPerMeter());

        assertEquals(true, plan.canRemoveFenceJoint(sharedJointId));
        FenceRow joined = plan.removeFenceJoint(sharedJointId);

        assertPositionEquals(firstOuter, joined.position());
        assertPositionEquals(secondOuter, joined.endPosition(plan.pixelsPerMeter()));
        assertEquals(3, joined.segmentCount());
        assertEquals(1, plan.fenceNetworkRows(joined.id()).size());
    }

    @Test
    void repeatedSharedJointMovesNeverChangePhysicalFenceLengthsOrProduceInvalidCoordinates() {
        EventPlan plan = new EventPlan("Stabiilne aiavõrk");
        plan.setPixelsPerMeter(10);
        FenceRow top = fenceRow("top", new Position(0, 0), 0);
        FenceRow right = fenceRow("right", new Position(70, 0), 90);
        FenceRow bottom = fenceRow("bottom", new Position(70, 70), 180);
        FenceRow left = fenceRow("left", new Position(0, 70), -90);
        plan.addObject(top);
        plan.addObject(right);
        plan.addObject(bottom);
        plan.addObject(left);
        plan.setFenceRowJoints(right, top.endJointId(), right.endJointId());
        plan.setFenceRowJoints(bottom, right.endJointId(), bottom.endJointId());
        plan.setFenceRowJoints(left, bottom.endJointId(), top.startJointId());

        for (int index = 0; index < 50; index++) {
            plan.moveFenceEndpoint(top, false, new Position(70 + index * 6, 20 - index * 4));
            for (FenceRow row : List.of(top, right, bottom, left)) {
                assertEquals(3.5, row.segmentLengthMeters(), 0.000001);
                assertEquals(true, Double.isFinite(row.position().x()));
                assertEquals(true, Double.isFinite(row.position().y()));
                assertEquals(true, Math.abs(row.position().x()) < 10_000);
                assertEquals(true, Math.abs(row.position().y()) < 10_000);
            }
        }
    }

    private double distance(Position first, Position second) {
        return Math.hypot(second.x() - first.x(), second.y() - first.y());
    }

    private FenceRow fenceRow(String id, Position position, double rotationDegrees) {
        FenceRow row = new FenceRow(id, id, position);
        row.setSegmentCount(2);
        row.setRotationDegrees(rotationDegrees);
        return row;
    }

    private void assertPositionEquals(Position expected, Position actual) {
        assertEquals(expected.x(), actual.x(), 0.0001);
        assertEquals(expected.y(), actual.y(), 0.0001);
    }
}
