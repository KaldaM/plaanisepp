package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.EventPlan;
import ee.matteus.plaanisepp.core.model.PlannerObject;
import ee.matteus.plaanisepp.core.model.PowerConnection;
import ee.matteus.plaanisepp.core.model.PowerConsumer;
import ee.matteus.plaanisepp.core.model.PowerSource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CableInventorySummaryService {
    private static final Pattern PIECE_PATTERN = Pattern.compile(
            "(?:(\\d+)\\s*[x×]\\s*)?(\\d+(?:[,.]\\d+)?)\\s*m?"
    );
    private static final Comparator<Row> ROW_COMPARATOR = Comparator
            .comparing(Row::connectorType)
            .thenComparing(Row::consumerName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Row::sourceName, String.CASE_INSENSITIVE_ORDER);

    Summary summarize(EventPlan plan) {
        List<Input> inputs = new ArrayList<>();
        for (PowerConnection connection : plan.powerConnections()) {
            PlannerObject consumer = plan.findObject(connection.consumerId()).orElse(null);
            if (!(consumer instanceof PowerConsumer)) {
                continue;
            }
            PowerSource source = plan.findObject(connection.sourceId())
                    .filter(PowerSource.class::isInstance)
                    .map(PowerSource.class::cast)
                    .orElse(null);
            if (source == null) {
                continue;
            }
            double mapLengthMeters = CableDisplayHelper.lengthMeters(
                    CablePathHelper.cablePath(consumer, source, connection, plan.pixelsPerMeter()),
                    plan.pixelsPerMeter()
            );
            inputs.add(new Input(consumer.name(), source.name(), connection, mapLengthMeters, consumer.hidden()));
        }
        return summarize(inputs);
    }

    Summary summarize(List<Input> inputs) {
        List<Row> rows = new ArrayList<>();
        Map<ConnectorType, MutableTypeSummary> mutableTypes = new EnumMap<>(ConnectorType.class);
        double totalMapLengthMeters = 0;
        double totalNotedLengthMeters = 0;
        boolean hasNotedLength = false;

        for (Input input : inputs) {
            PowerConnection connection = input.connection();
            List<Double> pieces = cableLengthPieces(connection.cableLengthNotes());
            OptionalDouble notedLength = pieces.isEmpty()
                    ? OptionalDouble.empty()
                    : OptionalDouble.of(pieces.stream().mapToDouble(Double::doubleValue).sum());
            totalMapLengthMeters += input.mapLengthMeters();
            MutableTypeSummary type = mutableTypes.computeIfAbsent(
                    connection.connectorType(), ignored -> new MutableTypeSummary()
            );
            type.mapLengthMeters += input.mapLengthMeters();
            if (notedLength.isPresent()) {
                double length = notedLength.getAsDouble();
                totalNotedLengthMeters += length;
                hasNotedLength = true;
                type.notedLengthMeters += length;
                type.hasNotedLength = true;
                pieces.forEach(piece -> type.pieceCounts.merge(piece, 1, Integer::sum));
            }
            rows.add(new Row(
                    connection.id(),
                    connection.consumerId(),
                    input.consumerName(),
                    input.sourceName(),
                    connection.connectorType(),
                    !connection.defaultForConsumer(),
                    input.consumerHidden(),
                    input.mapLengthMeters(),
                    notedLength,
                    connection.cableNotes(),
                    cableNoteNeedsReview(connection.cableLengthNotes())
            ));
        }

        rows.sort(ROW_COMPARATOR);
        Map<ConnectorType, TypeSummary> types = new EnumMap<>(ConnectorType.class);
        mutableTypes.forEach((connectorType, type) -> types.put(connectorType, type.toSummary()));
        return new Summary(rows, totalMapLengthMeters, totalNotedLengthMeters, hasNotedLength, types);
    }

    private List<Double> cableLengthPieces(String notes) {
        List<Double> pieces = new ArrayList<>();
        for (String part : notes.split("\\+")) {
            Matcher matcher = PIECE_PATTERN.matcher(part.trim());
            if (!matcher.matches()) {
                continue;
            }
            int count = matcher.group(1) == null ? 1 : Integer.parseInt(matcher.group(1));
            double length = Double.parseDouble(matcher.group(2).replace(',', '.'));
            for (int index = 0; index < count; index++) {
                pieces.add(length);
            }
        }
        return pieces;
    }

    private boolean cableNoteNeedsReview(String notes) {
        if (notes.isBlank()) {
            return false;
        }
        for (String part : notes.split("\\+")) {
            if (!part.isBlank() && !PIECE_PATTERN.matcher(part.trim()).matches()) {
                return true;
            }
        }
        return false;
    }

    record Input(
            String consumerName,
            String sourceName,
            PowerConnection connection,
            double mapLengthMeters,
            boolean consumerHidden
    ) {
        Input(String consumerName, String sourceName, PowerConnection connection, double mapLengthMeters) {
            this(consumerName, sourceName, connection, mapLengthMeters, false);
        }
    }

    record Summary(
            List<Row> rows,
            double totalMapLengthMeters,
            double totalNotedLengthMeters,
            boolean hasNotedLength,
            Map<ConnectorType, TypeSummary> byType
    ) {
        Summary {
            rows = List.copyOf(rows);
            byType = Map.copyOf(byType);
        }

        boolean isEmpty() {
            return rows.isEmpty();
        }
    }

    record Row(
            String connectionId,
            String consumerId,
            String consumerName,
            String sourceName,
            ConnectorType connectorType,
            boolean alternativeConnection,
            boolean consumerHidden,
            double mapLengthMeters,
            OptionalDouble notedLengthMeters,
            String cableNotes,
            boolean noteNeedsReview
    ) {
    }

    record TypeSummary(
            double mapLengthMeters,
            double notedLengthMeters,
            boolean hasNotedLength,
            Map<Double, Integer> pieceCounts
    ) {
        TypeSummary {
            pieceCounts = Map.copyOf(pieceCounts);
        }
    }

    private static final class MutableTypeSummary {
        private double mapLengthMeters;
        private double notedLengthMeters;
        private boolean hasNotedLength;
        private final Map<Double, Integer> pieceCounts = new TreeMap<>();

        private TypeSummary toSummary() {
            return new TypeSummary(mapLengthMeters, notedLengthMeters, hasNotedLength, pieceCounts);
        }
    }
}
