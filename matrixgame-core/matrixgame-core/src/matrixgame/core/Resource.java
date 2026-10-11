package matrixgame.core;

/** Alles Begrenzte. Logistisches Nachwachsen: g = r * x * (1 - x / K). */
public final class Resource {
    private final String name;
    private final double capacity;
    private final double growthRate;
    private double amount;

    public Resource(String name, double amount, double capacity, double growthRate) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity > 0");
        this.name = name;
        this.capacity = capacity;
        this.growthRate = growthRate;
        this.amount = Math.max(0, Math.min(amount, capacity));
    }
    public String name() { return name; }
    public double amount() { return amount; }
    public double capacity() { return capacity; }
    public double growthRate() { return growthRate; }
    public double growth() { return growthRate * amount * (1 - amount / capacity); }

    /** Entnimmt hoechstens den vorhandenen Bestand und gibt die tatsaechliche Menge zurueck. */
    public double take(double want) {
        double t = Math.max(0, Math.min(want, amount));
        amount -= t;
        return t;
    }
    /** Ein Nachwachs-Schritt mit relativem Rauschen (Zufall). Bestand 0 bleibt 0 (Kollaps ist irreversibel). */
    public void regrow(double relativeNoise) {
        amount = Math.max(0, Math.min(capacity, amount + growth() * (1 + relativeNoise)));
    }
}
