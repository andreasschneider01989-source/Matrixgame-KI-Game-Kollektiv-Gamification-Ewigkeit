package perception;

/**
 * MATRIXGAME – PERCEPTION KERN v0.1 (Evolution Update 002+)
 *
 * Einheitliches Wahrnehmungsdatenformat fuer ALLE Agenten:
 * Menschen, Kameras, Sensoren, simulierte Units.
 *
 * Grundgesetz (Update 002):
 *   MESSUNG und INTERPRETATION werden NIE in einem Feld gemischt.
 *   Eine Waermebildkamera misst Infrarot – sie misst nicht Angst.
 *
 * Start: java perception.PerceptionFrame TEST
 * Evidenzklasse: C (Demo, noch nicht kompiliert) -> Q-Compile-Kandidat
 */
public record PerceptionFrame(
        String agentId,        // wer nahm wahr?
        String modality,       // VISUAL/AUDITORY/SPATIAL/THERMAL/CHEMICAL/INTERNAL/SOCIAL
        long timestamp,        // wann? (Tick oder Epoch-Millis)
        Object observation,    // was wurde GEMESSEN (roh, spaeter typisiert)
        double confidence,     // wie sicher ist die MESSUNG selbst [0..1]
        String sourceId        // woher stammt der Sensor/Datentraeger
) {

    public PerceptionFrame {
        if (agentId == null || agentId.isBlank())
            throw new IllegalArgumentException("IDENTITAETS-WAECHTER: jede Wahrnehmung braucht eine Unit-ID (Q44)");
        if (confidence < 0.0 || confidence > 1.0)
            throw new IllegalArgumentException("KALIBRIATIONS-WAECHTER: confidence ausserhalb [0,1]");
    }

    /** Interpretation ist eine SEPARATE, spaetere Stufe – nie Teil der Messung. */
    public record Interpretation(
            String interpretiertVon, String claim, double confidence, String evidenceClass) {
    }

    /** STRICT-MODUS (Q30-Prinzip): Interpretation ohne Messung oder falscher Klasse = abgelehnt. */
    public static Interpretation interpret(Interpretation i, boolean hasUnderlyingMeasurement) {
        if (!hasUnderlyingMeasurement)
            throw new IllegalStateException("KONFABULATIONS-WAECHTER: Interpretation ohne Messung (Klasse E)");
        if (i.evidenceClass() == null || !i.evidenceClass().matches("[ABCDE]"))
            throw new IllegalStateException("ETIKETT-WAECHTER: jede Interpretation braucht Evidenzklasse A–E");
        return i;
    }

    public static void main(String[] args) {
        boolean test = args.length > 0 && args[0].equals("TEST");

        // T1: Legale Messung (Kamera sieht Bewegung – mehr nicht)
        PerceptionFrame f1 = new PerceptionFrame("CAMERA_01", "VISUAL", 42L,
                "Bewegung: Objekt unbestimmter Art, 3m", 0.82, "cam-01");
        // T2: Saubere Interpretation mit Messung + Klasse
        Interpretation i1 = interpret(
                new Interpretation("FORSCHUNGS_UNIT", "moeglicherweise Lebewesen", 0.55, "C"), true);
        // T3: Konfabulation ("es hat Angst" ohne Messung) wird abgelehnt
        boolean caught = false;
        try {
            interpret(new Interpretation("SOCIAL_UNIT", "es hat Angst", 0.9, "A"), false);
        } catch (IllegalStateException e) { caught = true; }
        System.out.println("[T1] Messung ok  : " + f1);
        System.out.println("[T2] Deutung ok  : " + i1);
        System.out.println("[T3] Konfabulation abgelehnt: " + caught);
        if (test) {
            if (!caught) throw new AssertionError("T3 fehlgeschlagen");
            System.out.println("TEST BESTANDEN (3/3)");
        }
    }
}
