package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class CableRouteEditor {
    private CableRouteEditor() {
    }

    static boolean addPoint(EventPlan plan, String consumerId, Position point) {
        if (plan.findPowerConnectionForConsumer(consumerId).isEmpty()) {
            return false;
        }
        plan.addCableRoutePoint(consumerId, point);
        return true;
    }

    static boolean insertPoint(EventPlan plan, String consumerId, List<Position> path, Position point) {
        if (plan.findPowerConnectionForConsumer(consumerId).isEmpty()) {
            return false;
        }
        int insertionIndex = CableRouteGeometry.closestSegmentIndex(path, point);
        plan.insertCableRoutePoint(consumerId, insertionIndex, point);
        return true;
    }

    static Optional<List<Position>> replacePoint(EventPlan plan, String connectionId, int routePointIndex, Position point) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst()
                .filter(connection -> routePointIndex >= 0 && routePointIndex < connection.routePoints().size())
                .map(connection -> {
                    List<Position> routePoints = new ArrayList<>(connection.routePoints());
                    routePoints.set(routePointIndex, point);
                    plan.updateCableRoutePointsForConnection(connectionId, routePoints);
                    return routePoints;
                });
    }

    static boolean removePoint(EventPlan plan, String connectionId, int routePointIndex) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst()
                .filter(connection -> routePointIndex >= 0 && routePointIndex < connection.routePoints().size())
                .map(connection -> {
                    List<Position> routePoints = new ArrayList<>(connection.routePoints());
                    routePoints.remove(routePointIndex);
                    plan.updateCableRoutePointsForConnection(connectionId, routePoints);
                    return true;
                })
                .orElse(false);
    }

    static boolean insertPointForConnection(
            EventPlan plan,
            String connectionId,
            List<Position> path,
            Position point
    ) {
        List<Position> routePoints = plan.powerConnections().stream()
                .filter(connection -> connection.id().equals(connectionId))
                .findFirst()
                .map(connection -> new ArrayList<>(connection.routePoints()))
                .orElse(null);
        if (routePoints == null) {
            return false;
        }
        int insertionIndex = CableRouteGeometry.closestSegmentIndex(path, point);
        if (insertionIndex < 0 || insertionIndex > routePoints.size()) {
            routePoints.add(point);
        } else {
            routePoints.add(insertionIndex, point);
        }
        plan.updateCableRoutePointsForConnection(connectionId, routePoints);
        return true;
    }

    static boolean clearRoute(EventPlan plan, String consumerId) {
        if (plan.findPowerConnectionForConsumer(consumerId).isEmpty()) {
            return false;
        }
        plan.clearCableRoutePoints(consumerId);
        return true;
    }
}
