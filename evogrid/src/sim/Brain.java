package sim;

/**
 * Kleines Feed-Forward-Netz (Input -> Hidden -> Output).
 * Inputs : 8 Sicht-Richtungen x 4 Sensor-KanÃ¤le + Energie (normiert) + Bias-Anteil
 * Outputs: 0..3 Bewegung (IDLE,MOVE_UP,MOVE_RIGHT,MOVE_DOWN,MOVE_LEFT wird aus 5 Werten gewaehlt)
 *          danach Aktionen: DIG/EAT-Graben, PUSH, REPRODUZIEREN
 * Alles per Gewichtsarray -> mutation() ist trivial, Genome sind serialisierbar.
 */
public final class Brain {
    public static final int RAYS = 8;          // N, NE, E, SE, S, SW, W, NW
    public static final int SENSORS = 5;       // SOLID, FOOD/EXIT, ENEMY, ALLY, PHEROMON
    public static final int INPUTS = RAYS * SENSORS + 2;
    public static final int HIDDEN = 12;
    public static final int OUTPUTS = 9;       // 0=idle,1..4 move,5 dig,6 push,7 attack,8 reproduce

    public final double[] w1; // INPUTS x HIDDEN
    public final double[] w2; // HIDDEN x OUTPUTS
    private final double[] hidden = new double[HIDDEN];
    public double[] lastOut = new double[OUTPUTS];

    public Brain(Rng r) {
        w1 = new double[INPUTS * HIDDEN];
        w2 = new double[HIDDEN * OUTPUTS];
        randomize(r);
    }

    public Brain(double[] w1, double[] w2) { this.w1 = w1; this.w2 = w2; }

    public void randomize(Rng r) {
        for (int i = 0; i < w1.length; i++) w1[i] = (r.nextDouble() * 2 - 1) * 1.5;
        for (int i = 0; i < w2.length; i++) w2[i] = (r.nextDouble() * 2 - 1) * 1.5;
    }

    /** int[] Sichtfeld (RAYS*SENSORS) + Energie 0..1 -> Aktion (siehe OUTPUTS). */
    public int decide(double[] vision, double energy) {
        double[] in = new double[INPUTS];
        System.arraycopy(vision, 0, in, 0, vision.length);
        in[INPUTS - 2] = energy;
        in[INPUTS - 1] = 1.0;

        for (int h = 0; h < HIDDEN; h++) {
            double sum = 0;
            for (int i = 0; i < INPUTS; i++) sum += in[i] * w1[h * INPUTS + i];
            hidden[h] = Math.tanh(sum);
        }
        for (int o = 0; o < OUTPUTS; o++) {
            double sum = 0;
            for (int h = 0; h < HIDDEN; h++) sum += hidden[h] * w2[o * HIDDEN + h];
            lastOut[o] = sum;
        }
        int best = 0;
        for (int o = 1; o < OUTPUTS; o++) if (lastOut[o] > lastOut[best]) best = o;
        return best;
    }

    public static Brain crossover(Brain a, Brain b, Rng r) {
        double[] w1 = new double[a.w1.length], w2 = new double[a.w2.length];
        for (int i = 0; i < w1.length; i++) w1[i] = r.chance(0.5) ? a.w1[i] : b.w1[i];
        for (int i = 0; i < w2.length; i++) w2[i] = r.chance(0.5) ? a.w2[i] : b.w2[i];
        return new Brain(w1, w2);
    }

    public void mutate(Rng r, double rate) {
        for (int i = 0; i < w1.length; i++) if (r.chance(rate)) w1[i] += r.nextGaussianLike() * 0.4;        for (int i = 0; i < w2.length; i++) if (r.chance(rate)) w2[i] += r.nextGaussianLike() * 0.4;
    }
}
