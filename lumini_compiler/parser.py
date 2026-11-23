"""Recursive-descent model parser plus a Pratt expression parser."""
from __future__ import annotations

from dataclasses import dataclass

from .ast_nodes import BinaryNode, CallNode, Expression, NameNode, NumberNode, UnaryNode
from .lexer import Token, tokenize


@dataclass(frozen=True)
class Definition:
    name: str
    kind: str
    expression: Expression
    expression_text: str
    line: int
    source: str | None = None
    evidence_status: str | None = None
    expected_unit: Expression | None = None


@dataclass(frozen=True)
class ModelSource:
    model_id: str
    version: str
    epistemic_status: str
    engine_id: str
    description: str
    definitions: tuple[Definition, ...]
    outputs: tuple[str, ...]


class ParseError(ValueError):
    def __init__(self, message: str, token: Token, code: str = "P001"):
        super().__init__(f"LUMINI {code} at {token.line}:{token.column}: {message}")
        self.line = token.line
        self.column = token.column
        self.code = code


class Parser:
    _PRECEDENCE = {"PLUS": 10, "MINUS": 10, "STAR": 20, "SLASH": 20, "CARET": 30}

    def __init__(self, source: str):
        self.source = source
        self.tokens = tokenize(source)
        self.index = 0

    @property
    def current(self) -> Token:
        return self.tokens[self.index]

    def advance(self) -> Token:
        token = self.current
        if token.kind != "EOF":
            self.index += 1
        return token

    def accept(self, kind: str) -> Token | None:
        if self.current.kind == kind:
            return self.advance()
        return None

    def expect(self, kind: str, message: str | None = None) -> Token:
        if self.current.kind != kind:
            raise ParseError(message or f"expected {kind}, got {self.current.value!r}", self.current)
        return self.advance()

    def expect_word(self, value: str) -> Token:
        token = self.expect("IDENT", f"expected keyword {value!r}")
        if token.value != value:
            raise ParseError(f"expected keyword {value!r}, got {token.value!r}", token)
        return token

    def skip_separators(self) -> None:
        while self.current.kind in ("NEWLINE", "SEMICOLON"):
            self.advance()

    def parse(self) -> ModelSource:
        self.skip_separators()
        self.expect_word("model")
        model_id = self.expect("STRING", "model requires a quoted identifier").value
        self.expect_word("version")
        version = self.expect("STRING", "model version must be quoted").value
        self.expect("LBRACE", "expected '{' after model header")
        self.skip_separators()

        status: str | None = None
        engine_id = "formula.scalar"
        description = ""
        definitions: list[Definition] = []
        outputs: list[str] = []
        seen_names: set[str] = set()
        seen_singletons: set[str] = set()

        while self.current.kind not in ("RBRACE", "EOF"):
            token = self.current
            if token.kind != "IDENT":
                raise ParseError("expected a model statement", token)
            keyword = token.value
            self.advance()

            if keyword in {"status", "engine", "description"}:
                if keyword in seen_singletons:
                    raise ParseError(f"duplicate {keyword!r} declaration", token, "P002")
                seen_singletons.add(keyword)
                if keyword == "status":
                    status = self.expect("IDENT", "status requires FACT, MODEL, HYPOTHESIS, OPEN or FICTION").value
                elif keyword == "engine":
                    engine_id = self.expect("STRING", "engine id must be quoted").value
                else:
                    description = self.expect("STRING", "description must be quoted").value

            elif keyword in {"parameter", "constant", "formula"}:
                name_token = self.expect("IDENT", f"{keyword} requires a name")
                name = name_token.value
                if name in seen_names:
                    raise ParseError(f"duplicate definition {name!r}", name_token, "P003")
                if name in {"status", "engine", "description", "source", "evidence", "output"}:
                    raise ParseError(f"reserved word cannot be used as a definition name: {name!r}", name_token, "P004")
                seen_names.add(name)
                self.expect("EQUALS", f"expected '=' after {keyword} name")
                expression, expression_text = self.parse_expression_with_text(stop_at_colon=True)
                expected_unit = None
                if keyword == "formula" and self.accept("COLON"):
                    expected_unit, _ = self.parse_expression_with_text()
                source_value = None
                evidence_value = None
                while self.current.kind == "IDENT" and self.current.value in {"source", "evidence"}:
                    metadata = self.advance().value
                    if metadata == "source":
                        if source_value is not None:
                            raise ParseError("duplicate source metadata", self.current, "P005")
                        source_value = self.expect("STRING", "source metadata requires a quoted value").value
                    else:
                        if evidence_value is not None:
                            raise ParseError("duplicate evidence metadata", self.current, "P005")
                        evidence_value = self.expect("IDENT", "evidence requires a status label").value
                definitions.append(Definition(
                    name=name, kind=keyword, expression=expression,
                    expression_text=expression_text, line=name_token.line,
                    source=source_value, evidence_status=evidence_value,
                    expected_unit=expected_unit,
                ))

            elif keyword == "output":
                output_token = self.expect("IDENT", "output requires a definition name")
                if output_token.value in outputs:
                    raise ParseError(f"duplicate output {output_token.value!r}", output_token, "P006")
                outputs.append(output_token.value)

            else:
                raise ParseError(f"unknown statement {keyword!r}", token, "P007")

            if self.current.kind not in ("NEWLINE", "SEMICOLON", "RBRACE", "EOF"):
                raise ParseError("unexpected token at end of statement", self.current)
            self.skip_separators()

        self.expect("RBRACE", "model block is not closed; expected '}'")
        self.skip_separators()
        self.expect("EOF", "unexpected content after model block")

        if not model_id.strip() or not version.strip():
            raise ParseError("model id and version must not be empty", self.tokens[0], "P008")
        if status is None:
            raise ParseError("model must declare epistemic status", self.tokens[0], "P009")
        if status not in {"FACT", "MODEL", "HYPOTHESIS", "OPEN", "FICTION"}:
            raise ParseError(f"unsupported epistemic status {status!r}", self.tokens[0], "P010")
        if not definitions:
            raise ParseError("model must contain at least one definition", self.tokens[0], "P011")
        if not outputs:
            raise ParseError("model must declare at least one output", self.tokens[0], "P012")

        return ModelSource(
            model_id=model_id, version=version, epistemic_status=status,
            engine_id=engine_id, description=description,
            definitions=tuple(definitions), outputs=tuple(outputs),
        )

    def parse_expression_with_text(self, stop_at_colon: bool = False) -> tuple[Expression, str]:
        start_offset = self.current.offset
        expression = self.parse_expression(0)
        end_offset = self.tokens[self.index - 1].end_offset
        if stop_at_colon and self.current.kind not in ("COLON", "NEWLINE", "SEMICOLON", "RBRACE", "EOF") and not (self.current.kind == "IDENT" and self.current.value in {"source", "evidence"}):
            raise ParseError("unexpected token in formula expression", self.current)
        return expression, self.source[start_offset:end_offset].strip()

    def parse_expression(self, min_precedence: int = 0) -> Expression:
        token = self.current
        if token.kind == "NUMBER":
            self.advance()
            left: Expression = NumberNode(float(token.value))
        elif token.kind == "IDENT":
            name = self.advance().value
            if self.accept("LPAREN"):
                arguments: list[Expression] = []
                if self.current.kind != "RPAREN":
                    while True:
                        arguments.append(self.parse_expression(0))
                        if not self.accept("COMMA"):
                            break
                self.expect("RPAREN", "expected ')' after function arguments")
                left = CallNode(name, tuple(arguments))
            else:
                left = NameNode(name)
        elif token.kind in ("PLUS", "MINUS"):
            operator = self.advance().value
            left = UnaryNode(operator, self.parse_expression(25))
        elif self.accept("LPAREN"):
            left = self.parse_expression(0)
            self.expect("RPAREN", "expected ')' after expression")
        else:
            raise ParseError("expected a number, name, unary operator or parenthesized expression", token)

        while True:
            operator = self.current
            precedence = self._PRECEDENCE.get(operator.kind, -1)
            if precedence < min_precedence:
                break
            self.advance()
            next_min = precedence if operator.kind == "CARET" else precedence + 1
            right = self.parse_expression(next_min)
            left = BinaryNode(operator.value, left, right)
        return left


def parse_model(source: str) -> ModelSource:
    return Parser(source).parse()
