package ee.matteus.plaanisepp.core.service;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.Equipment;
import ee.matteus.plaanisepp.core.model.EquipmentContainer;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerConsumer;
import ee.matteus.plaanisepp.core.model.PowerOutlet;
import ee.matteus.plaanisepp.core.model.PowerSource;

import java.util.ArrayList;
import java.util.List;

public final class PowerHierarchyService {
    public Hierarchy summarize(EventPlan plan) {
        List<SourceRow> sources = plan.powerSources().stream()
                .map(source -> sourceRow(plan, source))
                .toList();
        List<UnconnectedConsumerRow> unconnected = plan.powerConsumers().stream()
                .filter(consumer -> plan.findPowerConnectionForConsumer(consumer.id()).isEmpty())
                .map(consumer -> new UnconnectedConsumerRow(
                        consumer.id(), consumer.name(), consumer.requiredWatts()
                ))
                .toList();
        return new Hierarchy(sources, unconnected);
    }

    private SourceRow sourceRow(EventPlan plan, PowerSource source) {
        List<OutletRow> outlets = new ArrayList<>();
        for (int index = 0; index < source.outlets().size(); index++) {
            PowerOutlet outlet = source.outlets().get(index);
            outlets.add(new OutletRow(
                    outlet.id(),
                    outlet.name(),
                    outlet.type(),
                    outletTypeIndex(source, outlet, index),
                    outlet.capacityWatts(),
                    plan.outletDemandWatts(outlet.id()),
                    connections(plan, source.id(), outlet.id())
            ));
        }
        List<ConsumerRow> directConsumers = connections(plan, source.id(), "");
        int usedWatts = plan.powerConnections().stream()
                .filter(connection -> connection.sourceId().equals(source.id()))
                .mapToInt(plan::powerDemandWatts)
                .sum();
        return new SourceRow(
                source.id(), source.name(), source.totalCapacityWatts(), usedWatts, outlets, directConsumers
        );
    }

    private List<ConsumerRow> connections(EventPlan plan, String sourceId, String outletId) {
        return plan.powerConnections().stream()
                .filter(connection -> connection.sourceId().equals(sourceId))
                .filter(connection -> connection.outletId().equals(outletId))
                .map(connection -> consumerRow(plan, connection))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private ConsumerRow consumerRow(EventPlan plan, PowerConnection connection) {
        PlannerObject object = plan.findObject(connection.consumerId()).orElse(null);
        if (!(object instanceof PowerConsumer consumer)) {
            return null;
        }
        List<EquipmentRow> equipment = new ArrayList<>();
        if (object instanceof EquipmentContainer container) {
            for (Equipment item : container.equipment()) {
                boolean usesConnection = item.usesDefaultPower()
                        ? connection.defaultForConsumer()
                        : connection.id().equals(item.powerConnectionId());
                if (usesConnection) {
                    equipment.add(new EquipmentRow(item.id(), item.name(), item.requiredWatts()));
                }
            }
        }
        return new ConsumerRow(
                consumer.id(),
                consumer.name(),
                plan.powerDemandWatts(connection),
                connection.connectorType(),
                !connection.defaultForConsumer(),
                object.groupName(),
                equipment
        );
    }

    private int outletTypeIndex(PowerSource source, PowerOutlet targetOutlet, int targetIndex) {
        int matchingIndex = 0;
        for (int index = 0; index <= targetIndex; index++) {
            if (source.outlets().get(index).type() == targetOutlet.type()) {
                matchingIndex++;
            }
        }
        return matchingIndex;
    }

    public record Hierarchy(List<SourceRow> sources, List<UnconnectedConsumerRow> unconnectedConsumers) {
        public Hierarchy {
            sources = List.copyOf(sources);
            unconnectedConsumers = List.copyOf(unconnectedConsumers);
        }
    }

    public record SourceRow(
            String id,
            String name,
            int capacityWatts,
            int usedWatts,
            List<OutletRow> outlets,
            List<ConsumerRow> directConsumers
    ) {
        public SourceRow {
            outlets = List.copyOf(outlets);
            directConsumers = List.copyOf(directConsumers);
        }

        public int remainingWatts() {
            return capacityWatts - usedWatts;
        }
    }

    public record OutletRow(
            String id,
            String name,
            ConnectorType type,
            int typeIndex,
            int capacityWatts,
            int usedWatts,
            List<ConsumerRow> consumers
    ) {
        public OutletRow {
            consumers = List.copyOf(consumers);
        }

        public int remainingWatts() {
            return capacityWatts - usedWatts;
        }
    }

    public record ConsumerRow(
            String id,
            String name,
            int usedWatts,
            ConnectorType connectorType,
            boolean alternativeConnection,
            String groupName,
            List<EquipmentRow> equipment
    ) {
        public ConsumerRow {
            groupName = groupName == null ? "" : groupName;
            equipment = List.copyOf(equipment);
        }
    }

    public record EquipmentRow(String id, String name, int requiredWatts) {
    }

    public record UnconnectedConsumerRow(String id, String name, int requiredWatts) {
    }
}
