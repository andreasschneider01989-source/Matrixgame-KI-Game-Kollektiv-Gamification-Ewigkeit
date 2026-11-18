# v0.8.4 – FormatException-Fix (18.11.2026)

## Fehlerursache
In `MatrixgameUniversalGridLab_v08_4.refresh()` wurde in der Genom-/Unit-Inspektor-Ausgabe eine falsche Argumentreihenfolge an `String.format()` übergeben. Dadurch erhielt der Platzhalter `%s` für die Eltern-ID einen `Double`, was den Start mit

`java.util.IllegalFormatConversionException: d != java.lang.Double`

beendete.

## Korrektur
Die Anzeige enthält jetzt ausdrücklich die Teilungsschwelle und die daraus berechneten Reproduktionskosten. Formatstring und Argumentliste stimmen überein:

```java
"<html><b>Zelle %s</b> | Team %d | Gen %d | HP %.0f | Energie %.2f | Fitness %.2f | Teilungsschwelle %.2f | Reproduktionskosten %.2f | Eltern %s | Mutationen %d<br>" +
```

Dazugehörige Argumente in genau dieser Reihenfolge:

```java
selected.id, (int) selected.value("team") + 1,
(int) selected.value("generation"), selected.value("hp"), selected.value("energy"),
selected.value("fitness"), selected.value("reproductionThreshold"),
selected.value("reproductionThreshold") * 0.42,
selected.parentId.isBlank() ? "—" : selected.parentId,
selected.mutationEvents, ...
```

## Verifikation der lokalen Reparatur
- Java 17: `javac --release 17 -Xlint:all -Werror` erfolgreich.
- Headless-Selbsttests: 63 Checks bestanden.
- GUI-Smoke-Test: Swing-Oberfläche startete und blieb bis zum absichtlichen Test-Timeout aktiv.
- Vollständiger manueller Windows-/Tastaturtest steht noch aus.

## Wichtige Synchronisationsnotiz
Dieses Dokument protokolliert den Reparatur-Patch. Es ist **nicht** die vollständige Quelldatei v0.8.4. Der lokale, getestete Quellstand und das ZIP-Archiv bleiben bis zur separaten Übertragung der vollständigen Quelldatei die maßgebliche v0.8.4-Referenz.
