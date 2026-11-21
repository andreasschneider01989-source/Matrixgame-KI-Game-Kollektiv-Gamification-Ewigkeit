# Matrixgame Perception v0.2 – Change Log

## Status
New candidate implementation, locally compiled and tested with Java 21. Based on the repository's verified v0.1 source and PERCEPTION-PROTOCOL.md; not a recovered Grok artifact.

## Preserved contract
- PerceptionFrame remains a Java record with the original six fields in the same order.
- Measurement and interpretation remain separate.
- All seven protocol modalities remain supported.
- interpret(Interpretation, boolean) remains the interpretation gate.

## Improvements
- Modality allow-list validation.
- Reject negative timestamps, null observations, and blank source IDs.
- Reject NaN and infinite confidence values.
- Validate interpretation author, claim, and evidence class A–E.
- Reject null interpretations explicitly.
- Expose supported modalities as an immutable set.

## Known limitation
Java records are shallowly immutable. Object observation can reference mutable data. Producers must use immutable values or defensive copies. Typed modality-specific observations and per-avatar privacy policies remain future work (P1/P2 in the protocol).

## Test command
```bash
javac -d out src/perception/PerceptionFrame.java test/perception/PerceptionFrameTest.java
java -ea -cp out perception.PerceptionFrameTest
```
Expected: `PERCEPTION v0.2: 36/36 checks passed.`
