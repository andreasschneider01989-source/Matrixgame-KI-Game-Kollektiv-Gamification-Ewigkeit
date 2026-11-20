package sim;

/** Feldtypen des 2D-Grids. Offen fuer neue Typen: einfach ergaenzen. */
public enum Tile {
    EMPTY('.', 0xFF101018),
    WALL('#', 0xFF5A5A6E),
    DIRT('=', 0xFF7A5230),      // grabbar (Boulder-Dash)
    BOULDER('O', 0xFFC8C8D0),   // faellt bei Gravitation, zerquetscht Units
    FOOD('*', 0xFF35D07F),
    EXIT('>', 0xFFFFD75E);

    public final char glyph;
    public final int rgb;

    Tile(char g, int rgb) { glyph = g; this.rgb = rgb; }

    public boolean solid() { return this == WALL || this == BOULDER; }
}
