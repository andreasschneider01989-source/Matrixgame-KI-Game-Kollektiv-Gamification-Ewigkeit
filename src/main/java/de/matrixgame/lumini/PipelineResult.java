package de.matrixgame.lumini;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
/** Immutable result with both a console projection and a dependency-free JSONL export. */
public final class PipelineResult {
    private final IdeaPacket packet;
    private final String intentHint;
    private final List<PipelineRoute> suggestedRoutes;
    private final List<FilterTraceEntry> trace;
    public PipelineResult(IdeaPacket packet, String intentHint,
                          List<PipelineRoute> suggestedRoutes, List<FilterTraceEntry> trace) {
        this.packet = Objects.requireNonNull(packet, "packet");
        this.intentHint = Objects.requireNonNull(intentHint, "intentHint");
        this.suggestedRoutes = Collections.unmodifiableList(new ArrayList<>(suggestedRoutes));
        this.trace = Collections.unmodifiableList(new ArrayList<>(trace));
    }
    public IdeaPacket getPacket() { return packet; }
    public String getIntentHint() { return intentHint; }
    public List<PipelineRoute> getSuggestedRoutes() { return suggestedRoutes; }
    public List<FilterTraceEntry> getTrace() { return trace; }
    public String toHumanReport() {
        StringBuilder out = new StringBuilder();
        out.append("[LUMINI] idea=").append(packet.getIdeaId()).append('\n');
        for (FilterTraceEntry entry : trace) {
            out.append(String.format("[%02d %-12s] %-16s | %s%n",
                    entry.getSequence(), entry.getStage(), entry.getStatus(), entry.getDetail()));
        }
        out.append("[ROUTE] ").append(suggestedRoutes).append('\n');
        out.append("[EPISTEMIC] ").append(packet.getEpistemicStatus())
                .append(" | privacy=").append(packet.getPrivacyClass()).append('\n');
        out.append("[NEXT] Human review required before model claims, publication or code generation.");
        return out.toString();
    }
    /** One valid JSON object per line. Original text is escaped, never rewritten. */
    public String toJsonLine() {
        String routes = suggestedRoutes.stream().map(route -> quote(route.name()))
                .collect(Collectors.joining(","));
        String stages = trace.stream().map(entry -> "{"
                + "\"sequence\":" + entry.getSequence() + ","
                + "\"stage\":" + quote(entry.getStage()) + ","
                + "\"status\":" + quote(entry.getStatus().name()) + ","
                + "\"detail\":" + quote(entry.getDetail()) + "}"
        ).collect(Collectors.joining(","));
        return "{"
                + "\"schemaVersion\":\"1.0\","
                + "\"ideaId\":" + quote(packet.getIdeaId()) + ","
                + "\"createdAt\":" + quote(packet.getCreatedAt().toString()) + ","
                + "\"source\":" + quote(packet.getSource()) + ","
                + "\"privacyClass\":" + quote(packet.getPrivacyClass()) + ","
                + "\"originalText\":" + quote(packet.getOriginalText()) + ","
                + "\"epistemicStatus\":" + quote(packet.getEpistemicStatus().name()) + ","
                + "\"intentHint\":" + quote(intentHint) + ","
                + "\"suggestedRoutes\":[" + routes + "],"
                + "\"filterTrace\":[" + stages + "]"
                + "}";
    }
    private static String quote(String value) {
        StringBuilder out = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\b': out.append("\\b"); break;
                case '\f': out.append("\\f"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (ch < 0x20) out.append(String.format("\\u%04x", (int) ch));
                    else out.append(ch);
            }
        }
        return out.append('"').toString();
    }
}
