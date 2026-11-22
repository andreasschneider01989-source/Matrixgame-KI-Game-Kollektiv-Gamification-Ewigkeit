package de.matrixgame.lumini;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
/** Immutable intake record. The original text is retained character-for-character. */
public final class IdeaPacket {
    private final String ideaId;
    private final String originalText;
    private final String source;
    private final Instant createdAt;
    private final EpistemicStatus epistemicStatus;
    private final String privacyClass;
    public IdeaPacket(String originalText, String source) {
        this(UUID.randomUUID().toString(), originalText, source, Instant.now(),
                EpistemicStatus.OPEN, "PRIVATE");
    }
    /** Explicit constructor for reproducible tests, imports and replayed records. */
    public IdeaPacket(String ideaId, String originalText, String source, Instant createdAt,
                      EpistemicStatus epistemicStatus, String privacyClass) {
        this.ideaId = requireText(ideaId, "ideaId");
        this.originalText = requireText(originalText, "originalText");
        this.source = requireText(source, "source");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.epistemicStatus = Objects.requireNonNull(epistemicStatus, "epistemicStatus");
        this.privacyClass = requireText(privacyClass, "privacyClass");
    }
    private static String requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
    public String getIdeaId() { return ideaId; }
    public String getOriginalText() { return originalText; }
    public String getSource() { return source; }
    public Instant getCreatedAt() { return createdAt; }
    public EpistemicStatus getEpistemicStatus() { return epistemicStatus; }
    public String getPrivacyClass() { return privacyClass; }
}
