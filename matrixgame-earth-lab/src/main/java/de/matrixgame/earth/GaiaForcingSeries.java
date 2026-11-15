package de.matrixgame.earth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Immutable, dated daily forcing values for the Gaia forest water model.
 *
 * <p>A missing observation is represented explicitly with
 * {@link OptionalDouble#empty()}; it is never silently converted to zero.
 * This class validates data structure and units, not the scientific quality
 * of the source dataset.</p>
 */
public final class GaiaForcingSeries {
    /**
     * One calendar day's forcing. Water depths are millimetres per day.
     */
    public record DailyForcing(
            LocalDate date,
            OptionalDouble precipitationMm,
            OptionalDouble potentialCanopyEvaporationMm,
            OptionalDouble potentialRootZoneEtMm) {
        public DailyForcing {
            Objects.requireNonNull(date, "date");
            validateOptional(precipitationMm, "precipitationMm");
            validateOptional(
                    potentialCanopyEvaporationMm,
                    "potentialCanopyEvaporationMm");
            validateOptional(potentialRootZoneEtMm, "potentialRootZoneEtMm");
        }

        /**
         * Returns true only when all three required forcing values are present.
         */
        public boolean isComplete() {
            return precipitationMm.isPresent()
                    && potentialCanopyEvaporationMm.isPresent()
                    && potentialRootZoneEtMm.isPresent();
        }

        /**
         * Converts this row to the model input or fails explicitly on missing data.
         */
        public GaiaForestWaterCycle.Forcing toModelForcing() {
            return new GaiaForestWaterCycle.Forcing(
                    requirePresent(precipitationMm, "precipitationMm"),
                    requirePresent(
                            potentialCanopyEvaporationMm,
                            "potentialCanopyEvaporationMm"),
                    requirePresent(potentialRootZoneEtMm, "potentialRootZoneEtMm"));
        }

        private double requirePresent(OptionalDouble value, String name) {
            if (value.isEmpty()) {
                throw new IllegalStateException(
                        "Missing forcing value '" + name + "' for " + date
                                + "; missing data must not be treated as zero.");
            }
            return value.getAsDouble();
        }
    }

    private final List<DailyForcing> rows;

    /**
     * Creates a non-empty, strictly contiguous daily series.
     *
     * <p>Represent a missing day's measurement with an explicit row containing
     * {@code OptionalDouble.empty()}. Skipping a date is rejected so that a
     * model step cannot accidentally be interpreted as a longer interval.</p>
     */
    public GaiaForcingSeries(List<DailyForcing> rows) {
        Objects.requireNonNull(rows, "rows");
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Forcing series must not be empty.");
        }

        List<DailyForcing> copy = List.copyOf(rows);
        for (int index = 0; index < copy.size(); index++) {
            DailyForcing row = Objects.requireNonNull(copy.get(index), "row");
            if (index > 0) {
                LocalDate expected = copy.get(index - 1).date().plusDays(1);
                if (!expected.equals(row.date())) {
                    throw new IllegalArgumentException(
                            "Daily forcing dates must be consecutive: expected "
                                    + expected + " but found " + row.date() + ".");
                }
            }
        }
        this.rows = copy;
    }

    public List<DailyForcing> rows() {
        return rows;
    }

    public int size() {
        return rows.size();
    }

    public boolean isComplete() {
        return rows.stream().allMatch(DailyForcing::isComplete);
    }

    public int missingValueCount() {
        int missing = 0;
        for (DailyForcing row : rows) {
            if (row.precipitationMm().isEmpty()) {
                missing++;
            }
            if (row.potentialCanopyEvaporationMm().isEmpty()) {
                missing++;
            }
            if (row.potentialRootZoneEtMm().isEmpty()) {
                missing++;
            }
        }
        return missing;
    }

    /**
     * Converts all rows to model inputs. The first incomplete row stops the run.
     */
    public List<GaiaForestWaterCycle.Forcing> toModelForcings() {
        List<GaiaForestWaterCycle.Forcing> forcings = new ArrayList<>(rows.size());
        for (DailyForcing row : rows) {
            forcings.add(row.toModelForcing());
        }
        return List.copyOf(forcings);
    }

    private static void validateOptional(OptionalDouble value, String name) {
        Objects.requireNonNull(value, name);
        if (value.isPresent()) {
            double amount = value.getAsDouble();
            if (!Double.isFinite(amount) || amount < 0.0) {
                throw new IllegalArgumentException(
                        name + " must be finite and non-negative when present.");
            }
        }
    }
}
