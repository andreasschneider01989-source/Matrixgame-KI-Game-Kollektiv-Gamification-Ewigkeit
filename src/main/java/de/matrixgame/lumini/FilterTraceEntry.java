package de.matrixgame.lumini;
import java.util.Objects;
/** Immutable, human-readable record of one actually executed pipeline stage. */
public final class FilterTraceEntry {
    private final int sequence;
    private final String stage;
    private final FilterStatus status;
    private final String detail;
    public FilterTraceEntry(int sequence, String stage, FilterStatus status, String detail) {
        if (sequence < 0) throw new IllegalArgumentException("sequence must be >= 0");
        if (stage == null || stage.trim().isEmpty()) throw new IllegalArgumentException("stage must not be blank");
        this.sequence = sequence;
        this.stage = stage;
        this.status = Objects.requireNonNull(status, "status");
        this.detail = Objects.requireNonNull(detail, "detail");
    }
    public int getSequence() { return sequence; }
    public String getStage() { return stage; }
    public FilterStatus getStatus() { return status; }
    public String getDetail() { return detail; }
}
