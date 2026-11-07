package de.matrixgame.language;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/** Small, dependency-free LUMINI compiler prototype. */
public final class LuminiCompiler {
    private LuminiCompiler() {}

    public static CompilationResult compile(String source) {
        Objects.requireNonNull(source, "source");
        return new Parser(source).parse();
    }

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            System.err.println("Usage: lumini <source.lumini> [--out <model-ir.json>]");
            System.exit(2);
        }
        try {
            Path sourcePath = Path.of(args[0]);
            CompilationResult result = compile(Files.readString(sourcePath));
            if (args.length == 3 && "--out".equals(args[1])) {
                Path outputPath = Path.of(args[2]);
                Path parent = outputPath.toAbsolutePath().getParent();
                if (parent != null) Files.createDirectories(parent);
                Files.writeString(outputPath, result.toJson());
                System.out.println("LUMINI OK: " + result.modelId());
                System.out.println("Model IR: " + outputPath);
            } else if (args.length == 1) {
                System.out.println(result.toJson());
            } else {
                throw new IllegalArgumentException("Expected --out <model-ir.json>.");
            }
        } catch (Exception exception) {
            System.err.println("LUMINI ERROR: " + exception.getMessage());
            System.exit(1);
        }
    }

    public record CompilationResult(
            String modelId,
            String epistemicStatus,
            List<Declaration> declarations,
            List<UnitAssertion> assertions) {
        public CompilationResult {
            declarations = List.copyOf(declarations);
            assertions = List.copyOf(assertions);
        }

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"ir_version\": \"0.1.0\",\n");
            json.append("  \"model_id\": ").append(quote(modelId)).append(",\n");
            json.append("  \"model_kind\": \"generic\",\n");
            json.append("  \"epistemic_status\": ").append(quote(epistemicStatus)).append(",\n");
            json.append("  \"declarations\": [");
            for (int index = 0; index < declarations.size(); index++) {
                Declaration declaration = declarations.get(index);
                json.append(index == 0 ? "\n" : ",\n");
                json.append("    {\"kind\": ").append(quote(declaration.kind()))
                        .append(", \"name\": ").append(quote(declaration.name())).append(", ");
                if ("parameter".equals(declaration.kind())) {
                    json.append("\"value\": ").append(declaration.valueLiteral())
                            .append(", \"unit\": ").append(quote(declaration.unit().display())).append(", ");
                } else {
                    json.append("\"expression\": ").append(quote(declaration.expression())).append(", ");
                }
                json.append("\"dimension\": ").append(declaration.dimension().toJson()).append("}");
            }
            if (!declarations.isEmpty()) json.append("\n  ");
            json.append("],\n");
            json.append("  \"unit_assertions\": [");
            for (int index = 0; index < assertions.size(); index++) {
                UnitAssertion assertion = assertions.get(index);
                json.append(index == 0 ? "\n" : ",\n");
                json.append("    {\"name\": ").append(quote(assertion.name()))
                        .append(", \"expected_unit\": ").append(quote(assertion.expected().display()))
                        .append(", \"dimension\": ").append(assertion.expected().dimension().toJson())
                        .append("}");
            }
            if (!assertions.isEmpty()) json.append("\n  ");
            json.append("]\n");
            json.append("}\n");
            return json.toString();
        }
    }

    public record Declaration(
            String kind, String name, String valueLiteral, Unit unit,
            String expression, Dimension dimension) {}

    public record UnitAssertion(String name, Unit expected) {}

    public record Dimension(int mass, int length, int time) {
        Dimension multiply(Dimension other) {
            return new Dimension(mass + other.mass, length + other.length, time + other.time);
        }
        Dimension divide(Dimension other) {
            return new Dimension(mass - other.mass, length - other.length, time - other.time);
        }
        Dimension power(int exponent) {
            return new Dimension(mass * exponent, length * exponent, time * exponent);
        }
        boolean isCompatible(Dimension other) {
            return equals(other);
        }
        String toJson() {
            return "{\"mass\": " + mass + ", \"length\": " + length + ", \"time\": " + time + "}";
        }
        String displayUnit() {
            List<String> numerator = new ArrayList<>();
            List<String> denominator = new ArrayList<>();
            addUnit(numerator, denominator, "kg", mass);
            addUnit(numerator, denominator, "m", length);
            addUnit(numerator, denominator, "s", time);
            if (numerator.isEmpty()) numerator.add("1");
            String top = String.join(" * ", numerator);
            return denominator.isEmpty() ? top : top + " / " + String.join(" * ", denominator);
        }
        private static void addUnit(List<String> numerator, List<String> denominator,
                                    String symbol, int exponent) {
            if (exponent > 0) numerator.add(powerText(symbol, exponent));
            else if (exponent < 0) denominator.add(powerText(symbol, -exponent));
        }
        private static String powerText(String symbol, int exponent) {
            return exponent == 1 ? symbol : symbol + "^" + exponent;
        }
    }

    public record Unit(Dimension dimension) {
        public String display() { return dimension.displayUnit(); }
    }

    private record Symbol(String name, Dimension dimension) {}

    private interface Expression {
        Dimension dimension();
        String render();
    }

    private record NameExpression(Symbol symbol) implements Expression {
        @Override public Dimension dimension() { return symbol.dimension(); }
        @Override public String render() { return symbol.name(); }
    }

    private record BinaryExpression(String operator, Expression left,
                                    Expression right, Dimension dimension) implements Expression {
        @Override public String render() {
            return "(" + left.render() + " " + operator + " " + right.render() + ")";
        }
    }

    private static final class Parser {
        private final List<Token> tokens;
        private int position;
        private String modelId;
        private String epistemicStatus;
        private final Map<String, Symbol> symbols = new LinkedHashMap<>();
        private final List<Declaration> declarations = new ArrayList<>();
        private final List<UnitAssertion> assertions = new ArrayList<>();

        Parser(String source) { tokens = new Lexer(source).scan(); }

        CompilationResult parse() {
            expectIdent("project");
            modelId = expect("STRING", "project name").text();
            expectSymbol("{");
            while (!checkSymbol("}") && !check("EOF")) {
                if (checkIdent("claim_status")) parseClaimStatus();
                else if (checkIdent("parameter")) parseParameter();
                else if (checkIdent("derive")) parseDerived();
                else if (checkIdent("assert_unit")) parseUnitAssertion();
                else fail(peek(), "Expected claim_status, parameter, derive or assert_unit.");
            }
            expectSymbol("}");
            expect("EOF", "end of file");
            if (epistemicStatus == null) throw error(previous(), "Missing required claim_status declaration.");
            if (declarations.isEmpty()) throw error(previous(), "A model must contain at least one declaration.");
            return new CompilationResult(modelId, epistemicStatus, declarations, assertions);
        }

        private void parseClaimStatus() {
            expectIdent("claim_status");
            expectSymbol("=");
            Token status = expect("IDENT", "epistemic status");
            String value = status.text().toUpperCase(Locale.ROOT);
            if (!List.of("FACT", "MODEL", "HYPOTHESIS", "OPEN", "FICTION").contains(value))
                throw error(status, "Unknown claim_status. Use FACT, MODEL, HYPOTHESIS, OPEN or FICTION.");
            if (epistemicStatus != null) throw error(status, "claim_status may only be declared once.");
            epistemicStatus = value;
            expectSymbol(";");
        }

        private void parseParameter() {
            expectIdent("parameter");
            Token name = expect("IDENT", "parameter name");
            ensureNewName(name);
            expectSymbol("=");
            Token number = expect("NUMBER", "numeric parameter value");
            double parsed;
            try { parsed = Double.parseDouble(number.text()); }
            catch (NumberFormatException exception) { throw error(number, "Invalid numeric literal."); }
            if (!Double.isFinite(parsed)) throw error(number, "Parameter values must be finite.");
            Unit unit = parseUnitExpression();
            expectSymbol(";");
            Symbol symbol = new Symbol(name.text(), unit.dimension());
            symbols.put(name.text(), symbol);
            declarations.add(new Declaration("parameter", name.text(), number.text(),
                    unit, null, unit.dimension()));
        }

        private void parseDerived() {
            expectIdent("derive");
            Token name = expect("IDENT", "derived variable name");
            ensureNewName(name);
            expectSymbol("=");
            Expression expression = parseExpression();
            expectSymbol(";");
            Symbol symbol = new Symbol(name.text(), expression.dimension());
            symbols.put(name.text(), symbol);
            declarations.add(new Declaration("derived", name.text(), null, null,
                    expression.render(), expression.dimension()));
        }

        private void parseUnitAssertion() {
            expectIdent("assert_unit");
            Token name = expect("IDENT", "variable name");
            Symbol symbol = symbols.get(name.text());
            if (symbol == null) throw error(name, "Unknown variable '" + name.text() + "'.");
            expectSymbol("=");
            Unit expected = parseUnitExpression();
            expectSymbol(";");
            if (!symbol.dimension().isCompatible(expected.dimension()))
                throw error(name, "Unit mismatch for '" + name.text() + "': inferred "
                        + symbol.dimension().displayUnit() + " but asserted " + expected.display() + ".");
            assertions.add(new UnitAssertion(name.text(), expected));
        }

        private Expression parseExpression() { return parseAdditive(); }

        private Expression parseAdditive() {
            Expression expression = parseMultiplicative();
            while (checkSymbol("+") || checkSymbol("-")) {
                Token operator = advance();
                Expression right = parseMultiplicative();
                if (!expression.dimension().isCompatible(right.dimension()))
                    throw error(operator, "Cannot " + ("+".equals(operator.text()) ? "add" : "subtract")
                            + " incompatible dimensions " + expression.dimension().displayUnit()
                            + " and " + right.dimension().displayUnit() + ".");
                expression = new BinaryExpression(operator.text(), expression, right, expression.dimension());
            }
            return expression;
        }

        private Expression parseMultiplicative() {
            Expression expression = parsePrimary();
            while (checkSymbol("*") || checkSymbol("/")) {
                Token operator = advance();
                Expression right = parsePrimary();
                Dimension result = "*".equals(operator.text())
                        ? expression.dimension().multiply(right.dimension())
                        : expression.dimension().divide(right.dimension());
                expression = new BinaryExpression(operator.text(), expression, right, result);
            }
            return expression;
        }

        private Expression parsePrimary() {
            if (matchSymbol("(")) {
                Expression nested = parseExpression();
                expectSymbol(")");
                return nested;
            }
            Token name = expect("IDENT", "variable name");
            Symbol symbol = symbols.get(name.text());
            if (symbol == null)
                throw error(name, "Unknown variable '" + name.text() + "'. Declare it before using it.");
            return new NameExpression(symbol);
        }

        private Unit parseUnitExpression() {
            Dimension dimension = parseUnitFactor();
            while (checkSymbol("*") || checkSymbol("/")) {
                Token operator = advance();
                Dimension right = parseUnitFactor();
                dimension = "*".equals(operator.text())
                        ? dimension.multiply(right) : dimension.divide(right);
            }
            return new Unit(dimension);
        }

        private Dimension parseUnitFactor() {
            Token unitName = expect("IDENT", "SI unit (kg, m or s)");
            Dimension base = switch (unitName.text()) {
                case "kg" -> new Dimension(1, 0, 0);
                case "m" -> new Dimension(0, 1, 0);
                case "s" -> new Dimension(0, 0, 1);
                default -> throw error(unitName, "Unsupported unit '" + unitName.text()
                        + "'. MVP supports only SI base units kg, m and s.");
            };
            if (matchSymbol("^")) {
                boolean negative = matchSymbol("-");
                Token exponentToken = expect("NUMBER", "integer unit exponent");
                if (!exponentToken.text().matches("[0-9]+"))
                    throw error(exponentToken, "Unit exponent must be an integer.");
                int exponent = Integer.parseInt(exponentToken.text());
                if (negative) exponent = -exponent;
                if (Math.abs(exponent) > 12)
                    throw error(exponentToken, "Unit exponent magnitude must not exceed 12.");
                return base.power(exponent);
            }
            return base;
        }

        private void ensureNewName(Token name) {
            if (symbols.containsKey(name.text())) throw error(name, "Duplicate declaration '" + name.text() + "'.");
        }
        private boolean checkIdent(String value) { return check("IDENT") && peek().text().equals(value); }
        private void expectIdent(String value) {
            if (!checkIdent(value)) fail(peek(), "Expected '" + value + "'.");
            advance();
        }
        private boolean checkSymbol(String symbol) { return check(symbol); }
        private void expectSymbol(String symbol) {
            if (!matchSymbol(symbol)) fail(peek(), "Expected '" + symbol + "'.");
        }
        private boolean matchSymbol(String symbol) {
            if (checkSymbol(symbol)) { advance(); return true; }
            return false;
        }
        private boolean check(String kind) { return peek().kind().equals(kind); }
        private Token expect(String kind, String description) {
            if (!check(kind)) fail(peek(), "Expected " + description + ".");
            return advance();
        }
        private Token advance() {
            if (!check("EOF")) position++;
            return previous();
        }
        private Token peek() { return tokens.get(position); }
        private Token previous() { return tokens.get(Math.max(0, position - 1)); }
        private void fail(Token token, String message) { throw error(token, message); }
        private CompilerException error(Token token, String message) {
            return new CompilerException("line " + token.line() + ", column " + token.column() + ": " + message);
        }
    }

    private record Token(String kind, String text, int line, int column) {}

    private static final class Lexer {
        private final String source;
        private final List<Token> tokens = new ArrayList<>();
        private int offset;
        private int line = 1;
        private int column = 1;

        Lexer(String source) { this.source = source; }

        List<Token> scan() {
            while (!atEnd()) {
                char current = peekChar();
                if (Character.isWhitespace(current)) consumeChar();
                else if (current == '/' && peekNextChar() == '/') consumeComment();
                else if (current == '"') scanString();
                else if (Character.isDigit(current)
                        || (current == '.' && Character.isDigit(peekNextChar()))) scanNumber();
                else if (Character.isLetter(current) || current == '_') scanIdentifier();
                else if ("{}=;*/^()+-".indexOf(current) >= 0) {
                    tokens.add(new Token(String.valueOf(current), String.valueOf(current), line, column));
                    consumeChar();
                } else {
                    throw new CompilerException("line " + line + ", column " + column
                            + ": unexpected character '" + current + "'.");
                }
            }
            tokens.add(new Token("EOF", "", line, column));
            return List.copyOf(tokens);
        }

        private void scanIdentifier() {
            int start = offset, startLine = line, startColumn = column;
            while (!atEnd()) {
                char c = peekChar();
                if (!Character.isLetterOrDigit(c) && c != '_') break;
                consumeChar();
            }
            tokens.add(new Token("IDENT", source.substring(start, offset), startLine, startColumn));
        }

        private void scanNumber() {
            int start = offset, startLine = line, startColumn = column;
            while (!atEnd() && Character.isDigit(peekChar())) consumeChar();
            if (!atEnd() && peekChar() == '.') {
                consumeChar();
                while (!atEnd() && Character.isDigit(peekChar())) consumeChar();
            }
            if (!atEnd() && (peekChar() == 'e' || peekChar() == 'E')) {
                consumeChar();
                if (!atEnd() && (peekChar() == '+' || peekChar() == '-')) consumeChar();
                int exponentStart = offset;
                while (!atEnd() && Character.isDigit(peekChar())) consumeChar();
                if (exponentStart == offset)
                    throw new CompilerException("line " + startLine + ", column " + startColumn
                            + ": malformed scientific notation.");
            }
            tokens.add(new Token("NUMBER", source.substring(start, offset), startLine, startColumn));
        }

        private void scanString() {
            int startLine = line, startColumn = column;
            consumeChar();
            StringBuilder value = new StringBuilder();
            while (!atEnd() && peekChar() != '"') {
                char c = consumeChar();
                if (c == '\\') {
                    if (atEnd()) throw new CompilerException("line " + startLine + ", column "
                            + startColumn + ": unfinished string escape.");
                    char escaped = consumeChar();
                    switch (escaped) {
                        case '"' -> value.append('"');
                        case '\\' -> value.append('\\');
                        case 'n' -> value.append('\n');
                        case 't' -> value.append('\t');
                        default -> throw new CompilerException("line " + line + ", column "
                                + column + ": unsupported string escape.");
                    }
                } else value.append(c);
            }
            if (atEnd()) throw new CompilerException("line " + startLine + ", column "
                    + startColumn + ": unterminated string.");
            consumeChar();
            tokens.add(new Token("STRING", value.toString(), startLine, startColumn));
        }

        private void consumeComment() {
            while (!atEnd() && peekChar() != '\n') consumeChar();
        }
        private char peekChar() { return atEnd() ? '\0' : source.charAt(offset); }
        private char peekNextChar() { return offset + 1 >= source.length() ? '\0' : source.charAt(offset + 1); }
        private char consumeChar() {
            char c = source.charAt(offset++);
            if (c == '\n') { line++; column = 1; } else column++;
            return c;
        }
        private boolean atEnd() { return offset >= source.length(); }
    }

    private static String quote(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + "\"";
    }

    public static final class CompilerException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;
        public CompilerException(String message) { super(message); }
    }
}
