package ee.matteus.plaanisepp.gui;

import ee.matteus.plaanisepp.core.model.ConnectorType;
import ee.matteus.plaanisepp.core.model.PowerConnection;

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
    private static final Pattern LENGTH_PATTERN = Pattern.compile("\\d+(?:[,.]\\d+)?");
    private static final Comparator<Row> ROW_COMPARATOR = Comparator
            .comparing(Row::connectorType)
            .thenComparing(Row::consumerName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Row::sourceName, String.CASE_INSENSITIVE_ORDER);

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
                    input.consumerName(),
                    input.sourceName(),
                    connection.connectorType(),
                    !connection.defaultForConsumer(),
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
        Matcher matcher = LENGTH_PATTERN.matcher(notes);
        while (matcher.find()) {
            pieces.add(Double.parseDouble(matcher.group().replace(',', '.')));
        }
        return pieces;
    }

    private boolean cableNoteNeedsReview(String notes) {
        if (notes.isBlank() || !notes.contains("+")) {
            return false;
        }
        for (String part : notes.split("\\+")) {
            if (!part.isBlank() && !LENGTH_PATTERN.matcher(part).find()) {
                return true;
            }
        }
        return false;
    }

    record Input(String consumerName, String sourceName, PowerConnection connection, double mapLengthMeters) {
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
            String consumerName,
            String sourceName,
            ConnectorType connectorType,
            boolean alternativeConnection,
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
