"""Tokenizer for the deliberately small, safe LUMINI formula language."""
from __future__ import annotations

from dataclasses import dataclass
import json
import re


@dataclass(frozen=True)
class Token:
    kind: str
    value: str
    line: int
    column: int
    offset: int
    end_offset: int


_NUMBER = re.compile(r"(?:\d+(?:\.\d*)?|\.\d+)(?:[eE][+-]?\d+)?")
_IDENT = re.compile(r"[A-Za-z_][A-Za-z0-9_]*")
_SYMBOLS = {
    "{": "LBRACE", "}": "RBRACE", "(": "LPAREN", ")": "RPAREN",
    "=": "EQUALS", "+": "PLUS", "-": "MINUS", "*": "STAR",
    "/": "SLASH", "^": "CARET", ":": "COLON", ",": "COMMA",
    ";": "SEMICOLON",
}


class LexError(ValueError):
    def __init__(self, message: str, line: int, column: int):
        super().__init__(f"LUMINI L001 at {line}:{column}: {message}")
        self.line = line
        self.column = column


def tokenize(source: str) -> list[Token]:
    tokens: list[Token] = []
    index = 0
    line = 1
    column = 1
    length = len(source)

    while index < length:
        char = source[index]

        if char in " \t\r":
            index += 1
            column += 1
            continue

        if char == "\n":
            tokens.append(Token("NEWLINE", "\\n", line, column, index, index + 1))
            index += 1
            line += 1
            column = 1
            continue

        if char == "#" or source.startswith("//", index):
            while index < length and source[index] != "\n":
                index += 1
                column += 1
            continue

        if char == '"':
            start, start_column, start_line = index, column, line
            index += 1
            column += 1
            escaped = False
            while index < length:
                current = source[index]
                if current == "\n" and not escaped:
                    raise LexError("newline inside string literal", start_line, start_column)
                if current == '"' and not escaped:
                    index += 1
                    column += 1
                    raw = source[start:index]
                    try:
                        value = json.loads(raw)
                    except json.JSONDecodeError as exc:
                        raise LexError("invalid string escape", start_line, start_column) from exc
                    tokens.append(Token("STRING", value, start_line, start_column, start, index))
                    break
                if current == "\\" and not escaped:
                    escaped = True
                else:
                    escaped = False
                index += 1
                column += 1
            else:
                raise LexError("unterminated string literal", start_line, start_column)
            continue

        number_match = _NUMBER.match(source, index)
        if number_match:
            value = number_match.group(0)
            end = number_match.end()
            tokens.append(Token("NUMBER", value, line, column, index, end))
            column += end - index
            index = end
            continue

        ident_match = _IDENT.match(source, index)
        if ident_match:
            value = ident_match.group(0)
            end = ident_match.end()
            tokens.append(Token("IDENT", value, line, column, index, end))
            column += end - index
            index = end
            continue

        if char in _SYMBOLS:
            tokens.append(Token(_SYMBOLS[char], char, line, column, index, index + 1))
            index += 1
            column += 1
            continue

        raise LexError(f"unexpected character {char!r}", line, column)

    tokens.append(Token("EOF", "", line, column, length, length))
    return tokens
